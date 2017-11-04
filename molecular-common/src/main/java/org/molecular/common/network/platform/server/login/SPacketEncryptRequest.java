/*
 * This file ("SPacketEncryptRequest.java") is part of the molecular-project by Louis.
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
import org.molecular.common.crypt.CipherManager;
import org.molecular.common.network.platform.client.NetworkHandlerClientLogin;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.security.PublicKey;

/**
 * @author Louis
 */

public class SPacketEncryptRequest implements Packet<NetworkHandlerClientLogin> {

    private PublicKey publicKey;
    private byte[] token;

    @PacketConstructor
    private SPacketEncryptRequest() {
    }

    public SPacketEncryptRequest(@Nonnull PublicKey publicKey, @Nonnull byte[] token) {
        this.publicKey = publicKey;
        this.token = token;
    }

    @Override
    public void writePacket(PacketBuf buffer) throws IOException {
        buffer.writeByteArray(this.publicKey.getEncoded());
        buffer.writeByteArray(this.token);
    }

    @Override
    public void readPacket(PacketBuf buffer) throws IOException {
        this.publicKey = CipherManager.decodePublicKey(buffer.readByteArray());
        this.token = buffer.readByteArray();
    }

    @Override
    public void notifyHandler(@Nonnull NetworkHandlerClientLogin handler, @Nonnull NetworkChannel channel) {
        handler.processEncryptRequest(this, channel);
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public byte[] getToken() {
        return this.token;
    }
}
