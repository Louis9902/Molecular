/*
 * This file ("Namespace.java") is part of the molecular-project by Louis.
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

package org.molecular.api.resources;

import org.molecular.api.plugin.PluginContainer;

/**
 * @author Louis
 */

public final class Namespace {

    private final String resourceDomain;
    private final String resourcePath;

    public Namespace(PluginContainer container, String path) {
        this.resourceDomain = container.identifier();
        this.resourcePath = path;
    }

    public Namespace(String domain, String path) {
        this.resourceDomain = domain;
        this.resourcePath = path;
    }

    public String getResourceDomain() {
        return this.resourceDomain;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public String toString() {
        return this.resourceDomain + ':' + this.resourcePath;
    }

}
