/*
 * This file ("PluginTransitionException.java") is part of the molecular-project by Louis.
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

package org.molecular.common.exception;

/**
 * @author Louis
 */

public class PluginTransitionException extends RuntimeException {

    private static final long serialVersionUID = -2225656486362644959L;

    public PluginTransitionException() {
    }

    public PluginTransitionException(String message) {
        super(message);
    }

    public PluginTransitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginTransitionException(Throwable cause) {
        super(cause);
    }
}
