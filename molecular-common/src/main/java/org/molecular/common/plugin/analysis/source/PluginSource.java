/*
 * This file ("PluginSource.java") is part of the molecular-project by Louis.
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

import com.google.common.base.MoreObjects;
import org.molecular.common.plugin.analysis.DataWatcher;
import org.molecular.common.plugin.container.AbstractPluginContainer;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Louis
 */

public final class PluginSource {

    private final Path source;
    private final SourceType sourceType;

    public PluginSource(@Nonnull Path source, @Nonnull SourceType sourceType) {
        this.source = source;
        this.sourceType = sourceType;
    }

    @Nonnull
    public Path source() {
        return this.source;
    }

    public List<AbstractPluginContainer> evaluate(DataWatcher watcher) {
        return this.sourceType.evaluate(this, watcher);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("source", this.source)
                .add("sourceType", this.sourceType)
                .toString();
    }
}
