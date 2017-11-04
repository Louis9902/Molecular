/*
 * This file ("DefaultEventController.java") is part of the molecular-project by Louis.
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

package org.molecular.common.event;

import org.molecular.api.event.Event;
import org.molecular.api.event.EventController;
import org.molecular.api.event.listener.EventListener;
import org.molecular.api.event.listener.EventPriority;
import org.molecular.api.event.listener.EventSubscriber;
import org.molecular.api.platform.PlatformHandler;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Louis
 */

public class DefaultEventController implements EventController {

    private final PlatformHandler platform;

    public DefaultEventController(PlatformHandler handler) {
        this.platform = handler;
    }

    @Override
    public <E extends Event<E>> void post(@Nonnull E event) {
        event.getHandlers().post(event);
    }

    @Override
    public <E extends Event<E>> void registerListener(@Nonnull Class<E> type, @Nonnull EventListener<E> listener) {
        Event.getEventHandlers(type).register(listener);
    }

    @Override
    public <E extends Event<E>> void registerListener(@Nonnull Class<E> type, @Nonnull EventListener<E> listener, @Nonnull EventPriority priority) {
        Event.getEventHandlers(type).register(listener, priority);
    }

    @Override
    public <E extends Event<E>> void unregisterListener(@Nonnull Class<E> type, @Nonnull EventListener<E> listener) {
        Event.getEventHandlers(type).unregister(listener);
    }

    @Override
    public <E extends Event<E>> void unregisterListener(@Nonnull Class<E> type, @Nonnull EventListener<E> listener, @Nonnull EventPriority priority) {
        Event.getEventHandlers(type).unregister(listener, priority);
    }

    @Override
    public void register(@Nonnull Object object) {
        // TODO: 30.10.2017 rework
        Class<?> clazz = object.getClass();
        List<Method> methods = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {

            EventSubscriber subscriber = method.getAnnotation(EventSubscriber.class);
            if (subscriber == null) {
                continue;
            }

            if (!method.getReturnType().equals(Void.TYPE)) {
                continue;
            }

            if (method.getParameterCount() != 1) {
                continue;
            }

            Class<?> param = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(param)) {
                continue;
            }

            if (param.equals(Event.class)) {
                continue;
            }

            method.setAccessible(true);
            methods.add(method);
        }

        for (Method method : methods) {
            EventPriority priority = method.getAnnotation(EventSubscriber.class).priority();
            Class<? extends Event> event = method.getParameterTypes()[0].asSubclass(Event.class);
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            Event.getEventHandlers(event).register(new ReflectEventListener<>(method, isStatic ? null : object), priority);
        }

    }

}
