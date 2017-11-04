/*
 * This file ("CPacketRevision.java") is part of the molecular-project by Louis.
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

package org.molecular.common.network.platform.client.work;

import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.buffer.PacketBuf;
import org.molecular.api.network.packet.Packet;
import org.molecular.api.network.packet.PacketConstructor;
import org.molecular.common.network.platform.server.NetworkHandlerServerWork;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Louis
 */

public class CPacketRevision implements Packet<NetworkHandlerServerWork> {

    private String resource;

    @PacketConstructor
    private CPacketRevision() {
    }

    public CPacketRevision(@Nonnull String resource) {
        this.resource = resource;
    }

    @Override
    public void writePacket(PacketBuf buffer) throws IOException {
        buffer.writeString(this.resource);
    }

    @Override
    public void readPacket(PacketBuf buffer) throws IOException {
        this.resource = buffer.readString();
    }

    @Override
    public void notifyHandler(@Nonnull NetworkHandlerServerWork handler, @Nonnull NetworkChannel channel) {
        handler.processRevision(this, channel);
    }

    public String getResource() {
        return this.resource;
    }
}
