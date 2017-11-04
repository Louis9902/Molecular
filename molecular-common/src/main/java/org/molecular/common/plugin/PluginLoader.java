/*
 * This file ("PluginLoader.java") is part of the molecular-project by Louis.
 * Copyright © 2017 Louis
 *
 * The molecular-project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The molecular-project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with molecular-project.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.molecular.common.plugin;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.molecular.api.Molecular;
import org.molecular.api.plugin.PluginPhase;
import org.molecular.api.plugin.event.MPLCompleteEvent;
import org.molecular.api.plugin.event.MPLInitializeEvent;
import org.molecular.api.plugin.event.MPLTerminateEvent;
import org.molecular.api.plugin.event.MPLTransmitEvent;
import org.molecular.common.exception.PluginTransitionException;
import org.molecular.common.plugin.analysis.DataAnalyser;
import org.molecular.common.plugin.analysis.DataWatcher;
import org.molecular.common.plugin.container.AbstractPluginContainer;
import org.molecular.common.plugin.trigger.AssembleTrigger;
import org.molecular.common.plugin.trigger.ComputeTrigger;
import org.molecular.common.plugin.trigger.LoadTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Louis
 */

public final class PluginLoader {

    private final Logger logger = LoggerFactory.getLogger("org.molecular.plugin");

    private final DefaultPluginController controller;

    private final EventBus master;
    private final DataWatcher watcher;

    private final Multimap<String, Throwable> exceptions = ArrayListMultimap.create();
    private final Multimap<String, PluginPhase> phases = ArrayListMultimap.create();

    private final Map<String, AbstractPluginContainer> potentials = new HashMap<>();
    private final Set<AbstractPluginContainer> containers = new HashSet<>();

    private State state = State.NOTHING;

    PluginLoader(DefaultPluginController controller) {
        this.controller = controller;
        this.master = new EventBus();
        this.watcher = new DataWatcher();
        this.master.register(this);
    }

    public void transition(State state) {
        State prev = this.state;
        this.state = this.exceptions.size() > 0 ? State.EXCEPTION : this.state.next();

        if (this.state != state) {
            logger.error("Fatal errors occurred during the transition from {} to {}. Loading can't continue", prev, this.state);

            StringBuilder builder = new StringBuilder();
            {
                builder.append(Strings.repeat("-", 64)).append('\n');
                for (String identifier : this.phases.keySet()) {
                    builder.append(Strings.padEnd(identifier, 64, ' ')).append(" > ");
                    for (PluginPhase phase : this.phases.get(identifier)) {
                        builder.append(phase.name().charAt(0)).append(" ");
                    }
                    builder.append('\n');
                }
                builder.append(Strings.repeat("-", 64)).append('\n');
                for (PluginPhase phase : PluginPhase.values()) {
                    builder.append(phase.getPrintInfo()).append('\n');
                }
                builder.append(Strings.repeat("-", 64)).append('\n');
            }

            logger.error("The plugins had reached the following phases:\n{}", builder.toString());

            logger.error("The following problems were captured during this phase");
            for (Map.Entry<String, Throwable> entry : this.exceptions.entries()) {
                if (entry.getValue() instanceof InvocationTargetException) {
                    logger.error("Caught exception from '{}' while invoking into target", entry.getKey(), entry.getValue().getCause());
                } else {
                    logger.error("Caught exception from '{}'", entry.getKey(), entry.getValue());
                }
            }
            throw new PluginTransitionException("The transition of plugin loader failed");
        }

        state.getInstance().ifPresent(this.master::post);
    }

    @Subscribe
    public void onLoad(LoadTrigger event) {
        Path plugins = Molecular.getResourceController().getPluginDir();
        DataAnalyser.instance().analyse(this.potentials, this.watcher, plugins);
        for (AbstractPluginContainer container : this.potentials.values()) {
            this.phases.put(container.identifier(), PluginPhase.LOADED);
        }
    }

    @Subscribe
    public void onCompute(ComputeTrigger event) {
        this.containers.addAll(this.potentials.values());
        // TODO: 19.10.2017 Implement plugin sorting
        for (AbstractPluginContainer container : this.potentials.values()) {
            this.phases.put(container.identifier(), PluginPhase.COMPUTED);
        }
        this.potentials.clear();
    }

    @Subscribe
    public void onAssemble(AssembleTrigger event) {
        for (AbstractPluginContainer container : this.containers) {
            try {
                this.controller.classpathInvoke(container);
                container.assemble(this);
                this.controller.instanceInvoke(container);
                this.phases.put(container.identifier(), PluginPhase.ASSEMBLED);
            } catch (Throwable throwable) {
                this.phases.put(container.identifier(), PluginPhase.FAILED);
                this.exceptions.put(container.identifier(), throwable);
            }
        }
    }

    @Subscribe
    public void onTransmit(MPLTransmitEvent event) {
        for (AbstractPluginContainer container : this.containers) {
            try {
                logger.trace(">> MPLTransmitEvent {} -> {}", event.getClass(), container.identifier());
                event.accept(container);
                container.transmit(event);
                logger.trace("<< MPLTransmitEvent {} -> {}", event.getClass(), container.identifier());
                this.phases.put(container.identifier(), event.getPhase());
            } catch (Throwable throwable) {
                this.phases.put(container.identifier(), PluginPhase.FAILED);
                this.exceptions.put(container.identifier(), throwable);
            }
        }
    }

    public DataWatcher getWatcher() {
        return this.watcher;
    }

    public ClassLoader getPluginClassLoader() {
        return this.controller.getPluginClassLoader();
    }

    public enum State {

        NOTHING(null),
        LOADING(LoadTrigger.class),
        COMPUTING(ComputeTrigger.class),
        ASSEMBLY(AssembleTrigger.class),
        INITIALIZE(MPLInitializeEvent.class),
        COMPLETE(MPLCompleteEvent.class),
        TERMINATE(MPLTerminateEvent.class),
        EXCEPTION(null);

        private final Class<?> clazz;

        State(@Nullable Class<?> clazz) {
            this.clazz = clazz;
        }

        public Optional<Object> getInstance() {
            try {
                return this.clazz == null ? Optional.empty() : Optional.ofNullable(this.clazz.newInstance());
            } catch (Exception e) {
                return Optional.empty();
            }
        }

        public State next() {
            return State.values()[ordinal() < State.values().length ? ordinal() + 1 : ordinal()];
        }

        public State last() {
            return this == NOTHING ? NOTHING : State.values()[this.ordinal() - 1];
        }

    }
}
