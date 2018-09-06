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

import com.hortonworks.beacon.exceptions.BeaconException;
import org.apache.atlas.AtlasServiceException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * Implement retry logic for service calls.
 */
public class RetryingClient {
    private static final Logger LOG = LoggerFactory.getLogger(RetryingClient.class);
    private static final String ERROR_MESSAGE_NO_ENTITIES = "no entities to create/update";
    private static final String ERROR_MESSAGE_IN_PROGRESS = "import or export is in progress";

    private static final int MAX_RETY_COUNT = 5;
    private static final long PAUSE_DURATION_INCREMENT_IN_MINUTES = 5 * 60 * 1000; // 5 min

    protected <T> T invokeWithRetry(Callable<T> func, T defaultReturnValue) throws BeaconException {
        for (int currentRetryCount = 0; currentRetryCount < MAX_RETY_COUNT; currentRetryCount++) {
            try {
                debugLog("retrying method: {}", func.getClass().getName(), null);
                return func.call();
            } catch (Exception e) {
                if (processImportExportLockException(e, currentRetryCount)) {
                    continue;
                }

                if (processInvalidParameterException(e)) {
                    return null;
                }

                LOG.error("{}", func.getClass().getName(), e);
                throw new BeaconException(e);
            }
        }

        return defaultReturnValue;
    }

    private boolean processInvalidParameterException(Exception e) {
        if (!(e instanceof AtlasServiceException)) {
            return false;
        }

        return (StringUtils.contains(e.getMessage(), ERROR_MESSAGE_NO_ENTITIES));
    }

    private boolean processImportExportLockException(Exception e, int currentRetryCount) throws BeaconException {
        if (!(e instanceof AtlasServiceException)) {
            return false;
        }

        if (StringUtils.contains(e.getMessage(), ERROR_MESSAGE_IN_PROGRESS)) {
            try {
                Thread.sleep(PAUSE_DURATION_INCREMENT_IN_MINUTES * currentRetryCount);
            } catch (InterruptedException intEx) {
                LOG.error("pause wait interrupted!", intEx);
                throw new BeaconException(intEx);
            }

            return true;
        }

        return false;
    }

    private void debugLog(String s, String name, Object[] parameters) {
        if (!LOG.isDebugEnabled()) {
            return;
        }

        LOG.debug(s, name, parameters);
    }
}