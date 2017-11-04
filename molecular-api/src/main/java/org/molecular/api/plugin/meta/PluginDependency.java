/*
 * This file ("PluginDependency.java") is part of the molecular-project by Louis.
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

package org.molecular.api.plugin.meta;

import com.google.common.base.Splitter;
import org.molecular.api.util.Identifiable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Louis
 */

public final class PluginDependency implements Identifiable<String> {

    private static final Splitter DEPENDENCY_SPLITTER = Splitter.on(';').omitEmptyStrings().trimResults();
    private static final Splitter DEPENDENCY_PART_SPLITTER = Splitter.on(':').omitEmptyStrings().trimResults();
    private static final Splitter DEPENDENCY_VERSION_SPLITTER = Splitter.on('@').omitEmptyStrings().trimResults();


    private String identifier;
    private String version;
    private LoadOrder order;
    private boolean optional;

    private PluginDependency(String identifier, String version, LoadOrder order, boolean optional) {
        this.identifier = identifier;
        this.version = version;
        this.order = order;
        this.optional = optional;
    }

    public static Map<String, PluginDependency> computeDependencies(@Nonnull String str) {
        Map<String, PluginDependency> dependencies = new HashMap<>();
        for (String dependency : DEPENDENCY_SPLITTER.split(str)) {
            List<String> parts = DEPENDENCY_PART_SPLITTER.splitToList(dependency);

            if (parts.size() != 2) {
                throw new IllegalArgumentException("invalid dependency format for: " + dependency);
            }

            String instruction = parts.get(0);
            String target = parts.get(1);

            List<String> info = DEPENDENCY_VERSION_SPLITTER.splitToList(target);

            if (info.size() != 2) {
                throw new IllegalArgumentException("invalid dependency format for: " + dependency);
            }

            String identifier = info.get(0);
            String version = info.get(1);

            boolean required = instruction.contains("required");
            if ("required-before".equals(instruction) || "before".equals(instruction)) {
                dependencies.put(identifier, new PluginDependency(identifier, version, LoadOrder.BEFORE, !required));
                continue;
            }
            if ("required-after".equals(instruction) || "after".equals(instruction)) {
                dependencies.put(identifier, new PluginDependency(identifier, version, LoadOrder.AFTER, !required));
                continue;
            }

            throw new IllegalArgumentException("invalid dependency format for: " + dependency);
        }
        return dependencies;
    }

    @Nonnull
    @Override
    public String identifier() {
        return this.identifier;
    }

    @Nonnull
    public String version() {
        return this.version;
    }

    @Nonnull
    public LoadOrder order() {
        return this.order;
    }

    public boolean optional() {
        return this.optional;
    }

    public enum LoadOrder {

        /**
         * The dependency should be loaded <b>before</b> the plugin.
         */
        BEFORE,

        /**
         * The dependency should be loaded <b>after</b> the plugin.
         */
        AFTER

    }
}
