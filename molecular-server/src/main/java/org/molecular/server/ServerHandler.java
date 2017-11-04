/*
 * This file ("ServerHandler.java") is part of the molecular-project by Louis.
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

import org.molecular.api.network.NetworkBootstrap;
import org.molecular.api.platform.Platform;
import org.molecular.api.platform.PlatformDelegate;
import org.molecular.api.platform.PlatformHandler;
import org.molecular.api.platform.PlatformPersistent;
import org.molecular.common.MolecularHandler;
import org.molecular.common.persist.PersistentStash;
import org.molecular.common.platform.DefaultPersistent;
import org.molecular.server.persist.MongoPersistentStash;

import javax.annotation.Nonnull;

/**
 * @author Louis
 */

public final class ServerHandler implements PlatformHandler<ServerApplication> {

    private static final ServerHandler instance = new ServerHandler();

    private final ServerDelegate delegate;

    private ServerApplication server;
    private PlatformPersistent persistent;
    private PersistentStash persistentStash;

    private ServerHandler() {
        this.delegate = new ServerDelegate();
    }

    public static ServerHandler instance() {
        return ServerHandler.instance;
    }

    @Override
    public void inject(@Nonnull ServerApplication application) {
        this.server = application;
        MolecularHandler.instance().inject(this);
        Internals.inject(application);
    }

    @Override
    public void loading() {
        this.persistent = new DefaultPersistent();
    }

    @Override
    public void initialize() {
        this.persistentStash = new MongoPersistentStash(this.server.mongoLink);
    }

    @Override
    public void complete() {
    }

    @Override
    public void terminate() {
    }

    @Override
    public void destroy() {
        this.persistent.writeToFile();
    }

    @Override
    public Platform getPlatform() {
        return Platform.SERVER;
    }

    @Override
    public PlatformPersistent getPersistent() {
        return this.persistent;
    }

    @Override
    public PlatformDelegate getDelegate() {
        return this.delegate;
    }

    @Override
    public NetworkBootstrap getBootstrap() {
        return this.server.network;
    }

    @Override
    public ServerApplication getApplication() {
        return this.server;
    }

    public PersistentStash getPersistentStash() {
        return this.persistentStash;
    }
}
