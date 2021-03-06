/**
 * HORTONWORKS DATAPLANE SERVICE AND ITS CONSTITUENT SERVICES
 *
 * (c) 2016-2018 Hortonworks, Inc. All rights reserved.
 *
 * This code is provided to you pursuant to your written agreement with Hortonworks, which may be the terms of the
 * Affero General Public License version 3 (AGPLv3), or pursuant to a written agreement with a third party authorized
 * to distribute this code.  If you do not have a written agreement with Hortonworks or with an authorized and
 * properly licensed third party, you do not have any rights to this code.
 *
 * If this code is provided to you under the terms of the AGPLv3:
 * (A) HORTONWORKS PROVIDES THIS CODE TO YOU WITHOUT WARRANTIES OF ANY KIND;
 * (B) HORTONWORKS DISCLAIMS ANY AND ALL EXPRESS AND IMPLIED WARRANTIES WITH RESPECT TO THIS CODE, INCLUDING BUT NOT
 *    LIMITED TO IMPLIED WARRANTIES OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE;
 * (C) HORTONWORKS IS NOT LIABLE TO YOU, AND WILL NOT DEFEND, INDEMNIFY, OR HOLD YOU HARMLESS FOR ANY CLAIMS ARISING
 *    FROM OR RELATED TO THE CODE; AND
 * (D) WITH RESPECT TO YOUR EXERCISE OF ANY RIGHTS GRANTED TO YOU FOR THE CODE, HORTONWORKS IS NOT LIABLE FOR ANY
 *    DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES INCLUDING, BUT NOT LIMITED TO,
 *    DAMAGES RELATED TO LOST REVENUE, LOST PROFITS, LOSS OF INCOME, LOSS OF BUSINESS ADVANTAGE OR UNAVAILABILITY,
 *    OR LOSS OR CORRUPTION OF DATA.
 */

package com.hortonworks.beacon.replication.hive;

import com.hortonworks.beacon.RequestContext;
import com.hortonworks.beacon.Timer;
import com.hortonworks.beacon.constants.BeaconConstants;
import com.hortonworks.beacon.entity.HiveDRProperties;
import com.hortonworks.beacon.entity.util.ClusterHelper;
import com.hortonworks.beacon.entity.util.HiveDRUtils;
import com.hortonworks.beacon.entity.util.hive.HiveClientFactory;
import com.hortonworks.beacon.entity.util.hive.HiveServerClient;
import com.hortonworks.beacon.entity.util.hive.ReplCommand;
import com.hortonworks.beacon.exceptions.BeaconException;
import com.hortonworks.beacon.job.JobContext;
import com.hortonworks.beacon.replication.InstanceReplication;
import com.hortonworks.beacon.replication.ReplicationJobDetails;
import com.hortonworks.beacon.replication.ReplicationUtils;
import com.hortonworks.beacon.util.FSUtils;
import com.hortonworks.beacon.util.HiveActionType;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static com.hortonworks.beacon.constants.BeaconConstants.DATASET_BOOTSTRAP;

/**
 * Export Hive Replication implementation.
 */

public class HiveExport extends InstanceReplication {

    private static final Logger LOG = LoggerFactory.getLogger(HiveExport.class);

    private String database;
    private String sourceConnectionString;
    private String targetConnectionString;
    private Statement sourceStatement = null;
    private ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(1);

    public HiveExport(ReplicationJobDetails details) {
        super(details);
        database = properties.getProperty(HiveDRProperties.SOURCE_DATASET.getName());
    }

    @Override
    public void init(JobContext jobContext) throws BeaconException {
        try {
            jobContext.getJobContextMap().remove(BeaconConstants.END_TIME);
            jobContext.getJobContextMap().put(BeaconConstants.START_TIME, String.valueOf(System.currentTimeMillis()));
            initializeProperties();
            sourceConnectionString = HiveDRUtils.getConnectionString(properties, HiveActionType.EXPORT);
            targetConnectionString = HiveDRUtils.getTargetConnectionString(properties);
        } catch (Exception e) {
            throw new BeaconException("Exception occurred initializing Hive Server: ", e);
        }
    }

    @Override
    public void perform(JobContext jobContext) throws BeaconException, InterruptedException {
        RequestContext requestContext = RequestContext.get();
        final String methodName = this.getClass().getSimpleName() + '.'
                + Thread.currentThread().getStackTrace()[1].getMethodName();
        Timer methodTimer = requestContext.startTimer(methodName);
        try {
            String dumpDirectory;
            long replEventId = getReplEventId();
            if (replEventId <= 0) {
                jobContext.getJobContextMap().put(DATASET_BOOTSTRAP, "true");
                jobContext.getJobContextMap().put(BeaconConstants.DATABASE_BOOTSTRAP, "true");
                dumpDirectory = jobContext.getJobContextMap().get(HiveExport.DUMP_DIRECTORY);
                if (StringUtils.isNotEmpty(dumpDirectory)) {
                    boolean dumpDirectoryExists = isDumpDirectoryExists(dumpDirectory);
                    if (!dumpDirectoryExists) {
                        LOG.info("Stored Bootstrap dump directory doesn't exists, re exporting it.");
                        dumpDirectory = performExport(jobContext);
                    } else {
                        LOG.info("Dump directory {} already exists for bootstrap run, skipping hive export",
                                dumpDirectory);
                    }
                } else {
                    LOG.info("Bootstrap dump directory doesn't exists, exporting it.");
                    dumpDirectory = performExport(jobContext);
                }
            } else {
                LOG.info("Incremental dump started");
                jobContext.getJobContextMap().put(BeaconConstants.DATABASE_BOOTSTRAP, "false");
                jobContext.getJobContextMap().remove(HiveExport.DUMP_DIRECTORY);
                dumpDirectory = performExport(jobContext);
            }
            if (StringUtils.isNotBlank(dumpDirectory)) {
                jobContext.getJobContextMap().put(DUMP_DIRECTORY, dumpDirectory);
                LOG.info("Beacon Hive export completed successfully");
            } else {
                LOG.info("Repl Dump Directory is null as there are no events to replicate");
            }
        } finally {
            methodTimer.stop();
        }
    }

    private boolean isDumpDirectoryExists(String dumpDirectory) throws BeaconException {
        String fsEndPoint = properties.getProperty(HiveDRProperties.SOURCE_NN.getName());
        String sourceClusterName = properties.getProperty(HiveDRProperties.SOURCE_CLUSTER_NAME.getName());
        FileSystem fileSystem = FSUtils.getFileSystem(fsEndPoint, ClusterHelper.getHAConfigurationOrDefault(
                sourceClusterName));
        try {
            return fileSystem.exists(new Path(dumpDirectory));
        } catch (IOException e) {
            throw new BeaconException(e);
        }
    }

    private String performExport(JobContext jobContext) throws BeaconException, InterruptedException {
        LOG.info("Performing export for database: {}", database);
        String sourceNN = properties.getProperty(HiveDRProperties.SOURCE_NN.getName());

        String dumpDirectory = null;
        ReplCommand replCommand = new ReplCommand(database);
        HiveServerClient sourceHiveClient = null;
        ResultSet res = null;
        try {
            if (jobContext.shouldInterrupt().get()) {
                throw new InterruptedException("before repl status");
            }

            sourceHiveClient = HiveClientFactory.getHiveServerClient(sourceConnectionString);

            long lastReplEventId = getReplEventId();
            LOG.debug("Last replicated event id for database: {} is {}", database, lastReplEventId);
            String replDump = replCommand.getReplDump(lastReplEventId);
            if (jobContext.shouldInterrupt().get()) {
                throw new InterruptedException("before repl dump");
            }
            sourceStatement = sourceHiveClient.createStatement();
            getHiveReplicationProgress(timer, jobContext, HiveActionType.EXPORT,
                    ReplicationUtils.getReplicationMetricsInterval(), sourceStatement);

            res = sourceStatement.executeQuery(replDump);
            long currReplEventId = 0;
            if (res.next()) {
                dumpDirectory = sourceNN + res.getString(1);
                currReplEventId = Long.parseLong(res.getString(2));
            }
            LOG.info("Source Current Repl Event id : {} , Target Last Repl Event id : {}", currReplEventId,
                    lastReplEventId);
            /**
             *  Returning dump directory as null if currReplEventId and lastReplEventId
             *  are same as there will not be any extra events to replicate.
             *  This will disable the {@link HiveImport#perform(JobContext)}
             */
            if (currReplEventId == lastReplEventId) {
                return null;
            }
        } catch (SQLException e) {
            throw new BeaconException(e, "SQL Exception occurred");
        } catch (BeaconException e) {
            LOG.error("Exception occurred for export statement", e);
            throw new BeaconException(e.getMessage());
        } finally {
            timer.shutdown();
            captureHiveReplicationMetrics(jobContext, HiveActionType.EXPORT, sourceStatement);
            close(res);
            close(sourceStatement);
            close(sourceHiveClient);
        }
        return dumpDirectory;
    }

    @Override
    public void cleanUp(JobContext jobContext) throws BeaconException {
    }

    @Override
    public void recover(JobContext jobContext) throws BeaconException {
        LOG.info("No recovery for hive export job. Instance id [{}]", jobContext.getJobInstanceId());
    }

    @Override
    public void interrupt() throws BeaconException {
        shutdownTimer();
        try {
            if (sourceStatement != null && !sourceStatement.isClosed()) {
                LOG.debug("Interrupting Hive Export job!");
                sourceStatement.cancel();
            }
        } catch (SQLException e) {
            throw new BeaconException("Unable to interrupt Hive Export job!", e);
        }
    }

    private void shutdownTimer() {
        if (!timer.isShutdown()) {
            timer.shutdown();
        }
    }

    private long getReplEventId() throws BeaconException {
        Statement statement = null;
        HiveServerClient hiveServerClient = null;
        try {
            hiveServerClient = HiveClientFactory.getHiveServerClient(targetConnectionString);
            statement = hiveServerClient.createStatement();
            ReplCommand replCommand = new ReplCommand();
            return replCommand.getReplicatedEventId(statement, properties);
        } finally {
            close(statement);
            close(hiveServerClient);
        }
    }
}
