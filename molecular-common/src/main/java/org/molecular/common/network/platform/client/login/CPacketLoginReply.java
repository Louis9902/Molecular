/*
 * This file ("CPacketLoginReply.java") is part of the molecular-project by Louis.
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
import org.molecular.api.network.packet.Packet;
import org.molecular.api.network.packet.PacketConstructor;
import org.molecular.common.network.platform.server.NetworkHandlerServerLogin;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Louis
 */

public class CPacketLoginReply implements Packet<NetworkHandlerServerLogin> {

    private String username;
    private String password;

    @PacketConstructor
    private CPacketLoginReply() {
    }

    public CPacketLoginReply(@Nonnull String username, @Nonnull String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void writePacket(PacketBuf buffer) throws IOException {
        buffer.writeString(this.username);
        buffer.writeString(this.password);
    }

    @Override
    public void readPacket(PacketBuf buffer) throws IOException {
        this.username = buffer.readString();
        this.password = buffer.readString();
    }

    @Override
    public void notifyHandler(@Nonnull NetworkHandlerServerLogin handler, @Nonnull NetworkChannel channel) {
        handler.processLoginReply(this, channel);
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

}
