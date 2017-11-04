/*
 * This file ("NetworkHandlerClientLogin.java") is part of the molecular-project by Louis.
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

package org.molecular.common.network.platform.client;

import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.handler.NetworkHandler;
import org.molecular.common.network.platform.server.login.SPacketLoginRequest;
import org.molecular.common.network.platform.server.login.SPacketCompression;
import org.molecular.common.network.platform.server.login.SPacketEncryptRequest;
import org.molecular.common.network.platform.server.login.SPacketLoginSuccess;

/**
 * @author Louis
 */

public interface NetworkHandlerClientLogin extends NetworkHandler {

    void processEncryptRequest(SPacketEncryptRequest packet, NetworkChannel channel);

    void processLoginRequest(SPacketLoginRequest packet, NetworkChannel channel);

    void processCompression(SPacketCompression packet, NetworkChannel channel);

    void processLoginSuccess(SPacketLoginSuccess packet, NetworkChannel channel);
}
