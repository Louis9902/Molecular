/*
 * This file ("EventPriority.java") is part of the molecular-project by Louis.
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

package org.molecular.api.event.listener;

/**
 * @author Louis
 */

public enum EventPriority {

    EARLIEST(0),

    EARLY_IGNORE_CANCELLED(1),

    EARLY(2),

    DEFAULT_IGNORE_CANCELLED(3),

    DEFAULT(4),

    LATE_IGNORE_CANCELLED(5),

    LATE(6),

    LATEST_IGNORE_CANCELLED(7),

    LATEST(8),

    MONITOR(9);

    private final int index;

    EventPriority(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }
}
