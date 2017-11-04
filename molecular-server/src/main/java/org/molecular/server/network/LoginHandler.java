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

package org.molecular.server.network;

import org.molecular.api.Molecular;
import org.molecular.api.MolecularMaterial;
import org.molecular.api.MolecularSettings;
import org.molecular.api.accounts.Account;
import org.molecular.api.base.StringRandom;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.packet.PacketDisconnect;
import org.molecular.api.network.util.ChannelNotifier;
import org.molecular.api.resources.text.BaseTextPart;
import org.molecular.api.resources.text.TextPartTranslation;
import org.molecular.common.crypt.CipherManager;
import org.molecular.common.network.platform.client.login.CPacketEncryptReply;
import org.molecular.common.network.platform.client.login.CPacketLoginReply;
import org.molecular.common.network.platform.client.login.CPacketLoginStart;
import org.molecular.common.network.platform.server.NetworkHandlerServerLogin;
import org.molecular.common.network.platform.server.login.SPacketCompression;
import org.molecular.common.network.platform.server.login.SPacketEncryptRequest;
import org.molecular.common.network.platform.server.login.SPacketLoginRequest;
import org.molecular.common.network.platform.server.login.SPacketLoginSuccess;
import org.molecular.common.persist.PersistentStash;
import org.molecular.server.Internals;
import org.molecular.server.ServerApplication;
import org.molecular.server.ServerHandler;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static org.molecular.api.network.NetworkChannel.ACCOUNT;

/**
 * @author Louis
 */

public class LoginHandler implements NetworkHandlerServerLogin {

    private final static byte[] token;
    private final static StringRandom generator;

    static {
        token = CipherManager.generateVerifyToken(5);
        generator = new StringRandom(10, Molecular.RANDOM);
    }

    private final HashMap<NetworkChannel, byte[]> cache;

    private final Logger logger;
    private final ServerApplication application;

    {
        this.cache = new HashMap<>();
    }

    public LoginHandler(@Nonnull ServerApplication application) {
        this.application = application;
        this.logger = application.getLogger();
    }

    @Override
    public void processLoginStart(CPacketLoginStart packet, NetworkChannel channel) {
        boolean unknown = packet.getUsername().isPresent();
        this.cache.put(channel, new byte[]{1, (byte) (unknown ? 1 : 0)});

        if (unknown) {
            PersistentStash stash = ServerHandler.instance().getPersistentStash();

            if (stash.findAccount(packet.getUsername().get()).isPresent()) {
                logger.warn("User profile with username {} is already registered", packet.getUsername().get());
                channel.disconnect(new TextPartTranslation("disconnect.login.invalid"));
                return;
            }

            Account account = stash.createAccount(packet.getUsername().get(), generator.nextString());
            logger.info("Created account with {}[{}]", account.username(), account.identifier());
            channel.channel().ifPresent(c -> c.attr(ACCOUNT).set(account));
        }

        channel.writeAndFlushPacket(new SPacketEncryptRequest(this.application.getKeyPair().getPublic(), token));
    }

    @Override
    public void processEncryptReply(CPacketEncryptReply packet, NetworkChannel channel) {
        @Nullable byte[] present = this.cache.get(channel);
        checkArgument(present != null && present.length == 2, "invalid login state - null");
        checkArgument(present[0] == 1, "invalid login state - state");

        PrivateKey privateKey = this.application.getKeyPair().getPrivate();

        if (!Arrays.equals(token, packet.token(privateKey))) {
            this.logger.warn("Encryption key is invalid - packet encryption failed");
            return;
        }
        channel.enableEncryption(packet.secretKey(privateKey));

        boolean unknown = present[1] == 1;
        if (unknown) {
            Optional<Account> account = channel.account();

            if (!account.isPresent()) {
                logger.error("Couldn't find unknown account by username, but should be created (check log)");
                channel.disconnect(new TextPartTranslation("disconnect.login.error"));
                return;
            }

            channel.writeAndFlushPacket(new SPacketLoginRequest(account.get().password()));
        } else {
            channel.writeAndFlushPacket(new SPacketLoginRequest(null));
        }

        this.cache.put(channel, new byte[]{2});
    }

    @Override
    public void processLoginReply(CPacketLoginReply packet, NetworkChannel channel) {
        @Nullable byte[] present = this.cache.get(channel);
        checkArgument(present != null && present.length == 1, "invalid login state - null");
        checkArgument(present[0] == 2, "invalid login state - state");

        PersistentStash stash = ServerHandler.instance().getPersistentStash();

        Optional<Account> optional = stash.findAccount(packet.getUsername());
        if (!optional.isPresent()) {
            channel.disconnect(new TextPartTranslation("disconnect.login.invalid"));
            return;
        }

        Account account = optional.get();
        if (!account.authenticate(packet.getPassword())) {
            channel.disconnect(new TextPartTranslation("disconnect.login.failed"));
            return;
        }

        channel.channel().ifPresent(c -> c.attr(ACCOUNT).setIfAbsent(account));

        if (MolecularSettings.compression_threshold > -1) {
            ChannelNotifier<?> update = new ChannelNotifier<>(channel, c -> {
                c.enableCompression(MolecularSettings.compression_threshold);
            });
            channel.writeAndFlushPacket(new SPacketCompression(MolecularSettings.compression_threshold), update);
        }

        this.cache.remove(channel);

        ChannelNotifier<?> notifier = new ChannelNotifier<>(channel, c -> {
            c.switchNetworkProtocol(MolecularMaterial.PROTOCOL_WORK);
            c.switchNetworkHandler(Internals.handler_work);
        });
        channel.writeAndFlushPacket(new SPacketLoginSuccess(), notifier);
    }

    @Override
    public void processDisconnect(@Nonnull PacketDisconnect<?> packet, @Nonnull NetworkChannel channel) {
        this.logger.warn("Channel {} has send disconnect packet? (reason = {})", channel, packet.getReason());
    }

    @Override
    public void propagateDisconnect(@Nonnull BaseTextPart textPart) {
        this.logger.info("Channel disconnected from server with {} while logging in", textPart.getUnformattedWithChildren());
    }

}
