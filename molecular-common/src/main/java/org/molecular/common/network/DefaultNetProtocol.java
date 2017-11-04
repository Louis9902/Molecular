/*
 * This file ("DefaultNetProtocol.java") is part of the molecular-project by Louis.
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

package org.molecular.common.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.molecular.api.network.ConstNetwork;
import org.molecular.api.network.NetworkProtocol;
import org.molecular.api.network.packet.Packet;
import org.molecular.api.network.packet.PacketDisconnect;
import org.molecular.api.platform.Platform;
import org.molecular.api.resources.text.BaseTextPart;
import org.molecular.common.network.platform.client.handshake.CPacketHandshake;
import org.molecular.common.network.platform.client.login.CPacketEncryptReply;
import org.molecular.common.network.platform.client.login.CPacketLoginReply;
import org.molecular.common.network.platform.client.login.CPacketLoginStart;
import org.molecular.common.network.platform.client.work.CPacketPayload;
import org.molecular.common.network.platform.client.work.CPacketRevision;
import org.molecular.common.network.platform.client.work.CPacketTransmit;
import org.molecular.common.network.platform.server.handshake.SPacketDisconnectHandshake;
import org.molecular.common.network.platform.server.login.SPacketCompression;
import org.molecular.common.network.platform.server.login.SPacketDisconnectLogin;
import org.molecular.common.network.platform.server.login.SPacketEncryptRequest;
import org.molecular.common.network.platform.server.login.SPacketLoginRequest;
import org.molecular.common.network.platform.server.login.SPacketLoginSuccess;
import org.molecular.common.network.platform.server.work.SPacketDisconnectWork;
import org.molecular.common.network.platform.server.work.SPacketPayload;
import org.molecular.common.network.platform.server.work.SPacketRevisionEcho;
import org.molecular.common.network.platform.server.work.SPacketTransmitEcho;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Optional;

/**
 * @author Louis
 */

public enum DefaultNetProtocol implements NetworkProtocol {

    HANDSHAKE(ConstNetwork.HANDSHAKE) {
        {
            /*--- CLIENT -> SERVER ---*/
            this.register(Platform.SERVER, CPacketHandshake.class);
            /*--- CLIENT <- SERVER ---*/
            this.register(Platform.CLIENT, SPacketDisconnectHandshake.class);
        }
    },
    LOGIN(ConstNetwork.LOGIN) {
        {
            /*--- CLIENT -> SERVER ---*/
            this.register(Platform.SERVER, CPacketLoginStart.class);
            this.register(Platform.SERVER, CPacketEncryptReply.class);
            this.register(Platform.SERVER, CPacketLoginReply.class);
            /*--- CLIENT <- SERVER ---*/
            this.register(Platform.CLIENT, SPacketDisconnectLogin.class);
            this.register(Platform.CLIENT, SPacketEncryptRequest.class);
            this.register(Platform.CLIENT, SPacketLoginRequest.class);
            this.register(Platform.CLIENT, SPacketLoginSuccess.class);
            this.register(Platform.CLIENT, SPacketCompression.class);
        }
    },
    WORK(ConstNetwork.WORK) {
        {
            /*--- CLIENT -> SERVER ---*/
            this.register(Platform.SERVER, CPacketPayload.class);
            this.register(Platform.SERVER, CPacketTransmit.class);
            this.register(Platform.SERVER, CPacketRevision.class);
            /*--- CLIENT <- SERVER ---*/
            this.register(Platform.CLIENT, SPacketDisconnectWork.class);
            this.register(Platform.CLIENT, SPacketPayload.class);
            this.register(Platform.CLIENT, SPacketTransmitEcho.class);
            this.register(Platform.CLIENT, SPacketRevisionEcho.class);
        }
    };

    private static final Logger LOGGER = LoggerFactory.getLogger("org.molecular.network");

    private final int identifier;
    private final EnumMap<Platform, BiMap<Class<? extends Packet>, Integer>> mapping;

    DefaultNetProtocol(int identifier) {
        this.identifier = identifier;
        this.mapping = new EnumMap<>(Platform.class);
        for (Platform platform : Platform.values()) {
            this.mapping.put(platform, HashBiMap.create());
        }
    }

    @Override
    public void register(@Nonnull Platform platform, @Nonnull Class<? extends Packet> clazz) {
        BiMap<Class<? extends Packet>, Integer> mapping = this.mapping.get(platform);
        if (mapping.containsKey(clazz)) {
            int index = mapping.get(clazz);
            LOGGER.error("{} packet {} is already registered with identifier {}", platform, clazz.getSimpleName(), index);
        } else {
            mapping.put(clazz, this.nextIndex(platform));
        }
    }

    @Override
    public Optional<Integer> get(@Nonnull Platform platform, Class<? extends Packet> clazz) {
        return Optional.ofNullable(this.mapping.get(platform).get(clazz));
    }

    @Override
    public Optional<Class<? extends Packet>> get(@Nonnull Platform platform, Integer index) {
        return Optional.ofNullable(this.mapping.get(platform).inverse().get(index));
    }

    @Override
    public Optional<Packet> createDisconnectPacket(@Nonnull BaseTextPart reason) {
        Class<? extends Packet> clazz = this.mapping.get(Platform.CLIENT).inverse().get(0);
        try {
            Class<? extends PacketDisconnect> subclass = clazz.asSubclass(PacketDisconnect.class);
            return Optional.of(subclass.getConstructor(BaseTextPart.class).newInstance(reason));
        } catch (Exception e) {
            LOGGER.warn("Protocol {} has no matching PacketDisconnect to return", this.identifier);
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public Integer identifier() {
        return this.identifier;
    }

    private int nextIndex(Platform platform) {
        return this.mapping.get(platform).size();
    }
}
