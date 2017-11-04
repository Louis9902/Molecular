/*
 * This file ("LaunchCommands.java") is part of the molecular-project by Louis.
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

package org.molecular.common.launch;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static java.util.Arrays.asList;

/**
 * @author Louis
 */

public final class LaunchCommands {

    private static final OptionParser parser = new OptionParser();

    /*--- Options to parse via present ---*/

    public static final OptionSpec<Void> HELP = parser
            .acceptsAll(asList("help", "h", "?"), "Show this help text")
            .forHelp();

    public static final OptionSpec<Void> VERSION = parser
            .acceptsAll(asList("version", "v"), "Display the Molecular version");

    /*--- Options to parse via value ---*/

    public static final OptionSpec<Boolean> REDIRECT_STDOUT = parser
            .accepts("redirect-stdout", "Redirect standard output to the logger")
            .withRequiredArg()
            .ofType(Boolean.class)
            .defaultsTo(Boolean.TRUE)
            .describedAs("Redirect standard output");

    public static final OptionSpec<String> LOG_LEVEL = parser
            .acceptsAll(asList("lvl", "log-level"), "The level for the application logger")
            .withRequiredArg()
            .ofType(String.class)
            .defaultsTo("INFO")
            .describedAs("Default logging level");

    public static final OptionSpec<File> DIRECTORY = parser
            .acceptsAll(asList("d", "directory"), "The directory to store the application files")
            .withRequiredArg()
            .ofType(File.class)
            .defaultsTo(new File(""))
            .describedAs("System directory");

    public static final OptionSpec<String> HOST = parser
            .acceptsAll(asList("h", "host"), "The host to launch the application on")
            .withRequiredArg()
            .ofType(String.class)
            .defaultsTo("localhost")
            .describedAs("Network Host");

    public static final OptionSpec<Integer> PORT = parser
            .acceptsAll(asList("p", "port"), "The port to launch the application on")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(25565)
            .describedAs("Network Port");

    private static OptionSet options = null;

    public static OptionSet parse(String... args) throws OptionException {
        if (LaunchCommands.options == null) {
            LaunchCommands.options = LaunchCommands.parser.parse(args);
        }
        return LaunchCommands.options;
    }

    public static void printHelp(OutputStream out) throws IOException {
        LaunchCommands.parser.printHelpOn(out);
    }

}
