/*
 * This file ("AbstractRegistry.java") is part of the molecular-project by Louis.
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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterators;
import com.google.errorprone.annotations.CompatibleWith;
import org.molecular.api.exception.RegistryException;
import org.molecular.api.util.Acceptable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Louis
 */

public abstract class AbstractRegistry<K, V> implements Registry<K, V> {

    private final String name;
    private final Acceptable<K, V> acceptable;
    private final BiMap<K, V> registry;

    protected AbstractRegistry(@Nonnull String name, @Nonnull Acceptable<K, V> acceptable) {
        this.name = name;
        this.acceptable = acceptable;
        this.registry = HashBiMap.create();
    }

    @Override
    public void register(@Nonnull K key, @Nonnull V value) throws RegistryException {
        if (!this.registry.containsKey(key)) {
            if (this.acceptable.accepts(key, value)) {
                this.registry.put(key, value);
            } else {
                throw new RegistryException("Registry " + this.name + " does not permit entry parameter {key=" + key + ", value=" + value + "}");
            }
        } else {
            throw new RegistryException("Registry " + this.name + " has already value assigned to key " + key);
        }
    }

    @Override
    public boolean containsKey(@Nonnull @CompatibleWith("K") Object key) {
        return this.registry.containsKey(key);
    }

    @CheckForNull
    @Nullable
    @Override
    public K getKey(@Nonnull V value) {
        return this.registry.inverse().get(value);
    }

    @Nonnull
    @Override
    public K getKeySafe(@Nonnull V value) throws RegistryException {
        K key = this.getKey(value);
        if (key == null) {
            throw new RegistryException("Registry " + this.name + " does not contain mapping for value " + value);
        }
        return key;
    }

    @Override
    public boolean containsValue(@Nonnull @CompatibleWith("V") Object value) {
        return this.registry.containsValue(value);
    }

    @CheckForNull
    @Nullable
    @Override
    public V getValue(@Nonnull K key) {
        return this.registry.get(key);
    }

    @Nonnull
    @Override
    public V getValueSafe(@Nonnull K key) throws RegistryException {
        V value = this.getValue(key);
        if (value == null) {
            throw new RegistryException("Registry " + this.name + " does not contain mapping for key " + key);
        }
        return value;
    }

    @Nonnegative
    @Override
    public int size() {
        return this.registry.size();
    }

    @Nonnull
    @Override
    public Set<K> getKeys() {
        return this.registry.keySet();
    }

    @Nonnull
    @Override
    public Set<V> getValues() {
        return new HashSet<>(this.registry.values());
    }

    @Nonnull
    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return Iterators.unmodifiableIterator(this.registry.entrySet().iterator());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name, this.registry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractRegistry)) return false;
        AbstractRegistry<?, ?> that = (AbstractRegistry<?, ?>) o;
        return Objects.equal(this.name, that.name) &&
                Objects.equal(this.registry, that.registry);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name)
                .add("size", this.size())
                .toString();
    }
}
