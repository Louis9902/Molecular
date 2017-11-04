/*
 * This file ("Registry.java") is part of the molecular-project by Louis.
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

import com.google.errorprone.annotations.CompatibleWith;
import org.molecular.api.exception.RegistryException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * @author Louis
 */

public interface Registry<K, V> extends Iterable<Map.Entry<K, V>> {

    void register(@Nonnull K key, @Nonnull V value);

    boolean containsKey(@Nonnull @CompatibleWith("K") Object key);

    @Nullable
    K getKey(@Nonnull V value);

    @Nonnull
    K getKeySafe(@Nonnull V value) throws RegistryException;

    boolean containsValue(@Nonnull @CompatibleWith("V") Object value);

    @Nullable
    V getValue(@Nonnull K key);

    @Nonnull
    V getValueSafe(@Nonnull K key) throws RegistryException;

    @Nonnegative
    int size();

    @Nonnull
    Set<K> getKeys();

    @Nonnull
    Set<V> getValues();

}
