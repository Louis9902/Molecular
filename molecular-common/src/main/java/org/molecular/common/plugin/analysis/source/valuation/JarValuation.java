/*
 * This file ("JarValuation.java") is part of the molecular-project by Louis.
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

package org.molecular.common.plugin.analysis.source.valuation;

import org.molecular.common.plugin.analysis.DataWatcher;
import org.molecular.common.plugin.analysis.asm.ASMAnnotationStore;
import org.molecular.common.plugin.container.AbstractPluginContainer;
import org.molecular.common.plugin.container.PluginContainerFactory;
import org.molecular.common.plugin.analysis.source.PluginSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author Louis
 */

public class JarValuation implements SourceValuation {

    private final Logger logger = LoggerFactory.getLogger("org.molecular.plugin");

    @Override
    public List<AbstractPluginContainer> evaluate(@Nonnull PluginSource source, @Nonnull DataWatcher watcher, @Nonnull PluginContainerFactory factory) {
        logger.debug("Checking jar file {} for potential plugins", source.source());
        List<AbstractPluginContainer> containers = new ArrayList<>();

        try (JarFile file = new JarFile(new File(source.source().toUri()))) {
            for (ZipEntry entry : Collections.list(file.entries())) {

                if (entry.getName().endsWith(".class")) {
                    ASMAnnotationStore store;

                    try {
                        try (InputStream stream = file.getInputStream(entry)) {
                            store = new ASMAnnotationStore(stream);
                        }
                    } catch (Throwable throwable) {
                        logger.error("A problem occurred while reading entry from {}", source.source(), throwable);
                        file.close();
                        throw throwable;
                    }

                    store.transfer(watcher, source);
                    factory.build(source, store).ifPresent(containers::add);
                } else {
                    logger.trace("Found non class file {} in jar {}", entry.getName(), source.source());
                }

            }
        } catch (IOException e) {
            logger.warn("Failed to check jar file {} properly for plugins", source.source(), e);
        }

        return containers;
    }
}
