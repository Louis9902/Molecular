/*
 * This file ("MongoPersistentStash.java") is part of the molecular-project by Louis.
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

package org.molecular.server.persist;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.molecular.api.accounts.Account;
import org.molecular.common.event.MolecularEventFactory;
import org.molecular.common.persist.PersistentStash;
import org.molecular.server.accounts.SimpleAccount;
import org.molecular.server.mongo.MongoLink;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Louis
 */

public class MongoPersistentStash implements PersistentStash {

    private final MongoCollection<Account> accounts;

    public MongoPersistentStash(@Nonnull MongoLink link) {
        if (link.database().isPresent()) {
            this.accounts = link.database().get().getCollection("accounts", Account.class);
        } else {
            throw new IllegalStateException("MongoLink not connected to database");
        }
    }

    @Override
    public Account createAccount(@Nonnull String username, @Nonnull String password) {
        Account account = new SimpleAccount(UUID.randomUUID(), username, password);
        MolecularEventFactory.instance().callAccountCreateEvent(account);
        this.accounts.insertOne(account);
        return account;
    }

    @Override
    public Optional<Account> findAccount(@Nonnull UUID identifier) {
        return Optional.ofNullable(this.accounts.find(Filters.eq(identifier)).first());
    }

    @Override
    public Optional<Account> findAccount(@Nonnull String username) {
        return Optional.ofNullable(this.accounts.find(Filters.eq("username", username)).first());
    }
}
