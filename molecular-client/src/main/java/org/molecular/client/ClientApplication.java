/*
 * This file ("ClientApplication.java") is part of the molecular-project by Louis.
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
import org.molecular.api.network.NetworkBootstrap;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.resources.text.TextPartTranslation;
import org.molecular.common.MolecularApplication;
import org.molecular.common.launch.LaunchHeader;
import org.molecular.common.network.DefaultNetBootstrap;
import org.molecular.common.network.platform.client.handshake.CPacketHandshake;

import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.Collections;

import static org.molecular.common.MolecularHandler.instance;

/**
 * @author Louis
 */

public final class ClientApplication extends MolecularApplication {

    NetworkBootstrap network;
    NetworkChannel channel;

    public ClientApplication(Path directory, SocketAddress address) {
        super(ClientApplication.class, directory, address);
        ClientHandler.instance().inject(this);
        logger.info(LaunchHeader.instance().print());
    }

    @Override
    protected boolean doFirstTick() {
        logger.info("This client is running molecular version {} (Implementing API version {})", this.semantic, Molecular.version());

        instance().loading();
        instance().initialize();

        this.network = new DefaultNetBootstrap(this);
        logger.info("Connecting @ {}", this.address);
        try {
            this.network.connect(this.address).ifPresent(c -> this.channel = c);
            channel.writeAndFlushPacket(new CPacketHandshake(1, 1, Collections.emptyList()), future -> channel.close(new TextPartTranslation("disconnect.def")));
        } catch (Throwable throwable) {
            logger.warn("**** FAILED TO CONNECT! ****");
            logger.warn("The exception was:", throwable);
            return false;
        }

        return true;
    }

    @Override
    protected boolean doLastTick() {
        logger.info("Disconnecting @ {}", this.address);
        try {
            this.network.close();
        } catch (Throwable throwable) {
            logger.warn("**** FAILED TO DISCONNECT! ****");
            logger.warn("The exception was:", throwable);
            return false;
        }

        instance().terminate();
        instance().destroy();

        return true;
    }

    @Override
    protected void doRunningTick() {
        this.execute();
    }

}
