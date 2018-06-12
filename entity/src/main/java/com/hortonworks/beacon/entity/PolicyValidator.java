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

package com.hortonworks.beacon.entity;

import com.hortonworks.beacon.client.entity.EntityType;
import com.hortonworks.beacon.client.entity.ReplicationPolicy;
import com.hortonworks.beacon.entity.exceptions.ValidationException;
import com.hortonworks.beacon.entity.util.ClusterHelper;
import com.hortonworks.beacon.entity.util.ClusterDao;
import com.hortonworks.beacon.entity.util.PolicyHelper;
import com.hortonworks.beacon.exceptions.BeaconException;
import com.hortonworks.beacon.util.FSUtils;
import org.apache.hadoop.fs.Path;

import java.util.Date;

import static com.hortonworks.beacon.constants.BeaconConstants.ONE_MIN;

/**
 * Validation helper function to validate Beacon ReplicationPolicy definition.
 */
public class PolicyValidator extends EntityValidator<ReplicationPolicy> {

    public PolicyValidator() {
        super(EntityType.REPLICATIONPOLICY);
    }
    private ClusterDao clusterDao = new ClusterDao();

    @Override
    public void validate(ReplicationPolicy entity) throws BeaconException {
        validateScheduleDate(entity.getStartTime(), entity.getEndTime());
        if (PolicyHelper.isPolicyHCFS(entity.getSourceDataset(), entity.getTargetDataset())) {
            // Check which cluster is Non HCFS and validate it exists and no pairing required
            if (!FSUtils.isHCFS(new Path(entity.getSourceDataset()))) {
                clusterExists(entity.getSourceCluster());
            } else if (!FSUtils.isHCFS(new Path(entity.getTargetDataset()))) {
                clusterExists(entity.getTargetCluster());
            }
        } else {
            clusterExists(entity.getSourceCluster());
            clusterExists(entity.getTargetCluster());
            ClusterHelper.validateIfClustersPaired(entity.getSourceCluster(), entity.getTargetCluster());
        }
    }

    private void clusterExists(String name) throws BeaconException {
        clusterDao.getActiveCluster(name);
    }

    private void validateScheduleDate(Date startTime, Date endTime) throws ValidationException {
        if (startTime.before(new Date(System.currentTimeMillis() - ONE_MIN))) {
            throw new ValidationException("Start time cannot be earlier than current time.");
        }
        if (endTime != null && endTime.before(startTime)) {
            throw new ValidationException("End time cannot be earlier than start time.");
        }
        if (endTime != null && endTime.before(new Date())) {
            throw new ValidationException("End time cannot be earlier than current time.");
        }
    }
}
