/*
 * This file ("InjectorUtils.java") is part of the molecular-project by Louis.
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

package org.molecular.common.plugin.injector;

import com.google.common.base.Strings;
import org.molecular.common.plugin.analysis.DataTrace;
import org.molecular.common.plugin.analysis.DataWatcher;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Louis
 */

public final class InjectorUtils {

    public static Optional<String> fetchOwnerPlugin(Logger logger, Collection<DataTrace> plugins, DataTrace annotation) {
        String owner = (String) annotation.descriptor.get("owner");

        if (Strings.isNullOrEmpty(owner)) {
            String plugin = DataWatcher.getOwnerPlugin(plugins, annotation).orElse(null);

            if (Strings.isNullOrEmpty(plugin)) {
                logger.error("Could not find owning plugin for @{} on {} located at source {}", annotation.annotation, annotation.member, annotation.source.source());
                return Optional.empty();
            } else {
                owner = plugin;
            }
        }

        return Optional.of(owner);
    }

}
