/*
 * This file ("PacketDisconnect.java") is part of the molecular-project by Louis.
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

import org.molecular.api.binary.parcel.DataParcelText;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.buffer.PacketBuf;
import org.molecular.api.network.handler.NetworkHandler;
import org.molecular.api.resources.text.BaseTextPart;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Louis
 */

public abstract class PacketDisconnect<T extends NetworkHandler> implements Packet<T> {

    private BaseTextPart reason;

    protected PacketDisconnect() {
    }

    protected PacketDisconnect(@Nonnull BaseTextPart reason) {
        this.reason = reason;
    }

    @Override
    public final void writePacket(PacketBuf buffer) throws IOException {
        buffer.writeBinaryData(new DataParcelText("reason", this.reason));
    }

    @Override
    public final void readPacket(PacketBuf buffer) throws IOException {
        this.reason = ((DataParcelText) buffer.readBinaryData()).get();
    }

    @Override
    public final void notifyHandler(@Nonnull T handler, @Nonnull NetworkChannel channel) {
        handler.processDisconnect(this, channel);
    }

    public final BaseTextPart getReason() {
        return this.reason;
    }
}
