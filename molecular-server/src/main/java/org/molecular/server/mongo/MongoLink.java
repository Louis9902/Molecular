/*
 * This file ("MongoLink.java") is part of the molecular-project by Louis.
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

package org.molecular.server.mongo;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.molecular.api.accounts.Account;
import org.molecular.server.mongo.codecs.AccountCodec;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.MongoCredential.createCredential;
import static java.util.Collections.singletonList;
import static org.bson.codecs.configuration.CodecRegistries.*;

/**
 * @author Louis
 */

public class MongoLink {

    private final List<Codec<?>> codecs = new ArrayList<>();
    private final SocketAddress address;

    private MongoClient client;
    private MongoDatabase database;

    private CodecRegistry registry;

    private boolean connected;

    {
        this.codecs.add(new AccountCodec());
    }

    public MongoLink(@Nonnull SocketAddress address) {
        this.address = address;
    }

    @CanIgnoreReturnValue
    public MongoLink connect(String database) throws Exception {
        this.buildCodecRegistry();
        this.client = new MongoClient(new ServerAddress((InetSocketAddress) this.address));
        this.database = this.client.getDatabase(database).withCodecRegistry(this.registry);
        this.connected = true;
        return this;
    }

    @CanIgnoreReturnValue
    public MongoLink connect(String database, String username, String password) throws Exception {
        this.buildCodecRegistry();
        MongoCredential credential = createCredential(username, database, password.toCharArray());
        this.client = new MongoClient(new ServerAddress((InetSocketAddress) this.address), singletonList(credential));
        this.database = this.client.getDatabase(database).withCodecRegistry(this.registry);
        this.connected = true;
        return this;
    }

    @CanIgnoreReturnValue
    public MongoLink disconnect() throws Exception {
        this.client.close();
        this.connected = false;
        return this;
    }

    private void buildCodecRegistry() {
        CodecRegistry registry = fromProviders(new CodecProvider() {
            @Override
            public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
                if (Account.class.isAssignableFrom(clazz)) {
                    return (Codec<T>) registry.get(Account.class);
                }
                return null;
            }
        });
        this.registry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromCodecs(this.codecs), registry);
    }

    public boolean isConnected() {
        return this.connected;
    }

    public Optional<MongoClient> client() {
        return Optional.ofNullable(this.client);
    }

    public Optional<MongoDatabase> database() {
        return Optional.ofNullable(this.database);
    }

}
