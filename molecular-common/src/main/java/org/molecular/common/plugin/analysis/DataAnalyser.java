/*
 * This file ("DataAnalyser.java") is part of the molecular-project by Louis.
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

package org.molecular.common.plugin.analysis;

import org.molecular.common.plugin.analysis.source.PluginSource;
import org.molecular.common.plugin.analysis.source.SourceType;
import org.molecular.common.plugin.container.AbstractPluginContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Louis
 */

public final class DataAnalyser {

    private static final DataAnalyser instance = new DataAnalyser();

    private final Logger logger;
    private final Marker marker;

    private DataAnalyser() {
        this.logger = LoggerFactory.getLogger("org.molecular.plugin");
        this.marker = MarkerFactory.getMarker("TIMING");
    }

    public static DataAnalyser instance() {
        return DataAnalyser.instance;
    }

    public void analyse(@Nonnull Map<String, AbstractPluginContainer> cache, @Nonnull DataWatcher watcher, @Nonnull Path directory) {
        List<PluginSource> sources = new ArrayList<>();

        try {
            if (!Files.isDirectory(directory)) {
                Files.createDirectory(directory);
            }
        } catch (IOException e) {
            logger.error("Failed to create folder for plugins ({})", directory, e);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, entry -> entry.toString().endsWith(".jar"))) {
            for (Path path : stream) {
                sources.add(new PluginSource(path, SourceType.JAR));
                logger.debug("Found plugin file of type jar [{}]", path);
            }
        } catch (IOException e) {
            logger.error("Failed to analyse plugin directory {}", directory, e);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, entry -> Files.isDirectory(entry))) {
            for (Path path : stream) {
                sources.add(new PluginSource(path, SourceType.DIR));
                logger.debug("Found plugin file of type dir [{}]", path);
            }
        } catch (IOException e) {
            logger.error("Failed to analyse plugin directory {}", directory, e);
        }

        logger.info("Found {} potential file(s) to load at specified directory", sources.size());

        for (PluginSource source : sources) {

            long start = System.currentTimeMillis();
            List<AbstractPluginContainer> containers = source.evaluate(watcher);
            long stop = System.currentTimeMillis();

            int count = containers.size();
            logger.debug(marker, "Scanned plugin file {} in {}ms, found {} plugin{}", source.source(), (stop - start), count, (count > 1 ? "s" : ""));

            for (AbstractPluginContainer container : containers) {
                String identifier = container.identifier();

                if (!SourceType.ID_PATTERN.matcher(identifier).find()) {
                    logger.warn("Skipping plugin with invalid plugin identifier '{}' [{}]", identifier, container.source().orElse(null));
                    continue;
                }

                if (cache.containsKey(identifier)) {
                    logger.warn("Skipping plugin with duplicate plugin identifier '{}' [{}]", identifier, container.source().orElse(null));
                    continue;
                }

                cache.put(identifier, container);
            }
        }
    }
}
