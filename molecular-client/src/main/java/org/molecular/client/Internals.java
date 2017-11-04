/*
 * This file ("Internals.java") is part of the molecular-project by Louis.
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

package org.molecular.client;

import org.molecular.api.Molecular;
import org.molecular.api.event.EventController;
import org.molecular.api.events.network.channel.ChannelActiveEvent;
import org.molecular.api.network.handler.NetworkHandler;
import org.molecular.client.network.LoginHandler;
import org.molecular.client.network.WorkHandler;

/**
 * @author Louis
 */

public final class Internals {

    public static NetworkHandler handler_login;
    public static NetworkHandler handler_work;

    public static void inject(ClientApplication application) {
        Internals.handler_login = new LoginHandler(application);
        Internals.handler_work = new WorkHandler(application);

        Internals.channelListener();
    }

    private static void channelListener() {
        EventController controller = Molecular.getEventController();
        controller.registerListener(ChannelActiveEvent.class, event -> event.getChannel().switchNetworkHandler(handler_login));
    }

}
