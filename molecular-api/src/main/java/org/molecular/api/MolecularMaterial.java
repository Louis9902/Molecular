/*
 * This file ("MolecularMaterial.java") is part of the molecular-project by Louis.
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

package org.molecular.api;

import org.molecular.api.network.ConstNetwork;
import org.molecular.api.network.NetworkProtocol;
import org.molecular.api.registry.Registry;
import org.molecular.api.util.APIInternal;

/**
 * @author Louis
 */

public final class MolecularMaterial {

    public static final NetworkProtocol PROTOCOL_HANDSHAKE = find(ConstNetwork.HANDSHAKE, Molecular.PROTOCOL_REGISTRY);
    public static final NetworkProtocol PROTOCOL_LOGIN = find(ConstNetwork.LOGIN, Molecular.PROTOCOL_REGISTRY);
    public static final NetworkProtocol PROTOCOL_WORK = find(ConstNetwork.WORK, Molecular.PROTOCOL_REGISTRY);

    @APIInternal
    private static <K, V> V find(K key, Registry<K, V> registry) {
        return registry.getValueSafe(key);
    }

}
