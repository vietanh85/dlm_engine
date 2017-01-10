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

package com.hortonworks.beacon.replication;


import com.hortonworks.beacon.client.entity.ReplicationPolicy;
import com.hortonworks.beacon.replication.fs.FSDRProperties;
import com.hortonworks.beacon.replication.hive.HiveDRProperties;
import com.hortonworks.beacon.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

public class ReplicationJobDetails implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(ReplicationJobDetails.class);

    private String name;
    private String type;
    private int frequency;
    private Date startTime;
    private Date endTime;
    Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public ReplicationJobDetails() {
    }

    public ReplicationJobDetails(Properties properties) {
        this.properties = properties;
    }

    public ReplicationJobDetails(String name, String type, int frequency, Properties properties) {
        this.name = name;
        this.type = type;
        this.frequency = frequency;
        this.properties = properties;
    }

    public ReplicationJobDetails(String name, String type, int frequency, Date startTime, Date endTime, Properties properties) {
        this.name = name;
        this.type = type;
        this.frequency = frequency;
        this.startTime = startTime;
        this.endTime = endTime;
        this.properties = properties;
    }

    public ReplicationJobDetails setReplicationJobDetails(Properties properties) {
        return new ReplicationJobDetails(properties.getProperty(ReplicationPolicy.ReplicationPolicyFields.NAME.getName()),
                properties.getProperty(ReplicationPolicy.ReplicationPolicyFields.TYPE.getName()),
                Integer.parseInt(properties.getProperty(ReplicationPolicy.ReplicationPolicyFields.FREQUENCYINSEC.getName())),
                DateUtil.parseDate(properties.getProperty("startTime")),
                DateUtil.parseDate(properties.getProperty("endTime")),
                properties);
    }

    public void validateReplicationProperties(Properties properties) {
        if (properties.getProperty("type").equalsIgnoreCase("fs")) {
            for (FSDRProperties option : FSDRProperties.values()) {
                if (properties.getProperty(option.getName()) == null && option.isRequired()) {
                    throw new IllegalArgumentException("Missing DR property for FS Replication : " + option.getName());
                }
            }
        } else if (properties.getProperty("type").equalsIgnoreCase("hive")) {
            for (HiveDRProperties option : HiveDRProperties.values()) {
                if (properties.getProperty(option.getName()) == null && option.isRequired()) {
                    throw new IllegalArgumentException("Missing DR property for Hive Replication : " + option.getName());
                }
            }
        }
    }

   // public abstract void validateReplicationProperties(Properties properties);


    @Override
    public String toString() {
        return "ReplicationJobDetails{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", frequency=" + frequency +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", properties=" + properties +
                '}';
    }
}
