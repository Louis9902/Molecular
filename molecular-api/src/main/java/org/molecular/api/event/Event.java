/*
 * This file ("Event.java") is part of the molecular-project by Louis.
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

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Louis
 */

public abstract class Event<E extends Event<E>> {

    private boolean cancelled = false;

    protected Event() {
    }

    @SuppressWarnings({"unchecked", "JavaReflectionMemberAccess"})
    public static <E extends Event<E>> EventHandlers<E> getEventHandlers(@Nonnull Class<E> clazz) {
        try {
            Method method = Event.getEventHandlersClass(clazz).getDeclaredMethod("getEventHandlers");
            Preconditions.checkState(Modifier.isStatic(method.getModifiers()), "static access required");
            Class<? extends EventHandlers> subclass = method.getReturnType().asSubclass(EventHandlers.class);
            return subclass.cast(method.invoke(null));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("EventHandlers invoke failed");
        }
    }

    @SuppressWarnings({"unchecked", "JavaReflectionMemberAccess"})
    private static <E extends Event<E>> Class<? extends E> getEventHandlersClass(@Nonnull Class<E> clazz) {
        try {
            clazz.getDeclaredMethod("getEventHandlers");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                if (!clazz.getSuperclass().equals(Event.class)) {
                    return Event.getEventHandlersClass(clazz);
                }
                throw new RuntimeException("Cannot use EventHandlers of Event, subclass must provide one", e);
            }
            throw new RuntimeException("Cannot find static accessor for EventHandlers of " + clazz, e);
        }
    }

    @Nonnull
    public abstract EventHandlers<E> getHandlers();

    protected boolean isCancelled() {
        return this.cancelled;
    }

    protected void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
