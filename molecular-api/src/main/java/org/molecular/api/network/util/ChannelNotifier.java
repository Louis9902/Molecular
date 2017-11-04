/*
 * This file ("ChannelNotifier.java") is part of the molecular-project by Louis.
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

package org.molecular.api.network.util;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.function.Consumer;

/**
 * @author Louis
 */

public class ChannelNotifier<T> implements ChannelFutureListener {

    private final T channel;
    private final Consumer<T> consumer;

    public ChannelNotifier(T channel, Consumer<T> consumer) {
        this.channel = channel;
        this.consumer = consumer;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        this.consumer.accept(this.channel);
    }
}
