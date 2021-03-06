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
package com.hortonworks.beacon.plugin.atlas;

import com.google.common.annotations.VisibleForTesting;
import com.hortonworks.beacon.client.entity.Cluster;
import com.hortonworks.beacon.client.entity.ReplicationPolicy;
import com.hortonworks.beacon.config.BeaconConfig;
import com.hortonworks.beacon.entity.BeaconCluster;
import com.hortonworks.beacon.entity.util.ClusterHelper;
import com.hortonworks.beacon.exceptions.BeaconException;
import com.hortonworks.beacon.plugin.DataSet;
import com.hortonworks.beacon.plugin.Plugin;
import com.hortonworks.beacon.plugin.PluginInfo;
import com.hortonworks.beacon.plugin.PluginStats;
import com.hortonworks.beacon.plugin.service.PluginAction;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Beacon Plugin for Atlas.
 */
public class BeaconAtlasPlugin implements Plugin {
    private static final Logger LOG = LoggerFactory.getLogger(BeaconAtlasPlugin.class);

    private Status pluginStatus;

    private PluginInfo pluginInfo;
    private final AtlasPluginStats pluginStats;

    private AtlasProcess exportProcess;
    private AtlasProcess importProcess;

    public BeaconAtlasPlugin() {
        this(AtlasRESTClient.build());
    }

    @VisibleForTesting
    public BeaconAtlasPlugin(RESTClientBuilder clientBuilder) {
        this(new ExportProcess(clientBuilder), new ImportProcess(clientBuilder));
    }

    @VisibleForTesting
    BeaconAtlasPlugin(ExportProcess exportProcess, ImportProcess importProcess) {
        pluginStats = new AtlasPluginStats();
        this.exportProcess = exportProcess;
        this.importProcess = importProcess;

        this.exportProcess.setPluginStats(this.pluginStats);
        this.importProcess.setPluginStats(this.pluginStats);
        setStatusToInactive();
    }

    @Override
    public PluginInfo register() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("==> BeaconAtlasPlugin.register");
        }

        this.pluginInfo = getPluginDetails();
        setStatusToActive();
        return getPluginDetails();
    }

    @Override
    public Path exportData(DataSet dataset) throws BeaconException {
        if (isPluginInactive(dataset.getSourceCluster())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Plugin is inactive/atlas endpoint in cluster is not set. "
                        + "exportData would not be performed!");
            }

            return null;
        }

        return exportProcess.run(dataset, new Path(dataset.getStagingPath()), pluginStats);
    }


    @Override
    public void importData(DataSet dataset, Path exportedDataPath) throws BeaconException {
        if (isPluginInactive(dataset.getTargetCluster())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Plugin is inactive//atlas endpoint in cluster is not set. "
                        + "importData would not be performed!");
            }

            return;
        }

        importProcess.run(dataset, exportedDataPath, pluginStats);
    }

    @Override
    public PluginInfo getInfo() {
        return getPluginDetails();
    }

    @Override
    public PluginStats getStats() {
        return pluginStats;
    }

    @Override
    public Status getStatus() {
        return pluginStatus;
    }

    private boolean isPluginInactive(Cluster cluster) {
        return pluginStatus == Status.INACTIVE || cluster == null || StringUtils.isEmpty(cluster.getAtlasEndpoint());
    }

    @VisibleForTesting
    void setStatusToActive() {
        pluginStatus = Status.ACTIVE;
        LOG.info("BeaconAtlasPlugin: Status: {}", pluginStatus);
    }

    private void setStatusToInactive() {
        pluginStatus = Status.INACTIVE;
    }

    private PluginInfo getPluginDetails() {
        if (pluginInfo != null) {
            return pluginInfo;
        }

        pluginInfo = new AtlasPluginInfo();
        return pluginInfo;
    }

    public static String getPluginName() {
        return AtlasPluginInfo.PLUGIN_NAME;
    }

    public boolean isEnabled(String cluster) throws BeaconException {
        if (StringUtils.isEmpty(cluster)) {
            return false;
        }
        BeaconCluster beaconCluster = new BeaconCluster(ClusterHelper.getActiveCluster(cluster));
        return StringUtils.isNotEmpty(beaconCluster.getAtlasEndpoint());
    }

    @Override
    public List<PluginAction> getLineage(ReplicationPolicy policy) throws BeaconException {
        List<PluginAction> jobList = new ArrayList<>();

        if (isEnabled(policy.getSourceCluster())) {
            jobList.add(PluginAction.EXPORT);
        }

        if (isEnabled(policy.getTargetCluster())) {
            jobList.add(PluginAction.IMPORT);
        }

        //If export doesn't run, no point in running import
        if (!jobList.contains(PluginAction.EXPORT)) {
            return new ArrayList<>();
        }

        //If its only export and don't need to preserve meta, don't run export as well
        if (jobList.size() == 1 && jobList.contains(PluginAction.EXPORT) && !BeaconConfig.getInstance().getEngine()
                .isPreserveMeta()) {
            return new ArrayList<>();
        }
        return jobList;
    }
}
