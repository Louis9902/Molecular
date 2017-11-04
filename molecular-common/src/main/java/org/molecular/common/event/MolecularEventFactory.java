/*
 * This file ("MolecularEventFactory.java") is part of the molecular-project by Louis.
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

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.molecular.api.Molecular;
import org.molecular.api.accounts.Account;
import org.molecular.api.events.accounts.AccountCreateEvent;
import org.molecular.api.events.network.channel.ChannelActiveEvent;
import org.molecular.api.events.network.channel.ChannelInactiveEvent;
import org.molecular.api.events.network.channel.ChannelTimeoutEvent;
import org.molecular.api.events.network.payload.PayloadRetainEvent;
import org.molecular.api.events.plugin.PluginAssemblyEvent;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.packet.PacketPayload;
import org.molecular.api.plugin.PluginContainer;

/**
 * @author Louis
 */
public final class MolecularEventFactory {

    private static MolecularEventFactory instance = new MolecularEventFactory();

    private MolecularEventFactory() {
    }

    public static MolecularEventFactory instance() {
        return MolecularEventFactory.instance;
    }

    @CanIgnoreReturnValue
    public ChannelActiveEvent callChannelActiveEvent(NetworkChannel channel) {
        ChannelActiveEvent event = new ChannelActiveEvent(channel);
        Molecular.getEventController().post(event);
        return event;
    }

    @CanIgnoreReturnValue
    public ChannelInactiveEvent callChannelInactiveEvent(NetworkChannel channel) {
        ChannelInactiveEvent event = new ChannelInactiveEvent(channel);
        Molecular.getEventController().post(event);
        return event;
    }

    @CanIgnoreReturnValue
    public ChannelTimeoutEvent callChannelTimeoutEvent(NetworkChannel channel) {
        ChannelTimeoutEvent event = new ChannelTimeoutEvent(channel);
        Molecular.getEventController().post(event);
        return event;
    }

    @CanIgnoreReturnValue
    public AccountCreateEvent callAccountCreateEvent(Account account) {
        AccountCreateEvent event = new AccountCreateEvent(account);
        Molecular.getEventController().post(event);
        return event;
    }

    @CanIgnoreReturnValue
    public PayloadRetainEvent callPayloadRetainEvent(NetworkChannel channel, PacketPayload<?> packet) {
        PayloadRetainEvent event = new PayloadRetainEvent(channel, packet);
        Molecular.getEventController().post(event);
        return event;
    }

    @CanIgnoreReturnValue
    public PluginAssemblyEvent callPluginAssemblyEvent(PluginContainer container) {
        PluginAssemblyEvent event = new PluginAssemblyEvent(container);
        Molecular.getEventController().post(event);
        return event;
    }

}
