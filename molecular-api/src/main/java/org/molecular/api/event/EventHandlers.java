/*
 * This file ("EventHandlers.java") is part of the molecular-project by Louis.
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

package org.molecular.api.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.molecular.api.event.listener.EventListener;
import org.molecular.api.event.listener.EventPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Louis
 */

public final class EventHandlers<E extends Event<E>> {

    private static final Logger LOGGER = LoggerFactory.getLogger("org.molecular.event");
    private static final List<EventHandlers<?>> handlers = new ArrayList<>();

    private final Multimap<EventPriority, EventListener<E>> registry;

    private EventListener<E>[][] listeners;
    private int[] priorities;

    public EventHandlers() {
        this.registry = HashMultimap.create();
        EventHandlers.handlers.add(this);
    }

    public static void bakeAll() {
        long start = System.currentTimeMillis();
        EventHandlers.handlers.forEach(EventHandlers::bake);
        long stop = System.currentTimeMillis();
        LOGGER.info("Created all event listeners in {}ms", (stop - start));
    }

    public void register(EventListener<E> listener) {
        this.register(listener, EventPriority.DEFAULT);
    }

    public void register(EventListener<E> listener, EventPriority priority) {
        this.registry.put(priority, listener);
        this.listeners = null;
    }

    public void unregister(EventListener<E> listener) {
        this.unregister(listener, EventPriority.DEFAULT);
    }

    public void unregister(EventListener<E> listener, EventPriority priority) {
        this.registry.remove(priority, listener);
        this.listeners = null;
    }

    public void post(E event) {
        this.bake();
        for (int priority = 0; priority < priorities.length; priority++) {
            if (event instanceof Cancellable) {
                if (event.isCancelled() && (this.priorities[priority] & 1) == 0) {
                    continue;
                }
            }
            for (int index = 0; index < this.listeners[priority].length; index++) {
                try {
                    this.listeners[priority][index].onEvent(event);
                } catch (Throwable throwable) {
                    LOGGER.error("unable to execute event for listener of {}", event.getClass().getSimpleName(), throwable);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void bake() {
        if (this.listeners == null) {
            List<Integer> priorities = new LinkedList<>();
            List<EventListener[]> wrappers = new LinkedList<>();

            for (EventPriority priority : EventPriority.values()) {
                Collection<EventListener<E>> collection = this.registry.get(priority);
                if (!collection.isEmpty()) {
                    priorities.add(priority.index());
                    wrappers.add(collection.toArray(new EventListener[0]));
                }
            }

            this.priorities = priorities.stream().mapToInt(value -> value).toArray();
            this.listeners = wrappers.toArray(new EventListener[0][]);
        }
    }

}
