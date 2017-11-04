/*
 * This file ("HandshakeHandler.java") is part of the molecular-project by Louis.
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

import org.molecular.api.MolecularMaterial;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.packet.PacketDisconnect;
import org.molecular.api.resources.text.BaseTextPart;
import org.molecular.api.resources.text.TextPartTranslation;
import org.molecular.common.network.platform.client.handshake.CPacketHandshake;
import org.molecular.common.network.platform.server.NetworkHandlerServerHandshake;
import org.molecular.server.Internals;
import org.molecular.server.ServerApplication;
import org.slf4j.Logger;

import javax.annotation.Nonnull;

/**
 * @author Louis
 */

public class HandshakeHandler implements NetworkHandlerServerHandshake {

    private final Logger logger;
    private final ServerApplication application;

    public HandshakeHandler(ServerApplication application) {
        this.application = application;
        this.logger = application.getLogger();
    }

    @Override
    public void notifyHandshake(CPacketHandshake packet, NetworkChannel channel) {
        if (packet.getProtocol() != this.application.getProtocol()) {
            channel.disconnect(new TextPartTranslation("disconnect.protocol"));
            return;
        }

        if (packet.getVersion() != this.application.getVersion()) {
            boolean server = packet.getVersion() > this.application.getVersion();
            channel.disconnect(new TextPartTranslation("disconnect.outdated." + (server ? "server" : "client")));
            return;
        }

        // TODO: 24.10.2017 Check plugins

        channel.switchNetworkProtocol(MolecularMaterial.PROTOCOL_LOGIN);
        channel.switchNetworkHandler(Internals.handler_login);
    }

    @Override
    public void processDisconnect(@Nonnull PacketDisconnect<?> packet, @Nonnull NetworkChannel channel) {
        logger.warn("Channel {} has send disconnect packet? (reason = {})", channel, packet.getReason());
    }

    @Override
    public void propagateDisconnect(@Nonnull BaseTextPart textPart) {
        logger.info("Channel disconnected from server with {} while handshaking", textPart.getUnformattedWithChildren());
    }
}
