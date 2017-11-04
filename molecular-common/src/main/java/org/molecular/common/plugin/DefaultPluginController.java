/*
 * This file ("DefaultPluginController.java") is part of the molecular-project by Louis.
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

import org.molecular.api.platform.PlatformHandler;
import org.molecular.api.plugin.PluginContainer;
import org.molecular.api.plugin.PluginController;
import org.molecular.common.MolecularHandler;
import org.molecular.common.exception.PluginTransitionException;
import org.molecular.common.internal.DynamicClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author Louis
 */
public class DefaultPluginController implements PluginController {

    private final Logger logger = LoggerFactory.getLogger("org.molecular.plugin");

    private final PlatformHandler handler;
    private final DynamicClassLoader classLoader;
    private final PluginLoader loader;

    private final Map<String, PluginContainer> container_str;
    private final Map<Object, PluginContainer> container_obj;

    public DefaultPluginController(PlatformHandler handler) {
        this.handler = handler;
        this.classLoader = new DynamicClassLoader();
        this.loader = new PluginLoader(this);
        this.container_str = new HashMap<>();
        this.container_obj = new HashMap<>();
    }

    /**
     * Searches for potential plugins.
     * These plugins will be loaded and assembled as well as sorted after their dependencies.
     * This will also call {@link MolecularHandler#readResource(PluginContainer)} after the container is constructed and
     * loaded to the {@link PluginController}.
     * These steps are equivalent to:
     * <p>
     * {@link PluginLoader.State#LOADING}<br>
     * {@link PluginLoader.State#COMPUTING}<br>
     * {@link PluginLoader.State#ASSEMBLY}<br>
     *
     * @throws PluginTransitionException if an error occurred during these steps
     */
    public void loading() throws PluginTransitionException {
        this.loader.transition(PluginLoader.State.LOADING);     //  load
        this.loader.transition(PluginLoader.State.COMPUTING);   //  sort
        this.loader.transition(PluginLoader.State.ASSEMBLY);    //  build
    }

    /**
     * Calls the {@link org.molecular.api.plugin.event.MPLInitializeEvent} event listener in the plugin main class.
     * The listener must be annotated with {@link org.molecular.api.plugin.Plugin.EventHandler}.
     * This step is equivalent to:
     * <p>
     * {@link PluginLoader.State#INITIALIZE}
     *
     * @throws PluginTransitionException if an error occurred during these steps
     */
    public void initialize() throws PluginTransitionException {
        this.loader.transition(PluginLoader.State.INITIALIZE);
    }

    /**
     * Calls the {@link org.molecular.api.plugin.event.MPLCompleteEvent} event listener in the plugin main class.
     * The listener must be annotated with {@link org.molecular.api.plugin.Plugin.EventHandler}.
     * This step is equivalent to:
     * <p>
     * {@link PluginLoader.State#COMPLETE}
     *
     * @throws PluginTransitionException if an error occurred during these steps
     */
    public void complete() throws PluginTransitionException {
        this.loader.transition(PluginLoader.State.COMPLETE);
    }

    /**
     * Calls the {@link org.molecular.api.plugin.event.MPLTerminateEvent} event listener in the plugin main class.
     * The listener must be annotated with {@link org.molecular.api.plugin.Plugin.EventHandler}.
     * This step is equivalent to:
     * <p>
     * {@link PluginLoader.State#TERMINATE}
     *
     * @throws PluginTransitionException if an error occurred during these steps
     */
    public void terminate() throws PluginTransitionException {
        this.loader.transition(PluginLoader.State.TERMINATE);
    }

    @Override
    public Optional<PluginContainer> getPlugin(String identifier) {
        return Optional.ofNullable(this.container_str.get(identifier));
    }

    @Override
    public Optional<PluginContainer> getPlugin(Object instance) {
        return Optional.ofNullable(this.container_obj.get(instance));
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return this.container_str.values();
    }

    @Override
    public ClassLoader getPluginClassLoader() {
        return this.classLoader;
    }

    void classpathInvoke(@Nonnull PluginContainer container) throws IllegalStateException {
        Optional<Path> optional = container.source();
        checkState(optional.isPresent(), "plugin source null");

        logger.trace("classpath -> {} @ plugin controller", container.identifier());

        if (!this.classLoader.addPath(optional.get())) {
            throw new IllegalStateException("Unable to inject source into classpath");
        }
    }

    void instanceInvoke(@Nonnull PluginContainer container) throws IllegalStateException {
        Optional<Object> optional = container.instance();
        checkState(optional.isPresent(), "plugin instance null");

        logger.trace("instance -> {} @ plugin controller", container.identifier());

        this.container_str.put(container.identifier(), container);
        this.container_obj.put(optional.get(), container);
        MolecularHandler.instance().readResource(container);
    }
}
