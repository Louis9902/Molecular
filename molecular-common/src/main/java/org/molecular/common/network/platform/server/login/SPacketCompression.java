/*
 * This file ("SPacketCompression.java") is part of the molecular-project by Louis.
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
import org.molecular.api.network.packet.Packet;
import org.molecular.api.network.packet.PacketConstructor;
import org.molecular.common.network.platform.client.NetworkHandlerClientLogin;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Louis
 */

public class SPacketCompression implements Packet<NetworkHandlerClientLogin> {

    private int threshold;

    @PacketConstructor
    private SPacketCompression() {
    }

    public SPacketCompression(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void writePacket(PacketBuf buffer) throws IOException {
        buffer.writeVarInt(this.threshold);
    }

    @Override
    public void readPacket(PacketBuf buffer) throws IOException {
        this.threshold = buffer.readVarInt();
    }

    @Override
    public void notifyHandler(@Nonnull NetworkHandlerClientLogin handler, @Nonnull NetworkChannel channel) {
        handler.processCompression(this, channel);
    }

    public int getThreshold() {
        return this.threshold;
    }
}
