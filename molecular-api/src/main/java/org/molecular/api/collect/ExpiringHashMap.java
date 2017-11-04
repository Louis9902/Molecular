/*
 * This file ("ExpiringHashMap.java") is part of the molecular-project by Louis.
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

package org.molecular.api.collect;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Louis
 */

public class ExpiringHashMap<K, V> extends AbstractExpiringMap<K, V> {

    private final HashMap<K, V> mapping;

    public ExpiringHashMap(long expireAfter) {
        super(expireAfter);
        this.mapping = new HashMap<>();
    }

    public ExpiringHashMap(long expireAfter, int initialCapacity) {
        super(expireAfter, initialCapacity);
        this.mapping = new HashMap<>(initialCapacity);
    }

    public ExpiringHashMap(long expireAfter, int initialCapacity, float loadFactor) {
        super(expireAfter, initialCapacity, loadFactor);
        this.mapping = new HashMap<>(initialCapacity, expireAfter);
    }

    @Override
    protected Set<Entry<K, V>> forwarding() {
        return this.mapping.entrySet();
    }
}
