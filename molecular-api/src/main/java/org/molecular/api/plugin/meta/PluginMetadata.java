/*
 * This file ("PluginMetadata.java") is part of the molecular-project by Louis.
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

import org.molecular.api.util.Identifiable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;

/**
 * @author Louis
 */

public final class PluginMetadata implements Identifiable<String> {

    @Nonnull
    private String identifier, name, version;
    @Nonnull
    private Optional<String> description, website, dependencies;

    private Collection<PluginDependency> dependencies_;

    private PluginMetadata(@Nonnull String identifier, @Nonnull String name, @Nonnull String version) {
        this.identifier = identifier;
        this.name = name;
        this.version = version;
        this.description = Optional.empty();
        this.website = Optional.empty();
        this.dependencies = Optional.empty();
    }

    private PluginMetadata(@Nonnull String... strings) {
        checkArgument(strings.length == 6, "metadata args out of bounds");
        this.identifier = strings[0];
        this.name = strings[1];
        this.version = strings[2];
        this.description = Optional.ofNullable(strings[3]);
        this.website = Optional.ofNullable(strings[4]);
        this.dependencies = Optional.ofNullable(strings[5]);
    }

    //<editor-fold desc="Creators">
    public static PluginMetadata create(@Nonnull String id) throws NullPointerException {
        String id_ = checkNotNull(emptyToNull(id));
        return new PluginMetadata(id_, id_, "unknown");
    }

    public static PluginMetadata create(@Nonnull String id, @Nonnull String name, @Nonnull String version) throws NullPointerException {
        String id_ = checkNotNull(emptyToNull(id));
        String name_ = firstNonNull(emptyToNull(name), id_);
        String version_ = firstNonNull(emptyToNull(version), "unknown");
        return new PluginMetadata(id_, name_, version_);
    }

    public static PluginMetadata create(@Nonnull Map<String, Object> descriptor) throws NullPointerException {
        String id_ = checkNotNull(emptyToNull((String) descriptor.get("id")));
        String name_ = firstNonNull(emptyToNull((String) descriptor.get("name")), id_);
        String version_ = firstNonNull(emptyToNull((String) descriptor.get("version")), "unknown");
        String description_ = emptyToNull((String) descriptor.get("description"));
        String website_ = emptyToNull((String) descriptor.get("website"));
        String dependencies_ = emptyToNull((String) descriptor.get("dependencies"));
        return new PluginMetadata(id_, name_, version_, description_, website_, dependencies_);
    }

    //</editor-fold>

    @Nonnull
    @Override
    public String identifier() {
        return this.identifier;
    }

    @Nonnull
    public String name() {
        return this.name;
    }

    @Nonnull
    public String version() {
        return this.version;
    }

    @Nonnull
    public Optional<String> description() {
        return this.description;
    }

    @Nonnull
    public Optional<String> website() {
        return this.website;
    }

    @Nonnull
    public Optional<String> dependencies() {
        return this.dependencies;
    }

    public Collection<PluginDependency> getDependencies() {
        return this.dependencies_;
    }

}
