/*
 * This file ("CommandReader.java") is part of the molecular-project by Louis.
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

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.molecular.api.Molecular;
import org.molecular.api.command.Command;
import org.molecular.common.MolecularHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * @author Louis
 */

public class CommandReader extends Thread {

    private final Splitter splitter = Splitter.on(' ').trimResults().omitEmptyStrings();

    public CommandReader() {
        super("Command Console Reader");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        try (InputStreamReader streamReader = new InputStreamReader(System.in)) {
            try (BufferedReader reader = new BufferedReader(streamReader)) {
                String line;
                while ((line = reader.readLine()) != null) {

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] strings = Iterables.toArray(splitter.split(line), String.class);

                    if (strings.length != 0) {
                        Optional<Command> optional = Optional.ofNullable(Molecular.COMMAND_REGISTRY.getValue(strings[0]));
                        optional.ifPresent(command -> {
                            Callable<Object> callable = Executors.callable(() -> command.execute(this.omitFirst(strings)));
                            MolecularHandler.instance().getHandler().getApplication().scheduleSyncDelayedTask(callable);
                        });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] omitFirst(String[] strings) {
        return Arrays.copyOfRange(strings, 1, strings.length);
    }
}
