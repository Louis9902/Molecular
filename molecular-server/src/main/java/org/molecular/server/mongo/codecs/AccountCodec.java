/*
 * This file ("AccountCodec.java") is part of the molecular-project by Louis.
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

package org.molecular.server.mongo.codecs;

import com.google.common.base.Charsets;
import com.mongodb.MongoClient;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.molecular.api.accounts.Account;
import org.molecular.server.accounts.SimpleAccount;

import java.util.Base64;
import java.util.UUID;

/**
 * @author Louis
 */

public class AccountCodec implements Codec<Account> {

    private final CodecRegistry registry;

    public AccountCodec() {
        this(MongoClient.getDefaultCodecRegistry());
    }

    public AccountCodec(CodecRegistry registry) {
        this.registry = registry;
    }

    private static String encodeBase64(String value) {
        return new String(Base64.getEncoder().encode(value.getBytes(Charsets.UTF_8)), Charsets.UTF_8);
    }

    private static String decodeBase64(String value) {
        return new String(Base64.getDecoder().decode(value.getBytes(Charsets.UTF_8)), Charsets.UTF_8);
    }

    @Override
    public Account decode(BsonReader reader, DecoderContext context) {
        Account account;
        reader.readStartDocument();
        {
            reader.readName("_id");
            UUID identifier = context.decodeWithChildContext(this.registry.get(UUID.class), reader);

            reader.readName("username");
            String username = reader.readString();

            reader.readName("password");
            String password = decodeBase64(reader.readString());

            account = new SimpleAccount(identifier, username, password);
        }
        reader.readEndDocument();
        return account;
    }

    @Override
    public void encode(BsonWriter writer, Account value, EncoderContext context) {
        writer.writeStartDocument();
        {
            writer.writeName("_id");
            context.encodeWithChildContext(this.registry.get(UUID.class), writer, value.identifier());

            writer.writeName("username");
            writer.writeString(value.username());

            writer.writeName("password");
            writer.writeString(encodeBase64(value.password()));
        }
        writer.writeEndDocument();
    }

    @Override
    public Class<Account> getEncoderClass() {
        return Account.class;
    }
}
