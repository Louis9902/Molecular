/*
 * This file ("DataTrace.java") is part of the molecular-project by Louis.
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

import java.util.Map;

/**
 * @author Louis
 */

public final class DataTrace {

    public final PluginSource source;
    public final String annotation;
    public final String clazz;
    public final String member;
    public final Map<String, Object> descriptor;

    public DataTrace(PluginSource source, String annotation, String clazz, String member, Map<String, Object> descriptor) {
        this.source = source;
        this.annotation = annotation;
        this.clazz = clazz;
        this.member = member;
        this.descriptor = descriptor;
    }
}
