/*
 * This file ("DefaultResourceController.java") is part of the molecular-project by Louis.
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

import org.molecular.api.platform.PlatformHandler;
import org.molecular.api.resources.ResourceController;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * @author Louis
 */
public class DefaultResourceController implements ResourceController {

    private final PlatformHandler handler;
    private final Path root;

    public DefaultResourceController(@Nonnull PlatformHandler handler) {
        this.handler = handler;
        this.root = handler.getApplication().getRootPath();
    }

    @Override
    public Path getPluginDir() {
        return this.root.resolve("plugins");
    }

    @Override
    public Path getPersistentFile() {
        return this.root.resolve("persistent.bin");
    }
}
