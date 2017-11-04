/*
 * This file ("SPacketLoginRequest.java") is part of the molecular-project by Louis.
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

package org.molecular.common.network.platform.server.login;

import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.buffer.PacketBuf;
import org.molecular.api.network.buffer.PacketBufUtils;
import org.molecular.api.network.packet.Packet;
import org.molecular.api.network.packet.PacketConstructor;
import org.molecular.common.network.platform.client.NetworkHandlerClientLogin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Louis
 */

public class SPacketLoginRequest implements Packet<NetworkHandlerClientLogin> {

    private Optional<String> password;

    @PacketConstructor
    private SPacketLoginRequest() {
        this.password = Optional.empty();
    }

    public SPacketLoginRequest(@Nullable String password) {
        this.password = Optional.ofNullable(password);
    }

    @Override
    public void writePacket(PacketBuf buffer) throws IOException {
        PacketBufUtils.writeOptional(buffer, this.password, PacketBuf::writeString);
    }

    @Override
    public void readPacket(PacketBuf buffer) throws IOException {
        this.password = PacketBufUtils.readOptional(buffer, PacketBuf::readString);
    }

    @Override
    public void notifyHandler(@Nonnull NetworkHandlerClientLogin handler, @Nonnull NetworkChannel channel) {
        handler.processLoginRequest(this, channel);
    }

    public Optional<String> getPassword() {
        return this.password;
    }
}
