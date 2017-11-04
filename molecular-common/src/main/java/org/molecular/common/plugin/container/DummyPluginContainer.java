/*
 * This file ("DummyPluginContainer.java") is part of the molecular-project by Louis.
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

package org.molecular.common.plugin.container;

import org.molecular.api.plugin.event.MPLTransmitEvent;
import org.molecular.api.plugin.meta.PluginMetadata;
import org.molecular.common.plugin.PluginLoader;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author Louis
 */

public class DummyPluginContainer extends AbstractPluginContainer {

    public DummyPluginContainer(@Nonnull PluginMetadata metadata) {
        super(metadata);
    }

    @Override
    public void assemble(@Nonnull PluginLoader loader) throws Exception {
    }

    @Override
    public void transmit(@Nonnull MPLTransmitEvent event) throws Exception {
    }

    @Nonnull
    @Override
    public Optional<Path> source() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<Object> instance() {
        return Optional.empty();
    }
}
