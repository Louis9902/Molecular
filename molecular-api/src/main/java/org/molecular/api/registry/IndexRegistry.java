/*
 * This file ("IndexRegistry.java") is part of the molecular-project by Louis.
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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.stream.IntStream;

/**
 * @author Louis
 */
public class IndexRegistry<V> extends AbstractRegistry<Integer, V> {

    private final boolean autoIndex;

    public IndexRegistry(@Nonnull String name) {
        this(name, false);
    }

    public IndexRegistry(@Nonnull String name, boolean autoIndex) {
        super(name, (key, value) -> (key >= 0 && key <= Byte.MAX_VALUE));
        this.autoIndex = autoIndex;
    }

    public IndexRegistry(@Nonnull String name, @Nonnull Acceptable<Integer, V> acceptable) {
        this(name, false, acceptable);
    }

    public IndexRegistry(@Nonnull String name, boolean autoIndex, @Nonnull Acceptable<Integer, V> acceptable) {
        super(name, acceptable);
        this.autoIndex = autoIndex;
    }

    public void register(@Nonnull V value) throws RegistryException {
        this.register(-1, value);
    }

    @Override
    public void register(@Nonnegative @Nonnull Integer key, @Nonnull V value) throws RegistryException {
        super.register(this.autoIndex ? this.nextFreeIndex() : key, value);
    }

    public int nextFreeIndex() {
        return IntStream.rangeClosed(0, this.size()).filter(i -> !this.containsKey(i)).findFirst().orElse(-1);
    }

    public boolean isAutoIndexed() {
        return this.autoIndex;
    }
}
