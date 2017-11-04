/*
 * This file ("DefaultPluginContainer.java") is part of the molecular-project by Louis.
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

package org.molecular.common.plugin.container;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.molecular.api.Molecular;
import org.molecular.api.platform.Platform;
import org.molecular.api.plugin.Plugin;
import org.molecular.api.plugin.event.MPLTransmitEvent;
import org.molecular.api.plugin.meta.PluginMetadata;
import org.molecular.common.event.MolecularEventFactory;
import org.molecular.common.plugin.PluginLoader;
import org.molecular.common.plugin.analysis.source.PluginSource;
import org.molecular.common.plugin.injector.FieldInjector;
import org.molecular.common.plugin.injector.PluginInstanceInjector;
import org.molecular.common.plugin.injector.PluginMetadataInjector;
import org.molecular.common.plugin.injector.ProxyInjector;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author Louis
 */

public class DefaultPluginContainer extends AbstractPluginContainer {

    private final Multimap<Class<?>, Method> events;

    private final Path source;
    private final String clazz;

    private Object instance;
    private PluginLoader loader;

    public DefaultPluginContainer(@Nonnull PluginSource source, @Nonnull PluginMetadata metadata, @Nonnull String clazz) {
        super(metadata);
        this.events = ArrayListMultimap.create();
        this.source = source.source();
        this.clazz = clazz;
        this.instance = Optional.empty();
    }

    @Override
    public void assemble(@Nonnull PluginLoader loader) throws Exception {
        Platform platform = Molecular.getHandler().getPlatform();

        Class<?> clazz = Class.forName(this.clazz, true, loader.getPluginClassLoader());

        this.gatherEventHandler(clazz);

        this.loader = loader;
        this.instance = clazz.newInstance();

        PluginInstanceInjector.INSTANCE.inject(this, loader);
        PluginMetadataInjector.INSTANCE.inject(this, loader);

        ProxyInjector.INSTANCE.inject(this, loader, platform);
        FieldInjector.INSTANCE.inject(this, loader, platform);

        MolecularEventFactory.instance().callPluginAssemblyEvent(this);
    }

    @Override
    public void transmit(@Nonnull MPLTransmitEvent event) throws Exception {
        for (Method method : this.events.get(event.getClass())) {
            method.invoke(this.instance, event);
        }
    }

    @Nonnull
    @Override
    public Optional<Path> source() {
        return Optional.of(this.source);
    }

    @Nonnull
    @Override
    public Optional<Object> instance() {
        return Optional.ofNullable(this.instance);
    }

    private void gatherEventHandler(@Nonnull Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {

            if (!Modifier.isStatic(method.getModifiers())) {
                if (method.isAnnotationPresent(Plugin.EventHandler.class)) {
                    Class<?>[] types = method.getParameterTypes();
                    if (types.length == 1 && MPLTransmitEvent.class.isAssignableFrom(types[0])) {

                        method.setAccessible(true);
                        this.events.put(method.getParameterTypes()[0], method);
                    }
                }
            }

        }
    }
}
