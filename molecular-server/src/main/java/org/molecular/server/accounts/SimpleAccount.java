/*
 * This file ("SimpleAccount.java") is part of the molecular-project by Louis.
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

package org.molecular.server.accounts;

import org.molecular.api.accounts.Account;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author Louis
 */

public class SimpleAccount implements Account {

    private final UUID identifier;
    private final String password;
    private final String username;

    public SimpleAccount(@Nonnull UUID identifier, @Nonnull String username, @Nonnull String password) {
        this.identifier = identifier;
        this.username = username;
        this.password = password;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String password() {
        return this.password;
    }

    @Override
    public boolean authenticate(@Nonnull String password) {
        return this.password.equals(password.trim());
    }

    @Nonnull
    @Override
    public UUID identifier() {
        return this.identifier;
    }
}
