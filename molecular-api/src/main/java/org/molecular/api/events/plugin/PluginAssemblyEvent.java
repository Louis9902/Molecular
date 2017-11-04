/*
 * This file ("PluginAssemblyEvent.java") is part of the molecular-project by Louis.
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

package org.molecular.api.events.plugin;

import org.molecular.api.event.Event;
import org.molecular.api.event.EventHandlers;
import org.molecular.api.plugin.PluginContainer;

import javax.annotation.Nonnull;

/**
 * @author Louis
 */

public class PluginAssemblyEvent extends Event<PluginAssemblyEvent> {

    private static final EventHandlers<PluginAssemblyEvent> HANDLER = new EventHandlers<>();

    private final PluginContainer container;

    public PluginAssemblyEvent(PluginContainer container) {
        this.container = container;
    }

    public static EventHandlers<PluginAssemblyEvent> getEventHandlers() {
        return PluginAssemblyEvent.HANDLER;
    }

    public PluginContainer getContainer() {
        return this.container;
    }

    @Nonnull
    @Override
    public EventHandlers<PluginAssemblyEvent> getHandlers() {
        return PluginAssemblyEvent.HANDLER;
    }
}
