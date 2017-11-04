/*
 * This file ("EventController.java") is part of the molecular-project by Louis.
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

import org.molecular.api.event.listener.EventListener;
import org.molecular.api.event.listener.EventPriority;

import javax.annotation.Nonnull;

/**
 * @author Louis
 */

public interface EventController {

    <E extends Event<E>> void post(@Nonnull E event);

    <E extends Event<E>> void registerListener(@Nonnull Class<E> type, @Nonnull EventListener<E> listener);

    <E extends Event<E>> void registerListener(@Nonnull Class<E> type, @Nonnull EventListener<E> listener, @Nonnull EventPriority priority);

    <E extends Event<E>> void unregisterListener(@Nonnull Class<E> type, @Nonnull EventListener<E> listener);

    <E extends Event<E>> void unregisterListener(@Nonnull Class<E> type, @Nonnull EventListener<E> listener, @Nonnull EventPriority priority);

    void register(@Nonnull Object object);

}
