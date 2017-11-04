/*
 * This file ("SourceType.java") is part of the molecular-project by Louis.
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

package org.molecular.common.plugin.analysis.source;

import org.molecular.common.plugin.analysis.DataWatcher;
import org.molecular.common.plugin.analysis.source.valuation.DirValuation;
import org.molecular.common.plugin.analysis.source.valuation.JarValuation;
import org.molecular.common.plugin.analysis.source.valuation.SourceValuation;
import org.molecular.common.plugin.container.AbstractPluginContainer;
import org.molecular.common.plugin.container.PluginContainerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Louis
 */

public enum SourceType {

    JAR(JarValuation.class),
    DIR(DirValuation.class);

    public static final Pattern ID_PATTERN = Pattern.compile("[a-z][a-z0-9-_]{0,63}");

    private final SourceValuation valuation;

    SourceType(Class<? extends SourceValuation> clazz) {
        try {
            this.valuation = clazz.newInstance();
        } catch (Exception e) {
            throw new Error("Unable to create SourceValuation type for " + this, e);
        }
    }

    public List<AbstractPluginContainer> evaluate(@Nonnull PluginSource source, @Nonnull DataWatcher watcher) {
        return this.valuation.evaluate(source, watcher, PluginContainerFactory.instance());
    }
}
