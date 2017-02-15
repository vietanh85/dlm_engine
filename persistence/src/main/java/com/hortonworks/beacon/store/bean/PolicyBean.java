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

package com.hortonworks.beacon.store.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * Bean of policy.
 */
@Entity
@Table(name = "policy")
@NamedQueries({
        @NamedQuery(name = "GET_ACTIVE_POLICY", query = "select OBJECT(b) from PolicyBean b where b.name = :name "
                + "AND b.deletionTime IS NULL"),
        @NamedQuery(name = "GET_POLICY", query = "select OBJECT(b) from PolicyBean b where b.name = :name "
                + "order by b.version DESC"),
        @NamedQuery(name = "GET_SUBMITTED_POLICY", query = "select OBJECT(b) from PolicyBean b "
                + "where b.name = :name AND b.deletionTime IS NULL AND b.status = :status"),
        @NamedQuery(name = "DELETE_POLICY", query = "update PolicyBean b set b.deletionTime = :deletionTime, "
                + "b.lastModifiedTime = :lastModifiedTime" + ", b.status = :status "
                + "where b.name = :name AND b.deletionTime IS NULL"),
        @NamedQuery(name = "UPDATE_STATUS", query = "update PolicyBean b set b.status = :status, "
                + "b.lastModifiedTime = :lastModifiedTime "
                + "where b.name = :name AND b.type = :policyType AND b.deletionTime IS NULL")
    })
public class PolicyBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long policyId;

    @Column(name = "name")
    private String name;

    @Column(name = "version")
    private int version;

    @Column(name = "change_id")
    private int changeId;

    @Column(name = "status")
    private String status;

    @Column(name = "type")
    private String type;

    @Column(name = "source_cluster")
    private String sourceCluster;

    @Column(name = "target_cluster")
    private String targetCluster;

    @Column(name = "source_dataset")
    private String sourceDataset;

    @Column(name = "target_dataset")
    private String targetDataset;

    @Column(name = "created_time")
    private java.sql.Timestamp creationTime;

    @Column(name = "modified_time")
    private java.sql.Timestamp lastModifiedTime;

    @Column(name = "start_time")
    private java.sql.Timestamp startTime;

    @Column(name = "end_time")
    private java.sql.Timestamp endTime;

    @Column(name = "frequency")
    private long frequencyInSec;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "notification_to")
    private String notificationTo;

    @Column(name = "retry_count")
    private int retryCount;

    @Column(name = "retry_delay")
    private long retryDelay;

    @Column(name = "tags")
    private String tags;

    @Column(name = "execution_type")
    private String executionType;

    @Column(name = "deletion_time")
    private java.sql.Timestamp deletionTime;

    private List<PolicyPropertiesBean> customProperties;

    public long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(long policyId) {
        this.policyId = policyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getChangeId() {
        return changeId;
    }

    public void setChangeId(int changeId) {
        this.changeId = changeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Date getCreationTime() {
        return new Date(this.creationTime.getTime());
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = new java.sql.Timestamp(creationTime.getTime());
    }

    public Date getLastModifiedTime() {
        return new Date(this.lastModifiedTime.getTime());
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = new java.sql.Timestamp(lastModifiedTime.getTime());
    }

    public Date getStartTime() {
        if (this.startTime != null) {
            return new Date(this.startTime.getTime());
        } else {
            return null;
        }
    }

    public void setStartTime(Date startTime) {
        if (startTime != null) {
            this.startTime = new java.sql.Timestamp(startTime.getTime());
        }
    }

    public Date getEndTime() {
        if (this.endTime != null) {
            return new Date(this.endTime.getTime());
        } else {
            return null;
        }
    }

    public void setEndTime(Date endTime) {
        if (endTime != null) {
            this.endTime = new java.sql.Timestamp(endTime.getTime());
        }
    }

    public long getFrequencyInSec() {
        return frequencyInSec;
    }

    public void setFrequencyInSec(long frequencyInSec) {
        this.frequencyInSec = frequencyInSec;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public long getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(long retryDelay) {
        this.retryDelay = retryDelay;
    }

    public List<PolicyPropertiesBean> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(List<PolicyPropertiesBean> customProperties) {
        this.customProperties = customProperties;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationTo() {
        return notificationTo;
    }

    public void setNotificationTo(String notificationTo) {
        this.notificationTo = notificationTo;
    }

    public String getExecutionType() {
        return executionType;
    }

    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public Date getDeletionTime() {
        if (deletionTime != null) {
            return new Date(deletionTime.getTime());
        } else {
            return null;
        }
    }

    public void setDeletionTime(Date deletionTime) {
        if (deletionTime != null) {
            this.deletionTime = new java.sql.Timestamp(deletionTime.getTime());
        }
    }

    public PolicyBean() {
    }

    public PolicyBean(String name) {
        this.name = name;
    }
}