/*
 * This file ("DataWatcher.java") is part of the molecular-project by Louis.
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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import org.molecular.api.plugin.PluginContainer;
import org.molecular.common.plugin.analysis.source.PluginSource;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Louis
 */

public final class DataWatcher implements Iterable<DataTrace> {

    private final Multimap<String, DataTrace> global;
    private final Table<PluginContainer, String, Set<DataTrace>> district;

    public DataWatcher() {
        this.global = HashMultimap.create();
        this.district = HashBasedTable.create();
    }

    //searches the plugin by comparing the class of the annotation trace with the class of the plugin annotation trace
    public static Optional<String> getOwnerPlugin(Collection<DataTrace> plugins, DataTrace traceOfComparison) {
        for (DataTrace traceOfPlugin : plugins) {
            if (traceOfPlugin.clazz.equals(traceOfComparison.clazz)) {
                return Optional.of((String) traceOfPlugin.descriptor.get("id"));
            }
        }
        return Optional.empty();
    }

    public void insert(PluginSource source, String annotation, String clazz, String member, Map<String, Object> descriptor) {
        this.global.put(annotation, new DataTrace(source, annotation, clazz, member, descriptor));
    }

    public Collection<DataTrace> getAnnotationsGlobal(Class<? extends Annotation> clazz) {
        return this.global.get(clazz.getName());
    }

    public Collection<DataTrace> getAnnotationsDistrict(Class<? extends Annotation> clazz, PluginContainer container) {
        return this.district.column(clazz.getName()).computeIfAbsent(container, cont -> this.collectParts(clazz, cont));
    }

    private Set<DataTrace> collectParts(Class<? extends Annotation> clazz, PluginContainer container) {
        if (container.source().isPresent()) {
            Path source = container.source().get();
            return this.getAnnotationsGlobal(clazz).stream()
                    .filter(part -> source.equals(part.source.source()))
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    @Override
    public Iterator<DataTrace> iterator() {
        return this.global.values().iterator();
    }
}
