/*
 * This file ("DynamicClassLoader.java") is part of the molecular-project by Louis.
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

package org.molecular.common.internal;

import org.molecular.common.util.CommonInternal;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 * @author Louis
 */
@CommonInternal
public class DynamicClassLoader extends URLClassLoader {

    public DynamicClassLoader() {
        super(new URL[0]);
    }

    public boolean addPath(@Nonnull Path path) {
        try {
            this.addURL(path.toUri().toURL());
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
