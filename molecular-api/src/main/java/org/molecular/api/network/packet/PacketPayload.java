/*
 * This file ("PacketPayload.java") is part of the molecular-project by Louis.
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

package org.molecular.api.network.packet;

import org.molecular.api.network.buffer.PacketBuf;
import org.molecular.api.network.buffer.PacketBuffers;
import org.molecular.api.network.handler.NetworkHandler;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Louis
 */

public abstract class PacketPayload<T extends NetworkHandler> implements Packet<T> {

    private String channel;
    private PacketBuf payload;

    @PacketConstructor
    protected PacketPayload() {
    }

    protected PacketPayload(@Nonnull String channel, @Nonnull PacketBuf payload) {
        this.channel = channel;
        this.payload = payload;
    }

    @Override
    public final void writePacket(PacketBuf buffer) throws IOException {
        buffer.writeString(this.channel);
        buffer.writeVarInt(this.payload.readableBytes());
        buffer.writeBytes(this.payload);
    }

    @Override
    public final void readPacket(PacketBuf buffer) throws IOException {
        this.channel = buffer.readString();
        int size = buffer.readVarInt();
        this.payload = PacketBuffers.wrappedBuffer(buffer.readBytes(size));
    }

    protected void release() {
        this.payload.release();
    }

    public final String getChannel() {
        return this.channel;
    }

    public final PacketBuf getPayload() {
        return this.payload;
    }
}
