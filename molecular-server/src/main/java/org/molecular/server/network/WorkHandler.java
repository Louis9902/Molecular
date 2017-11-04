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

package org.molecular.server.network;

import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.packet.PacketDisconnect;
import org.molecular.api.resources.text.BaseTextPart;
import org.molecular.common.event.MolecularEventFactory;
import org.molecular.common.network.platform.client.work.CPacketPayload;
import org.molecular.common.network.platform.client.work.CPacketRevision;
import org.molecular.common.network.platform.client.work.CPacketTransmit;
import org.molecular.common.network.platform.server.NetworkHandlerServerWork;
import org.molecular.server.ServerApplication;
import org.slf4j.Logger;

import javax.annotation.Nonnull;

/**
 * @author Louis
 */

public class WorkHandler implements NetworkHandlerServerWork {

    private final ServerApplication application;
    private final Logger logger;

    public WorkHandler(@Nonnull ServerApplication application) {
        this.application = application;
        this.logger = application.getLogger();
    }

    @Override
    public void processPayload(CPacketPayload packet, NetworkChannel channel) {
        MolecularEventFactory.instance().callPayloadRetainEvent(channel, packet);
    }

    @Override
    public void processTransmit(CPacketTransmit packet, NetworkChannel channel) {

    }

    @Override
    public void processRevision(CPacketRevision packet, NetworkChannel channel) {

    }

    @Override
    public void processDisconnect(@Nonnull PacketDisconnect<?> packet, @Nonnull NetworkChannel channel) {
        this.logger.warn("Channel {} has send disconnect packet? (reason = {})", channel, packet.getReason());
    }

    @Override
    public void propagateDisconnect(@Nonnull BaseTextPart textPart) {
        this.logger.info("Channel disconnected from server with {} while working", textPart.getUnformattedWithChildren());
    }
}
