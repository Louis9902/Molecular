/*
 * This file ("CPacketEncryptReply.java") is part of the molecular-project by Louis.
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
import org.molecular.common.crypt.CipherManager;
import org.molecular.api.network.packet.PacketConstructor;
import org.molecular.common.network.platform.server.NetworkHandlerServerLogin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Louis
 */

public class CPacketEncryptReply implements Packet<NetworkHandlerServerLogin> {

    private byte[] secretKeyEncrypted;
    private byte[] token;

    @PacketConstructor
    private CPacketEncryptReply() {
        this.secretKeyEncrypted = new byte[0];
        this.token = new byte[0];
    }

    public CPacketEncryptReply(@Nonnull SecretKey secret, @Nonnull PublicKey key, @Nonnull byte[] token) {
        this.secretKeyEncrypted = CipherManager.encryptData(key, secret.getEncoded());
        this.token = CipherManager.encryptData(key, token);
    }

    @Override
    public void writePacket(@Nonnull PacketBuf buffer) throws IOException {
        buffer.writeByteArray(this.secretKeyEncrypted);
        buffer.writeByteArray(this.token);
    }

    @Override
    public void readPacket(@Nonnull PacketBuf buffer) throws IOException {
        this.secretKeyEncrypted = buffer.readByteArray();
        this.token = buffer.readByteArray();
    }

    @Override
    public void notifyHandler(@Nonnull NetworkHandlerServerLogin handler, @Nonnull NetworkChannel channel) {
        handler.processEncryptReply(this, channel);
    }

    public SecretKey secretKey(@Nonnull PrivateKey key) {
        return CipherManager.decryptSharedKey(key, this.secretKeyEncrypted);
    }

    public byte[] token(@Nullable PrivateKey key) {
        return key == null ? this.token : CipherManager.decryptData(key, this.token);
    }
}
