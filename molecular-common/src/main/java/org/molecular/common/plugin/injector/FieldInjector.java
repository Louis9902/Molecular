/*
 * This file ("FieldInjector.java") is part of the molecular-project by Louis.
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

package org.molecular.common.plugin.injector;

import org.molecular.api.platform.Platform;
import org.molecular.api.plugin.PluginContainer;
import org.molecular.common.plugin.PluginLoader;
import org.molecular.common.plugin.analysis.DataTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Louis
 */

public class FieldInjector {

    public static final FieldInjector INSTANCE = new FieldInjector();

    private final Logger logger = LoggerFactory.getLogger("org.molecular.plugin");
    private final EnumMap<Platform, Map<Class<? extends Annotation>, Object>> mapping;

    private FieldInjector() {
        this.mapping = new EnumMap<>(Platform.class);
        for (Platform platform : Platform.values()) {
            this.mapping.put(platform, new Hashtable<>());
        }
    }

    public void register(Platform platform, Class<? extends Annotation> clazz, Object value) {
        this.mapping.get(platform).put(clazz, value);
    }

    public void inject(PluginContainer container, PluginLoader loader, Platform platform) {
        Map<Class<? extends Annotation>, Object> annotations = this.mapping.get(platform);

        for (Class<? extends Annotation> annotation : annotations.keySet()) {
            logger.trace("Attempting to inject @{} classes into {}", annotation.getSimpleName(), container.identifier());
            Collection<DataTrace> instances = loader.getWatcher().getAnnotationsDistrict(annotation, container);

            for (DataTrace trace : instances) {
                try {
                    Class<?> clazz = Class.forName(trace.clazz, true, loader.getPluginClassLoader());
                    Field field = clazz.getDeclaredField(trace.member);

                    if (field == null) {
                        logger.error("FieldInjector could not find target field for value injection [class:{}, field:{}]", trace.clazz, trace.member);
                        continue;
                    }

                    field.setAccessible(true);

                    boolean isStatic = Modifier.isStatic(field.getModifiers());
                    boolean mustBeStatic = !clazz.isInstance(container.instance().orElse(null));

                    if (mustBeStatic && !isStatic) {
                        logger.error("FieldInjector could not inject value in non static field if static is required [not located in plugin instance]");
                        continue;
                    }

                    Object value = annotations.get(annotation);

                    if (!field.getType().isAssignableFrom(value.getClass())) {
                        logger.error("FieldInjector could not inject value because of non matching field type");
                        continue;
                    }

                    if (!isStatic && !container.instance().isPresent()) {
                        logger.error("FieldInjector could not inject value in non static field if plugin instance of {} is missing", container.identifier());
                        continue;
                    }

                    field.set(isStatic ? null : container.instance().get(), value);
                } catch (Throwable throwable) {
                    logger.error("An error occurred trying to inject a field value into {}.{}", trace.clazz, trace.member, throwable);
                }
            }
        }
    }
}
