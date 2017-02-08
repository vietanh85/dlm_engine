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

package com.hortonworks.beacon.entity;


import com.hortonworks.beacon.client.entity.Cluster;
import com.hortonworks.beacon.client.entity.EntityType;
import com.hortonworks.beacon.exceptions.BeaconException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validation helper function to validate Beacon Cluster definition.
 */
public class ClusterValidator extends EntityValidator<Cluster> {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterValidator.class);

    public ClusterValidator() {
        super(EntityType.CLUSTER);
    }

    @Override
    public void validate(Cluster entity) throws BeaconException {
    // Do any validation here
    }
}
