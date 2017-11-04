/*
 * This file ("CPacketLoginStart.java") is part of the molecular-project by Louis.
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

package org.molecular.common.network.platform.client.login;

import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.buffer.PacketBuf;
import org.molecular.api.network.buffer.PacketBufUtils;
import org.molecular.api.network.packet.Packet;
import org.molecular.api.network.packet.PacketConstructor;
import org.molecular.common.network.platform.server.NetworkHandlerServerLogin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Louis
 */

public class CPacketLoginStart implements Packet<NetworkHandlerServerLogin> {

    private Optional<String> username;

    @PacketConstructor
    private CPacketLoginStart() {
        this.username = Optional.empty();
    }

    public CPacketLoginStart(@Nullable String username) {
        this.username = Optional.ofNullable(username);
    }

    @Override
    public void writePacket(PacketBuf buffer) throws IOException {
        PacketBufUtils.writeOptional(buffer, this.username, PacketBuf::writeString);
    }

    @Override
    public void readPacket(PacketBuf buffer) throws IOException {
        this.username = PacketBufUtils.readOptional(buffer, PacketBuf::readString);
    }

    @Override
    public void notifyHandler(@Nonnull NetworkHandlerServerLogin handler, @Nonnull NetworkChannel channel) {
        handler.processLoginStart(this, channel);
    }

    public Optional<String> getUsername() {
        return this.username;
    }
}
