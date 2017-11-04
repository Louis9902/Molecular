/*
 * This file ("AbstractExpiringMap.java") is part of the molecular-project by Louis.
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

import javax.annotation.Nonnull;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author Louis
 */

public abstract class AbstractExpiringMap<K, V> extends AbstractMap<K, V> implements ExpiringMap<K, V> {

    private final long expireAfter;

    private final Map<K, Key<K>> keys;
    private final DelayQueue<Key<K>> delayed;

    protected AbstractExpiringMap(long expireAfter) {
        this.expireAfter = expireAfter;
        this.keys = new WeakHashMap<>();
        this.delayed = new DelayQueue<>();
    }

    protected AbstractExpiringMap(long expireAfter, int initialCapacity) {
        this.expireAfter = expireAfter;
        this.keys = new WeakHashMap<>(initialCapacity);
        this.delayed = new DelayQueue<>();
    }

    protected AbstractExpiringMap(long expireAfter, int initialCapacity, float loadFactor) {
        this.expireAfter = expireAfter;
        this.keys = new WeakHashMap<>(initialCapacity, loadFactor);
        this.delayed = new DelayQueue<>();
    }

    @Override
    public V get(Object key) {
        this.renewKey(key);
        return super.get(key);
    }

    @Override
    public V put(K key, V value) {
        return this.put(key, value, this.expireAfter);
    }

    @Override
    public V remove(Object key) {
        V value = super.remove(key);
        this.expireKey(this.keys.remove(key));
        return value;
    }

    @Override
    public void clear() {
        this.keys.clear();
        this.delayed.clear();
        super.clear();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        this.cleanup();
        return this.forwarding();
    }

    @Override
    public V put(K key, V value, long expireAfter) {
        Key<K> update = new SimpleKey<>(key, expireAfter);
        Key<K> remove = this.keys.put(key, update);
        this.expireKey(remove);
        this.delayed.offer(update);
        return super.put(key, value);
    }

    @Override
    public boolean renewKey(Object key) {
        Key<K> update = this.keys.get(key);
        if (update != null) {
            update.renew();
            return true;
        }
        return false;
    }

    @Override
    public void cleanup() {
        while (!this.delayed.isEmpty()) {
            Key<K> remove = this.delayed.poll();
            this.keys.remove(remove.getKey());
            super.remove(remove.getKey());
        }
    }

    protected abstract Set<Entry<K, V>> forwarding();

    private void expireKey(Key<K> key) {
        if (key != null) {
            key.expire();
            this.cleanup();
        }
    }

    public class SimpleKey<K> implements Key<K> {

        private final long expire;
        private final K key;

        private long current = System.currentTimeMillis();

        SimpleKey(K key, long expire) {
            this.expire = expire;
            this.key = key;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public void renew() {
            this.current = System.currentTimeMillis();
        }

        @Override
        public void expire() {
            this.current = System.currentTimeMillis() - expire - 1;
        }

        @Override
        public long getDelayMillis() {
            return (this.current + this.expire) - System.currentTimeMillis();
        }

        @Override
        public long getDelay(@Nonnull TimeUnit unit) {
            return unit.convert(this.getDelayMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(@Nonnull Delayed that) {
            return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), that.getDelay(TimeUnit.MILLISECONDS));
        }
    }

}
