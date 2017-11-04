/*
 * This file ("ServerApplication.java") is part of the molecular-project by Louis.
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

package org.molecular.server;

import org.molecular.api.Molecular;
import org.molecular.api.MolecularSettings;
import org.molecular.api.network.NetworkBootstrap;
import org.molecular.common.MolecularApplication;
import org.molecular.common.crypt.CipherManager;
import org.molecular.common.launch.LaunchHeader;
import org.molecular.common.network.DefaultNetBootstrap;
import org.molecular.server.mongo.MongoLink;

import java.net.SocketAddress;
import java.nio.file.Path;
import java.security.KeyPair;

import static org.molecular.common.MolecularHandler.instance;

/**
 * @author Louis
 */

public final class ServerApplication extends MolecularApplication {

    MongoLink mongoLink;
    NetworkBootstrap network;

    private KeyPair keys;

    public ServerApplication(Path directory, SocketAddress address) {
        super(ServerApplication.class, directory, address);
        logger.info(LaunchHeader.instance().print());
        ServerHandler.instance().inject(this);
    }

    @Override
    protected boolean doFirstTick() {
        new CommandReader().start();
        logger.info("This server is running molecular version {} (Implementing API version {})", this.semantic, Molecular.version());
        instance().loading();

        logger.info("Generating server keypair for packet encryption");
        this.keys = CipherManager.generateKeyPair();

        logger.info("Connecting to database '{}' @ {}", MolecularSettings.database_name, MolecularSettings.database_address);
        this.mongoLink = new MongoLink(MolecularSettings.database_address);
        try {
            this.mongoLink.connect(MolecularSettings.database_name);
        } catch (Throwable throwable) {
            logger.warn("**** FAILED TO CONNECT TO DATABASE! ****");
            logger.warn("The exception was:", throwable);
            return false;
        }

        instance().initialize();

        this.network = new DefaultNetBootstrap(this);
        logger.info("Start listening for connections @ {}", this.address);
        try {
            this.network.bind(this.address);
        } catch (Throwable throwable) {
            logger.warn("**** FAILED TO START LISTENER FOR CONNECTIONS! ****");
            logger.warn("The exception was:", throwable);
            return false;
        }

        return true;
    }

    @Override
    protected boolean doLastTick() {
        logger.info("Stop listening for connections @ {}", this.address);
        try {
            this.network.close();
        } catch (Throwable throwable) {
            logger.warn("**** FAILED TO STOP LISTENER FOR CONNECTIONS! ****");
            logger.warn("The exception was:", throwable);
            return false;
        }

        instance().terminate();

        logger.info("Disconnecting from database 'molecular' @ {}", "localhost:27017");
        try {
            this.mongoLink.disconnect();
        } catch (Throwable throwable) {
            logger.warn("**** FAILED TO DISCONNECT FROM DATABASE! ****");
            logger.warn("The exception was:", throwable);
            return false;
        }

        instance().destroy();
        return true;
    }

    @Override
    protected void doRunningTick() {
        this.execute();
    }

    public KeyPair getKeyPair() {
        return this.keys;
    }
}
