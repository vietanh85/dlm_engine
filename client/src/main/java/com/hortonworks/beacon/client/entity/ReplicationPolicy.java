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

package com.hortonworks.beacon.client.entity;

import com.hortonworks.beacon.api.PropertiesIgnoreCase;
import com.hortonworks.beacon.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * The ReplicationPolicy contains the definition of policy to be replicated like sourceDataset, targetDataset and
 * others.
 */
public class ReplicationPolicy extends Entity {
    private String policyId;
    private String name;
    private String type;
    private String description;
    private String status;
    private String lastInstanceStatus;
    private String executionType;
    private String sourceDataset;
    private String targetDataset;
    private String sourceCluster;
    private String targetCluster;
    private Date creationTime;
    private Date startTime;
    private Date endTime;
    private int frequencyInSec;
    private List<String> tags;
    private List<String> plugins;
    private Properties customProperties = new Properties();
    private Retry retry;
    private String user;
    private Notification notification;

    /**
     * ReplicationPolicy fields used in policy properties.
     */
    public enum ReplicationPolicyFields {
        ID("id"),
        NAME("name"),
        TYPE("type"),
        EXECUTIONTYPE("executionType"),
        DESCRIPTION("description"),
        SOURCEDATASET("sourceDataset"),
        TARGETDATASET("targetDataset"),
        SOURCECLUSTER("sourceCluster"),
        TARGETCLUSTER("targetCluster"),
        CLOUDCRED("cloudCred"),
        STARTTIME("startTime"),
        ENDTIME("endTime"),
        FREQUENCYINSEC("frequencyInSec"),
        TAGS("tags"),
        PLUGINS("plugins"),
        RETRYATTEMPTS("retryAttempts"),
        RETRYDELAY("retryDelay"),
        USER("user"),
        NOTIFICATIONTYPE("notificationType"),
        NOTIFICATIONTO("notificationReceivers"),
        SOURCE_SETSNAPSHOTTABLE("source.setSnapshottable"),
        ENABLE_SNAPSHOTBASEDREPLICATION("enableSnapshotBasedReplication"),
        CLOUD_ENCRYPTIONALGORITHM("cloud.encryptionAlgorithm"),
        CLOUD_ENCRYPTIONKEY("cloud.encryptionKey");

        private final String name;

        ReplicationPolicyFields(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

    }

    public ReplicationPolicy() {
    }

    public ReplicationPolicy(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.description = builder.description;
        this.sourceDataset = builder.sourceDataset;
        this.targetDataset = builder.targetDataset;
        this.sourceCluster = builder.sourceCluster;
        this.targetCluster = builder.targetCluster;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.tags = builder.tags;
        this.plugins = builder.plugins;
        this.frequencyInSec = builder.frequencyInSec;
        this.customProperties = builder.customProperties;
        this.retry = builder.retry;
        this.user = builder.user;
        this.notification = builder.notification;
    }

    /**
     * Builder class for Beacon ReplicationPolicy resource.
     */
    public static class Builder {
        private String name;
        private String type;
        private String description;
        private String sourceDataset;
        private String targetDataset;
        private String sourceCluster;
        private String targetCluster;
        private Date startTime;
        private Date endTime;
        private int frequencyInSec;
        private List<String> tags;
        private List<String> plugins;
        private Properties customProperties;
        private Retry retry;
        private String user;
        private Notification notification;

        public Builder(String nameValue, String typeValue, String sourceDatasetValue,
                       String targetDatasetValue, String sourceClusterValue,
                       String targetClusterValue, int frequencyInSecValue) {
            this.name = nameValue;
            this.type = typeValue;
            this.sourceDataset = sourceDatasetValue;
            this.targetDataset = targetDatasetValue;
            this.sourceCluster = sourceClusterValue;
            this.targetCluster = targetClusterValue;
            this.frequencyInSec = frequencyInSecValue;
        }

        public Builder description(String descriptionValue) {
            if (StringUtils.isNotBlank(descriptionValue)) {
                this.description = descriptionValue;
            }
            return this;
        }

        public Builder startTime(Date startTimeValue) {
            if (startTimeValue != null) {
                this.startTime = new Date(startTimeValue.getTime());
            }
            return this;
        }

        public Builder endTime(Date endTimeValue) {
            if (endTimeValue != null) {
                this.endTime = new Date(endTimeValue.getTime());
            }
            return this;
        }

        public Builder tags(List<String> tagsValue) {
            this.tags = tagsValue;
            return this;
        }

        public Builder customProperties(Properties customPropertiesValue) {
            this.customProperties = customPropertiesValue;
            return this;
        }

        public Builder retry(Retry retryValue) {
            this.retry = retryValue;
            return this;
        }

        public Builder user(String username) {
            this.user = username;
            return this;
        }

        public Builder notification(Notification notificationValue) {
            this.notification = notificationValue;
            return this;
        }

        public Builder plugins(List<String> pluginList) {
            this.plugins = pluginList;
            return this;
        }

        public ReplicationPolicy build() {
            return new ReplicationPolicy(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceDataset() {
        return sourceDataset;
    }

    public void setSourceDataset(String sourceDataset) {
        this.sourceDataset = sourceDataset;
    }

    public String getTargetDataset() {
        return targetDataset;
    }

    public void setTargetDataset(String targetDataset) {
        this.targetDataset = targetDataset;
    }

    public Date getStartTime() {
        return startTime != null ? new Date(startTime.getTime()) : null;

    }

    public void setStartTime(Date startTime) {
        if (startTime == null) {
            this.startTime = null;
        } else {
            this.startTime = new Date(startTime.getTime());
        }
    }

    public Date getEndTime() {
        return endTime != null ? new Date(endTime.getTime()) : null;
    }

    public void setEndTime(Date endTime) {
        if (endTime != null) {
            this.endTime = new Date(endTime.getTime());
        }
    }

    public int getFrequencyInSec() {
        return frequencyInSec;
    }

    public void setFrequencyInSec(int frequencyInSec) {
        this.frequencyInSec = frequencyInSec;
    }

    @Override
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Properties getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Properties customProperties) {
        this.customProperties = customProperties;
    }

    public String getSourceCluster() {
        return sourceCluster;
    }

    public void setSourceCluster(String sourceCluster) {
        this.sourceCluster = sourceCluster;
    }

    public String getTargetCluster() {
        return targetCluster;
    }

    public void setTargetCluster(String targetCluster) {
        this.targetCluster = targetCluster;
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public Date getCreationTime() {
        return creationTime != null ? new Date(creationTime.getTime()) : null;
    }

    public void setCreationTime(Date creationTime) {
        if (creationTime != null) {
            this.creationTime = new Date(creationTime.getTime());
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastInstanceStatus() {
        return lastInstanceStatus;
    }

    public void setLastInstanceStatus(String lastInstanceStatus) {
        this.lastInstanceStatus = lastInstanceStatus;
    }

    public String getExecutionType() {
        return executionType;
    }

    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSourceSetSnapshottable() {
        return customProperties.getProperty(ReplicationPolicyFields.SOURCE_SETSNAPSHOTTABLE.getName());
    }

    public String getEnableSnapshotBasedReplication() {
        return customProperties.getProperty(ReplicationPolicyFields.ENABLE_SNAPSHOTBASEDREPLICATION.getName());
    }

    public String getCloudEncryptionAlgorithm() {
        return customProperties.getProperty(ReplicationPolicyFields.CLOUD_ENCRYPTIONALGORITHM.getName());
    }

    public String getCloudEncryptionKey() {
        return customProperties.getProperty(ReplicationPolicyFields.CLOUD_ENCRYPTIONKEY.getName());
    }

    public String getCloudCred() {
        return customProperties.getProperty(ReplicationPolicy.ReplicationPolicyFields.CLOUDCRED.getName());
    }

    public List<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }

    public PropertiesIgnoreCase asProperties() {
        PropertiesIgnoreCase properties = new PropertiesIgnoreCase();
        properties.putIfNotNull(ReplicationPolicyFields.ID.getName(), policyId);
        properties.putIfNotNull(ReplicationPolicyFields.NAME.getName(), name);
        properties.putIfNotNull(ReplicationPolicyFields.DESCRIPTION.getName(), description);
        properties.putIfNotNull(ReplicationPolicyFields.TYPE.getName(), type);
        properties.putIfNotNull(ReplicationPolicyFields.EXECUTIONTYPE.getName(), executionType);
        properties.putIfNotNull(ReplicationPolicyFields.SOURCEDATASET.getName(), sourceDataset);
        properties.putIfNotNull(ReplicationPolicyFields.TARGETDATASET.getName(), targetDataset);
        properties.putIfNotNull(ReplicationPolicyFields.SOURCECLUSTER.getName(), sourceCluster);
        properties.putIfNotNull(ReplicationPolicyFields.TARGETCLUSTER.getName(), targetCluster);
        properties.putIfNotNull(ReplicationPolicyFields.STARTTIME.getName(), DateUtil.formatDate(startTime));
        properties.putIfNotNull(ReplicationPolicyFields.ENDTIME.getName(), DateUtil.formatDate(endTime));
        properties.put(ReplicationPolicyFields.FREQUENCYINSEC.getName(), frequencyInSec);
        properties.putIfNotNull(ReplicationPolicyFields.TAGS.getName(), tags);
        properties.putIfNotNull(ReplicationPolicyFields.PLUGINS.getName(), plugins);
        if (retry != null) {
            properties.putIfNotNull(ReplicationPolicyFields.RETRYATTEMPTS.getName(), retry.getAttempts());
            properties.putIfNotNull(ReplicationPolicyFields.RETRYDELAY.getName(), retry.getDelay());
        }
        properties.putIfNotNull(ReplicationPolicyFields.USER.getName(), user);
        if (notification != null) {
            properties.putIfNotNull(ReplicationPolicyFields.NOTIFICATIONTO.getName(), notification.getTo());
            properties.putIfNotNull(ReplicationPolicyFields.NOTIFICATIONTYPE.getName(), notification.getType());
        }
        for (String propertyKey : customProperties.stringPropertyNames()) {
            properties.put(propertyKey, customProperties.getProperty(propertyKey));
        }
        return properties;
    }
}


