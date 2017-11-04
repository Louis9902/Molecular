/*
 * This file ("ReflectEventListener.java") is part of the molecular-project by Louis.
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
import org.molecular.api.event.listener.EventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * @author Louis
 */

public final class ReflectEventListener<E extends Event<E>> implements EventListener<E> {

    private final Method method;
    private final Object instance;

    public ReflectEventListener(@Nonnull Method method, @Nullable Object instance) {
        this.method = method;
        this.instance = instance;
    }

    @Override
    public void onEvent(Event event) {
        try {
            this.method.invoke(this.instance, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
