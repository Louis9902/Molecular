/*
 * This file ("ClientHandler.java") is part of the molecular-project by Louis.
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

import org.molecular.api.MolecularMaterial;
import org.molecular.api.network.NetworkBootstrap;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.util.ChannelNotifier;
import org.molecular.api.platform.Platform;
import org.molecular.api.platform.PlatformDelegate;
import org.molecular.api.platform.PlatformHandler;
import org.molecular.api.platform.PlatformPersistent;
import org.molecular.common.MolecularHandler;
import org.molecular.common.network.platform.client.handshake.CPacketHandshake;
import org.molecular.common.network.platform.client.login.CPacketLoginStart;
import org.molecular.common.platform.DefaultPersistent;

import javax.annotation.Nonnull;
import java.util.Collections;

/**
 * @author Louis
 */

public final class ClientHandler implements PlatformHandler<ClientApplication> {

    public static final String USERNAME = "Louis9902";

    private static final ClientHandler instance = new ClientHandler();

    private final ClientDelegate delegate;

    private ClientApplication client;
    private PlatformPersistent persistent;

    private ClientHandler() {
        this.delegate = new ClientDelegate();
    }

    public static ClientHandler instance() {
        return ClientHandler.instance;
    }

    @Override
    public void inject(@Nonnull ClientApplication application) {
        this.client = application;
        MolecularHandler.instance().inject(this);
        Internals.inject(application);
    }

    @Override
    public void loading() {
        this.persistent = new DefaultPersistent();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void complete() {
        ChannelNotifier<NetworkChannel> notifier = new ChannelNotifier<>(this.client.channel, channel -> {
            this.client.channel.switchNetworkProtocol(MolecularMaterial.PROTOCOL_LOGIN);
            boolean exists = this.persistent.getCluster().contains("password");
            this.client.channel.writeAndFlushPacket(new CPacketLoginStart(exists ? null : USERNAME));
        });
        this.client.channel.writeAndFlushPacket(new CPacketHandshake(client.getProtocol(), client.getVersion(), Collections.emptyList()), notifier);
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
        return Platform.CLIENT;
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
        return this.client.network;
    }

    @Override
    public ClientApplication getApplication() {
        return this.client;
    }

    public NetworkChannel getChannel() {
        return this.client.channel;
    }

}
