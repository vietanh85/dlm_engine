/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hortonworks.beacon.replication.utils;

import com.hortonworks.beacon.exceptions.BeaconException;
import com.hortonworks.beacon.replication.fs.FSDRImpl;
import com.hortonworks.beacon.util.FileSystemClientFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.SnapshottableDirectoryStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;


public final class FSDRUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FSDRImpl.class);

    public static final String SNAPSHOT_PREFIX = "beacon-snapshot-";
    public static final String SNAPSHOT_DIR_PREFIX = ".snapshot";
    public static final String TDE_ENCRYPTION_ENABLED = "tdeEncryptionEnabled";

    private FSDRUtils() {
    }

    public static DistributedFileSystem getSourceFileSystem(String sourceStorageUrl,
                                                            Configuration conf) throws BeaconException {
        conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, sourceStorageUrl);
        return FileSystemClientFactory.get().createDistributedProxiedFileSystem(conf);
    }

    public static DistributedFileSystem getTargetFileSystem(String targetStorageUrl,
                                                            Configuration conf) throws BeaconException {
        conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, targetStorageUrl);
        return FileSystemClientFactory.get().createDistributedProxiedFileSystem(conf);
    }

    /* Path passed should be fully qualified absolute path */
    public static boolean isDirSnapshotable(DistributedFileSystem hdfs, Path path) throws BeaconException {
        if (path == null) {
            throw new BeaconException("isDirSnapshotable: Path cannot be null or empty");
        }
        try {
            LOG.info("Validating if dir : {} is snapshotable.", path.toString());
            URI pathUri = path.toUri();
            if (pathUri.getAuthority() == null) {
                LOG.error("{} is not fully qualified path", path);
                throw new BeaconException("isDirSnapshotable: " + path + " is not fully qualified path");
            }
            SnapshottableDirectoryStatus[] snapshotableDirs = hdfs.getSnapshottableDirListing();
            if (snapshotableDirs != null && snapshotableDirs.length > 0) {
                for (SnapshottableDirectoryStatus dir : snapshotableDirs) {
                    Path snapshotDirPath = dir.getFullPath();
                    URI snapshorDirUri = snapshotDirPath.toUri();
                    if (snapshorDirUri.getAuthority() == null) {
                        snapshotDirPath = new Path(hdfs.getUri().toString(), snapshotDirPath);
                    }
                    LOG.debug("snapshotDirPath: {}", snapshotDirPath);
                    if (path.toString().startsWith(snapshotDirPath.toString())) {
                        LOG.debug("isHCFS: {}", "true");
                        return true;
                    }
                }
            }
            return false;
        } catch (IOException e) {
            LOG.error("Unable to verify if dir : {} is snapshot-able. {}", path.toString(), e.getMessage());
            throw new BeaconException("Unable to verify if dir " + path.toString() + " is snapshot-able", e);
        }
    }

    public static boolean createSnapshotInFileSystem(String dirName, String snapshotName,
                                            FileSystem fs) throws BeaconException {
        boolean isSnapshot = false;
        try {
            LOG.info("Creating snapshot {} in directory {}", snapshotName, dirName);
            fs.createSnapshot(new Path(dirName), snapshotName);
            isSnapshot = true;
        } catch (IOException e) {
            LOG.warn("Unable to create snapshot {} in filesystem {}. Exception is {}",
                    snapshotName, fs.getConf().get(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY), e.getMessage());
            throw new BeaconException("Unable to create snapshot " + snapshotName, e);
        } finally {
            return isSnapshot;
        }
    }

    public static String getSnapshotDir(String dirName) {
        dirName = StringUtils.removeEnd(dirName, Path.SEPARATOR);
        return dirName + Path.SEPARATOR + SNAPSHOT_DIR_PREFIX + Path.SEPARATOR;
    }
}
