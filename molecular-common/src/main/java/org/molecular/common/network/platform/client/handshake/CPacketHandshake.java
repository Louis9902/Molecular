/*
 * This file ("CPacketHandshake.java") is part of the molecular-project by Louis.
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

package org.molecular.common.network.platform.client.handshake;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.buffer.PacketBuf;
import org.molecular.api.network.packet.Packet;
import org.molecular.api.network.packet.PacketConstructor;
import org.molecular.common.network.platform.server.NetworkHandlerServerHandshake;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Louis
 */

public class CPacketHandshake implements Packet<NetworkHandlerServerHandshake> {

    private static final Joiner JOINER = Joiner.on(';');
    private static final Splitter SPLITTER = Splitter.on(';');

    private int protocol;
    private int version;
    private List<String> plugins;

    @PacketConstructor
    private CPacketHandshake() {
        this.plugins = new ArrayList<>();
    }

    public CPacketHandshake(int protocol, int version, @Nonnull List<String> plugins) {
        this.protocol = protocol;
        this.version = version;
        this.plugins = plugins;
    }

    @Override
    public void writePacket(PacketBuf buffer) throws IOException {
        buffer.writeVarInt(this.protocol);
        buffer.writeVarInt(this.version);
        buffer.writeString(JOINER.join(this.plugins));
    }

    @Override
    public void readPacket(PacketBuf buffer) throws IOException {
        this.protocol = buffer.readVarInt();
        this.version = buffer.readVarInt();
        this.plugins = SPLITTER.splitToList(buffer.readString());
    }

    @Override
    public void notifyHandler(@Nonnull NetworkHandlerServerHandshake handler, @Nonnull NetworkChannel channel) {
        handler.notifyHandshake(this, channel);
    }

    public int getProtocol() {
        return this.protocol;
    }

    public int getVersion() {
        return this.version;
    }

    public List<String> getPlugins() {
        return this.plugins;
    }
}
