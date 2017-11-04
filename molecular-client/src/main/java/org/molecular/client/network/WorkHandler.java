/*
 * This file ("WorkHandler.java") is part of the molecular-project by Louis.
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

package org.molecular.client.network;

import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.packet.PacketDisconnect;
import org.molecular.api.resources.text.BaseTextPart;
import org.molecular.client.ClientApplication;
import org.molecular.common.event.MolecularEventFactory;
import org.molecular.common.network.platform.client.NetworkHandlerClientWork;
import org.molecular.common.network.platform.server.work.SPacketPayload;
import org.molecular.common.network.platform.server.work.SPacketRevisionEcho;
import org.molecular.common.network.platform.server.work.SPacketTransmitEcho;
import org.slf4j.Logger;

import javax.annotation.Nonnull;

/**
 * @author Louis
 */

public class WorkHandler implements NetworkHandlerClientWork {

    private final Logger logger;
    private final ClientApplication application;

    public WorkHandler(@Nonnull ClientApplication application) {
        this.application = application;
        this.logger = application.getLogger();
    }


    @Override
    public void processPayload(SPacketPayload packet, NetworkChannel channel) {
        MolecularEventFactory.instance().callPayloadRetainEvent(channel, packet);
    }

    @Override
    public void processTransmitEcho(SPacketTransmitEcho packet, NetworkChannel channel) {
    }

    @Override
    public void processRevisionEcho(SPacketRevisionEcho packet, NetworkChannel channel) {
    }

    @Override
    public void processDisconnect(@Nonnull PacketDisconnect<?> packet, @Nonnull NetworkChannel channel) {
        channel.close(packet.getReason());
    }

    @Override
    public void propagateDisconnect(@Nonnull BaseTextPart textPart) {
        logger.info("Channel disconnected with {} while working", textPart.getDisplayWithChildren());
    }
}
