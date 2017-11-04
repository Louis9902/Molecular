/*
 * This file ("LoginHandler.java") is part of the molecular-project by Louis.
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

import org.molecular.api.MolecularMaterial;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.packet.PacketDisconnect;
import org.molecular.api.network.util.ChannelNotifier;
import org.molecular.api.platform.PlatformPersistent;
import org.molecular.api.resources.text.BaseTextPart;
import org.molecular.client.ClientApplication;
import org.molecular.client.ClientHandler;
import org.molecular.common.crypt.CipherManager;
import org.molecular.common.network.platform.client.NetworkHandlerClientLogin;
import org.molecular.common.network.platform.client.login.CPacketEncryptReply;
import org.molecular.common.network.platform.client.login.CPacketLoginReply;
import org.molecular.common.network.platform.server.login.SPacketCompression;
import org.molecular.common.network.platform.server.login.SPacketEncryptRequest;
import org.molecular.common.network.platform.server.login.SPacketLoginRequest;
import org.molecular.common.network.platform.server.login.SPacketLoginSuccess;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.Optional;

/**
 * @author Louis
 */

public class LoginHandler implements NetworkHandlerClientLogin {

    private final Logger logger;
    private final ClientApplication application;

    public LoginHandler(@Nonnull ClientApplication application) {
        this.application = application;
        this.logger = application.getLogger();
    }

    @Override
    public void processEncryptRequest(SPacketEncryptRequest packet, NetworkChannel channel) {
        SecretKey secretKey = CipherManager.generateNewSharedKey();
        PublicKey publicKey = packet.getPublicKey();

        if (secretKey == null) {
            throw new IllegalStateException("CipherManager returned null key");
        }

        ChannelNotifier notifier = new ChannelNotifier<>(channel, c -> c.enableEncryption(secretKey));
        channel.writeAndFlushPacket(new CPacketEncryptReply(secretKey, publicKey, packet.getToken()), notifier);
    }

    @Override
    public void processLoginRequest(SPacketLoginRequest packet, NetworkChannel channel) {
        PlatformPersistent persistent = ClientHandler.instance().getPersistent();
        Optional<String> password = packet.getPassword();
        password.ifPresent(str -> persistent.getCluster().addString("password", str));
        String def = password.orElse(persistent.getCluster().getString("password"));
        channel.writeAndFlushPacket(new CPacketLoginReply(ClientHandler.USERNAME, def));
    }

    @Override
    public void processCompression(SPacketCompression packet, NetworkChannel channel) {
        channel.enableCompression(packet.getThreshold());
    }

    @Override
    public void processLoginSuccess(SPacketLoginSuccess packet, NetworkChannel channel) {
        channel.switchNetworkProtocol(MolecularMaterial.PROTOCOL_WORK);
        channel.switchNetworkHandler(new WorkHandler(this.application));
    }

    @Override
    public void processDisconnect(@Nonnull PacketDisconnect<?> packet, @Nonnull NetworkChannel channel) {
        channel.close(packet.getReason());
    }

    @Override
    public void propagateDisconnect(@Nonnull BaseTextPart textPart) {
        logger.info("Channel disconnected with {} while logging in", textPart.getDisplayWithChildren());
    }
}
