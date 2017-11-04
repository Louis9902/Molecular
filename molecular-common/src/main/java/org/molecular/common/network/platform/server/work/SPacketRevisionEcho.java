/*
 * This file ("SPacketRevisionEcho.java") is part of the molecular-project by Louis.
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

package org.molecular.common.network.platform.server.work;

import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.buffer.PacketBuf;
import org.molecular.api.network.buffer.PacketBuffers;
import org.molecular.api.network.packet.Packet;
import org.molecular.api.network.packet.PacketConstructor;
import org.molecular.common.network.platform.client.NetworkHandlerClientWork;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Louis
 */

public class SPacketRevisionEcho implements Packet<NetworkHandlerClientWork> {

    private String resource;
    private PacketBuf payload;

    @PacketConstructor
    private SPacketRevisionEcho() {
    }

    public SPacketRevisionEcho(String resource, PacketBuf payload) {
        this.resource = resource;
        this.payload = payload;
    }

    @Override
    public final void writePacket(PacketBuf buffer) throws IOException {
        buffer.writeString(this.resource);
        buffer.writeVarInt(this.payload.readableBytes());
        buffer.writeBytes(this.payload);
    }

    @Override
    public final void readPacket(PacketBuf buffer) throws IOException {
        this.resource = buffer.readString();
        int size = buffer.readVarInt();
        this.payload = PacketBuffers.wrappedBuffer(buffer.readBytes(size));
    }

    @Override
    public void notifyHandler(@Nonnull NetworkHandlerClientWork handler, @Nonnull NetworkChannel channel) {
        handler.processRevisionEcho(this,channel);
    }

    public String getResource() {
        return this.resource;
    }

    public PacketBuf getPayload() {
        return this.payload;
    }
}
