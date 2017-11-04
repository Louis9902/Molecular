/*
 * This file ("DefaultPersistent.java") is part of the molecular-project by Louis.
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

package org.molecular.common.platform;

import org.molecular.api.Molecular;
import org.molecular.api.binary.DataCluster;
import org.molecular.api.binary.io.DataParcelReader;
import org.molecular.api.binary.io.DataParcelWriter;
import org.molecular.api.binary.parcel.BaseDataParcel;
import org.molecular.api.binary.parcel.DataParcelMap;
import org.molecular.api.platform.PlatformPersistent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author Louis
 */

public class DefaultPersistent implements PlatformPersistent {

    private final Logger logger = LoggerFactory.getLogger("org.molecular.util");

    private final DataCluster cluster;

    public DefaultPersistent() {
        this.cluster = new DataCluster();
        this.readFromFile();
    }

    @Override
    public DataCluster getCluster() {
        return this.cluster;
    }

    @Override
    public void writeToFile() {
        Path file = Molecular.getResourceController().getPersistentFile();

        try (FileOutputStream stream = new FileOutputStream(new File(file.toUri()))) {
            try (DataParcelWriter writer = new DataParcelWriter(stream)) {
                writer.writeParcel(new DataParcelMap("", this.cluster));
            }
        } catch (IOException e) {
            logger.error("Failed to write to persistent file ({})", file, e);
        }

    }

    @Override
    public void readFromFile() {
        Path file = Molecular.getResourceController().getPersistentFile();

        if (!Files.isRegularFile(file)) {
            return;
        }

        try (InputStream stream = new FileInputStream(new File(file.toUri()))) {
            try (DataParcelReader reader = new DataParcelReader(stream)) {
                BaseDataParcel<?> parcel = reader.readParcel();
                if (parcel instanceof DataParcelMap) {
                    DataCluster entries = ((DataParcelMap) parcel).get();
                    for (Map.Entry<String, BaseDataParcel<?>> entry : entries) {
                        this.cluster.add(entry.getValue());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read from persistent file ({})", file, e);
        }
    }
}
