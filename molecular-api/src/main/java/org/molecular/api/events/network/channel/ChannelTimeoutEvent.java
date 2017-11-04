/*
 * This file ("ChannelTimeoutEvent.java") is part of the molecular-project by Louis.
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

package org.molecular.api.events.network.channel;

import org.molecular.api.event.EventHandlers;
import org.molecular.api.events.network.NetworkEvent;
import org.molecular.api.network.NetworkChannel;

import javax.annotation.Nonnull;

/**
 * @author Louis
 */

public class ChannelTimeoutEvent extends NetworkEvent<ChannelTimeoutEvent> {

    private static final EventHandlers<ChannelTimeoutEvent> HANDLER = new EventHandlers<>();

    public ChannelTimeoutEvent(NetworkChannel channel) {
        super(channel);
    }

    public static EventHandlers<ChannelTimeoutEvent> getEventHandlers() {
        return ChannelTimeoutEvent.HANDLER;
    }

    @Nonnull
    @Override
    public EventHandlers<ChannelTimeoutEvent> getHandlers() {
        return ChannelTimeoutEvent.HANDLER;
    }
}
