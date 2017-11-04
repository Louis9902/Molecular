/*
 * This file ("PayloadRetainEvent.java") is part of the molecular-project by Louis.
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

package org.molecular.api.events.network.payload;

import org.molecular.api.event.Cancellable;
import org.molecular.api.event.Event;
import org.molecular.api.event.EventHandlers;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.packet.PacketPayload;

import javax.annotation.Nonnull;

/**
 * @author Louis
 */

public class PayloadRetainEvent extends Event<PayloadRetainEvent> implements Cancellable {

    private static final EventHandlers<PayloadRetainEvent> HANDLER = new EventHandlers<>();

    private final NetworkChannel channel;
    private PacketPayload<?> packet;

    public PayloadRetainEvent(NetworkChannel channel, PacketPayload<?> packet) {
        this.channel = channel;
        this.packet = packet;
    }

    public static EventHandlers<PayloadRetainEvent> getEventHandlers() {
        return PayloadRetainEvent.HANDLER;
    }

    public PacketPayload<?> getPacket() {
        return this.packet;
    }

    public void setPacket(PacketPayload<?> packet) {
        this.packet = packet;
    }

    public NetworkChannel getChannel() {
        return this.channel;
    }

    @Nonnull
    @Override
    public EventHandlers<PayloadRetainEvent> getHandlers() {
        return PayloadRetainEvent.HANDLER;
    }

    @Override
    public boolean isCancelled() {
        return super.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancel) {
        super.setCancelled(cancel);
    }
}
