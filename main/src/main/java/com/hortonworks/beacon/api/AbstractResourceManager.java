/**
 *   Copyright  (c) 2016-2017, Hortonworks Inc.  All rights reserved.
 *
 *   Except as expressly permitted in a written agreement between you or your
 *   company and Hortonworks, Inc. or an authorized affiliate or partner
 *   thereof, any use, reproduction, modification, redistribution, sharing,
 *   lending or other exploitation of all or any part of the contents of this
 *   software is strictly prohibited.
 */

package com.hortonworks.beacon.api;

import com.hortonworks.beacon.client.entity.Entity;
import com.hortonworks.beacon.client.resource.PolicyInstanceList;
import com.hortonworks.beacon.config.BeaconConfig;
import com.hortonworks.beacon.constants.BeaconConstants;
import com.hortonworks.beacon.entity.EntityValidator;
import com.hortonworks.beacon.entity.EntityValidatorFactory;
import com.hortonworks.beacon.entity.util.PolicyPersistenceHelper;
import com.hortonworks.beacon.exceptions.BeaconException;
import org.apache.commons.lang3.StringUtils;
import java.util.List;

/**
 * A base class for managing Beacon resource operations.
 */
abstract class AbstractResourceManager {
    private BeaconConfig config = BeaconConfig.getInstance();

    PolicyInstanceList listInstance(String filters, String orderBy, String sortBy, Integer offset,
                                            Integer resultsPerPage, boolean isArchived) throws BeaconException {
        resultsPerPage = resultsPerPage <= getMaxResultsPerPage() ? resultsPerPage : getMaxResultsPerPage();
        offset = checkAndSetOffset(offset);
        try {
            return PolicyPersistenceHelper.getFilteredJobInstance(filters, orderBy, sortBy,
                    offset, resultsPerPage, isArchived);
        } catch (Exception e) {
            throw new BeaconException(e.getMessage(), e);
        }
    }

    Integer getDefaultResultsPerPage() {
        return config.getEngine().getResultsPerPage();
    }

    Integer getMaxInstanceCount() {
        return config.getEngine().getMaxInstanceCount();
    }

    Integer getMaxResultsPerPage() {
        return config.getEngine().getMaxResultsPerPage();
    }

    void validate(Entity entity) throws BeaconException {
        EntityValidator validator = EntityValidatorFactory.getValidator(entity.getEntityType());
        validator.validate(entity);
    }

    Integer checkAndSetOffset(Integer offset) {
        return (offset > 0) ? offset : 0;
    }

    private String concatKeyValue(List<String> keys, List<String> values, String separator, String delimiter) {
        // Throw exception if keys length and values length isn't same.
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < keys.size(); i++) {
            if (StringUtils.isNotBlank(values.get(i))) {
                builder.append(keys.get(i));
                builder.append(separator);
                builder.append(values.get(i));
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    String concatKeyValue(List<String> keys, List<String> values) {
        return concatKeyValue(keys, values, BeaconConstants.EQUAL_SEPARATOR, BeaconConstants.SEMICOLON_SEPARATOR);
    }
}
