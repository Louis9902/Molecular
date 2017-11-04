/*
 * This file ("AccountCreateEvent.java") is part of the molecular-project by Louis.
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

package org.molecular.api.events.accounts;

import org.molecular.api.accounts.Account;
import org.molecular.api.event.Event;
import org.molecular.api.event.EventHandlers;

import javax.annotation.Nonnull;

/**
 * @author Louis
 */

public class AccountCreateEvent extends Event<AccountCreateEvent> {

    private static final EventHandlers<AccountCreateEvent> HANDLER = new EventHandlers<>();

    private final Account account;

    public AccountCreateEvent(Account account) {
        this.account = account;
    }

    public static EventHandlers<AccountCreateEvent> getEventHandlers() {
        return AccountCreateEvent.HANDLER;
    }

    @Nonnull
    @Override
    public EventHandlers<AccountCreateEvent> getHandlers() {
        return AccountCreateEvent.HANDLER;
    }

    public Account getAccount() {
        return this.account;
    }
}
