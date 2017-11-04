/*
 * This file ("PacketEncryptingEncoder.java") is part of the molecular-project by Louis.
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

package org.molecular.common.network.codecs.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.molecular.common.crypt.CipherTranslator;
import org.molecular.common.network.codecs.ByteToByteEncoder;

import javax.crypto.Cipher;

/**
 * @author Louis
 */

public class PacketEncryptingEncoder extends ByteToByteEncoder {

    private final CipherTranslator translator;

    public PacketEncryptingEncoder(Cipher cipher) {
        this.translator = new CipherTranslator(cipher);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        this.translator.cipher(msg, out);
    }

}
