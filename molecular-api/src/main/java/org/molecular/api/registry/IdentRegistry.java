/*
 * This file ("IdentRegistry.java") is part of the molecular-project by Louis.
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

package org.molecular.api.registry;

import org.molecular.api.exception.RegistryException;
import org.molecular.api.util.Acceptable;
import org.molecular.api.util.Identifiable;

import javax.annotation.Nonnull;

/**
 * @author Louis
 */

public class IdentRegistry<K, V extends Identifiable<K>> extends AbstractRegistry<K, V> {

    public IdentRegistry(@Nonnull String name) {
        this(name, Acceptable.ANY());
    }

    public IdentRegistry(@Nonnull String name, @Nonnull Acceptable<K, V> acceptable) {
        super(name, acceptable);
    }

    public void register(@Nonnull V value) throws RegistryException {
        this.register(value.identifier(), value);
    }

    public V getValue(@Nonnull Identifiable<K> key) {
        return this.getValue(key.identifier());
    }

    public boolean containsKey(@Nonnull Identifiable<K> key) {
        return this.containsKey(key.identifier());
    }

}
