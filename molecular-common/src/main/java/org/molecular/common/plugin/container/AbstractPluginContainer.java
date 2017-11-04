/*
 * This file ("AbstractPluginContainer.java") is part of the molecular-project by Louis.
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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.molecular.api.plugin.PluginContainer;
import org.molecular.api.plugin.event.MPLTransmitEvent;
import org.molecular.api.plugin.meta.PluginMetadata;
import org.molecular.common.plugin.PluginLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Louis
 */

public abstract class AbstractPluginContainer implements PluginContainer {

    private final Logger logger;
    private final PluginMetadata metadata;

    protected AbstractPluginContainer(@Nonnull PluginMetadata metadata) {
        this.logger = LoggerFactory.getLogger(metadata.identifier());
        this.metadata = metadata;
    }

    public abstract void assemble(@Nonnull PluginLoader loader) throws Exception;

    public abstract void transmit(@Nonnull MPLTransmitEvent event) throws Exception;

    @Nonnull
    @Override
    public String identifier() {
        return this.metadata.identifier();
    }

    @Nonnull
    @Override
    public String name() {
        return this.metadata.name();
    }

    @Nonnull
    @Override
    public String version() {
        return this.metadata.version();
    }

    @Nonnull
    @Override
    public Optional<String> description() {
        return this.metadata.description();
    }

    @Nonnull
    @Override
    public Optional<String> website() {
        return this.metadata.website();
    }

    @Nonnull
    @Override
    public Logger logger() {
        return this.logger;
    }

    @Nonnull
    public PluginMetadata metadata() {
        return this.metadata;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.identifier(), this.source(), this.instance());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractPluginContainer that = (AbstractPluginContainer) o;
        return Objects.equal(this.identifier(), that.identifier()) &&
                Objects.equal(this.source(), that.source()) &&
                Objects.equal(this.instance(), that.instance());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("identifier", this.identifier())
                .add("version", this.version())
                .toString();
    }
}
