/*
 * This file ("ExpiringMap.java") is part of the molecular-project by Louis.
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

import java.util.Map;
import java.util.concurrent.Delayed;

/**
 * @author Louis
 */

public interface ExpiringMap<K, V> extends Map<K, V> {

    V put(K key, V value, long expireAfter);

    boolean renewKey(Object key);

    void cleanup();

    interface Key<K> extends Delayed {

        K getKey();

        void renew();

        void expire();

        long getDelayMillis();

    }

}
