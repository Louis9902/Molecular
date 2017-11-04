/*
 * This file ("PluginContainerFactory.java") is part of the molecular-project by Louis.
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

import org.molecular.api.plugin.Plugin;
import org.molecular.api.plugin.meta.PluginMetadata;
import org.molecular.common.plugin.analysis.asm.ASMAnnotation;
import org.molecular.common.plugin.analysis.asm.ASMAnnotationStore;
import org.molecular.common.plugin.analysis.source.PluginSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Louis
 */

public final class PluginContainerFactory {

    private static final PluginContainerFactory instance = new PluginContainerFactory();

    private final Logger logger = LoggerFactory.getLogger("org.molecular.plugin");
    private final Map<String, Constructor<? extends AbstractPluginContainer>> mapping;

    private PluginContainerFactory() {
        this.mapping = new HashMap<>();
        this.registerType(Plugin.class, DefaultPluginContainer.class);
    }

    public static PluginContainerFactory instance() {
        return PluginContainerFactory.instance;
    }

    public Optional<AbstractPluginContainer> build(@Nonnull PluginSource source, @Nonnull ASMAnnotationStore store) {
        for (ASMAnnotation target : store) {
            String annotation = target.annotation.getClassName();
            if (this.mapping.containsKey(annotation)) {
                logger.trace("Found container for plugin class {} ({})", store.getClassName(), annotation);

                Constructor<? extends AbstractPluginContainer> constructor = this.mapping.get(annotation);

                try {
                    PluginMetadata metadata = PluginMetadata.create(target.properties);
                    return Optional.of(constructor.newInstance(source, metadata, store.getClassName()));
                } catch (Exception e) {
                    logger.error("Cannot assembly plugin container for {} ({})", store.getClassName(), annotation);
                    logger.debug("PluginContainer assembly failed because of ", e);
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    private void registerType(Class<? extends Annotation> clazz, Class<? extends AbstractPluginContainer> container) throws Error {
        try {
            this.mapping.put(clazz.getName(), container.getConstructor(PluginSource.class, PluginMetadata.class, String.class));
        } catch (NoSuchMethodException e) {
            throw new Error("Cannot register plugin container type " + clazz.getName(), e);
        }
    }


}
