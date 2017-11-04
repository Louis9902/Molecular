/*
 * This file ("Internationalization.java") is part of the molecular-project by Louis.
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

package org.molecular.common.resource;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.CharStreams;
import org.molecular.api.plugin.PluginContainer;
import org.molecular.api.util.APIInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Louis
 */

public final class Internationalization {

    private static final Internationalization instance = new Internationalization();

    private final Logger logger = LoggerFactory.getLogger("org.molecular.util");
    private final Splitter splitter = Splitter.on('=').limit(2).omitEmptyStrings();

    private final Map<String, String> properties;

    private Internationalization() {
        this.properties = new HashMap<>();
    }

    public static Internationalization instance() {
        return Internationalization.instance;
    }

    public String localize(String key, Object... parameters) {
        try {
            return String.format(this.properties.getOrDefault(key, key), parameters);
        } catch (IllegalFormatException e) {
            return "FormatException-<" + key + ">";
        }
    }

    @APIInternal
    public void inject(PluginContainer container, Locale locale) {
        Optional<Path> source = container.source();

        if (!source.isPresent()) {
            return;
        }

        String resource = String.format("assets/%s/lang/%s.lang", container.identifier(), locale.toLanguageTag());

        if (Files.isRegularFile(source.get())) {
            try (JarFile file = new JarFile(new File(source.get().toUri()))) {
                JarEntry entry = file.getJarEntry(resource);
                if (entry != null) {
                    this.readStream(file.getInputStream(entry));
                } else {
                    throw new FileNotFoundException(resource + " @ " + source.get());
                }
            } catch (IOException e) {
                logger.warn("Failed to check jar file {} for resource {}", source.get(), resource, e);
            }
        }

        if (Files.isDirectory(source.get())) {
            // TODO: 31.10.2017 add support for dir plugin resource
        }

    }

    private void readStream(InputStream stream) throws IOException {
        for (String line : CharStreams.readLines(new InputStreamReader(stream, Charsets.UTF_8))) {
            if (line != null && line.charAt(0) != '#') {
                String[] strings = this.splitter.splitToList(line).toArray(new String[0]);
                this.properties.putIfAbsent(strings[0], strings[1]);
            }
        }
    }
}
