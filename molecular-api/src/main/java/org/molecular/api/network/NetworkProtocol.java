/*
 * This file ("NetworkProtocol.java") is part of the molecular-project by Louis.
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

package org.molecular.api.network;

import org.molecular.api.network.packet.Packet;
import org.molecular.api.platform.Platform;
import org.molecular.api.resources.text.BaseTextPart;
import org.molecular.api.util.Identifiable;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Louis
 */

public interface NetworkProtocol extends Identifiable<Integer> {

    void register(Platform platform, @Nonnull Class<? extends Packet> clazz);

    Optional<Integer> get(@Nonnull Platform platform, Class<? extends Packet> clazz);

    Optional<Class<? extends Packet>> get(@Nonnull Platform platform, Integer index);

    Optional<Packet> createDisconnectPacket(@Nonnull BaseTextPart reason);

}
