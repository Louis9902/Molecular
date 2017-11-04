/*
 * This file ("NetworkChannel.java") is part of the molecular-project by Louis.
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

package org.molecular.api.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import org.molecular.api.accounts.Account;
import org.molecular.api.network.handler.NetworkHandler;
import org.molecular.api.network.packet.Packet;
import org.molecular.api.platform.Platform;
import org.molecular.api.resources.text.BaseTextPart;
import org.molecular.api.util.Identifiable;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Louis
 */

public interface NetworkChannel extends Identifiable<UUID> {

    AttributeKey<NetworkProtocol> PROTOCOL = AttributeKey.newInstance("molecular:protocol");

    AttributeKey<Account> ACCOUNT = AttributeKey.newInstance("molecular:account");

    Platform platform();

    Optional<Channel> channel();

    boolean isChannelOpen();

    Optional<BaseTextPart> reason();

    Optional<Account> account();

    void disconnect(@Nonnull BaseTextPart reason);

    void close(@Nonnull BaseTextPart reason);

    void writeAndFlushPacket(@Nonnull Packet<?> packet, @Nonnull ChannelFutureListener... listener);

    void writePacket(@Nonnull Packet<?> packet, @Nonnull ChannelFutureListener... listener);

    void flush();

    void enableCompression(int threshold);

    void enableEncryption(@Nonnull SecretKey secretKey);

    void switchNetworkProtocol(@Nonnull NetworkProtocol protocol);

    void switchNetworkHandler(@Nonnull NetworkHandler handler);

}
