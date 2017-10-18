/**
 *   Copyright  (c) 2016-2017, Hortonworks Inc.  All rights reserved.
 *
 *   Except as expressly permitted in a written agreement between you or your
 *   company and Hortonworks, Inc. or an authorized affiliate or partner
 *   thereof, any use, reproduction, modification, redistribution, sharing,
 *   lending or other exploitation of all or any part of the contents of this
 *   software is strictly prohibited.
 */

package com.hortonworks.beacon.store.executors;

import com.hortonworks.beacon.log.BeaconLog;
import com.hortonworks.beacon.store.bean.ClusterBean;
import com.hortonworks.beacon.store.bean.ClusterPropertiesBean;
import com.hortonworks.beacon.store.executors.ClusterPropertiesExecutor.ClusterPropertiesQuery;
import com.hortonworks.beacon.util.PropertiesIgnoreCase;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Beacon store executor for Cluster entity update.
 */
public class ClusterUpdateExecutor {

    private static final BeaconLog LOG = BeaconLog.getLog(ClusterUpdateExecutor.class);

    private static final String UPDATE_CLUSTER = "update ClusterBean b set ";

    public ClusterUpdateExecutor() {
    }

    public void persistUpdatedCluster(ClusterBean updatedCluster, PropertiesIgnoreCase updatedProps,
                                      PropertiesIgnoreCase newProps, EntityManager entityManager) {
        Query clusterUpdateQuery = createClusterUpdateQuery(updatedCluster, entityManager);
        List<Query> clusterPropUpdateQuery = createClusterPropUpdateQuery(updatedCluster, updatedProps, entityManager);
        List<ClusterPropertiesBean> clusterPropertiesBeans = insertNewClusterProps(updatedCluster, newProps);
        clusterUpdateQuery.executeUpdate();
        for (Query query : clusterPropUpdateQuery) {
            query.executeUpdate();
        }
        for (ClusterPropertiesBean bean : clusterPropertiesBeans) {
            entityManager.persist(bean);
        }
    }

    private List<ClusterPropertiesBean> insertNewClusterProps(ClusterBean updatedCluster,
                                                              PropertiesIgnoreCase newProps) {
        List<ClusterPropertiesBean> propBeans = new ArrayList<>();
        Date createdTime = new Date();
        for (String property : newProps.stringPropertyNames()) {
            ClusterPropertiesBean propBean = new ClusterPropertiesBean();
            propBean.setClusterName(updatedCluster.getName());
            propBean.setClusterVersion(updatedCluster.getVersion());
            propBean.setName(property);
            propBean.setValue(newProps.getPropertyIgnoreCase(property));
            propBean.setCreatedTime(createdTime);
            propBeans.add(propBean);
            LOG.debug("Cluster new custom properties key: [{0}], value: [{1}]",
                    property, newProps.getPropertyIgnoreCase(property));
        }
        return propBeans;
    }

    private List<Query> createClusterPropUpdateQuery(ClusterBean updatedCluster, PropertiesIgnoreCase updatedProps,
                                                     EntityManager entityManager) {
        List<Query> clusterPropUpdateQueries = new ArrayList<>();
        for (String property : updatedProps.stringPropertyNames()) {
            Query query = entityManager.createNamedQuery(ClusterPropertiesQuery.UPDATE_CLUSTER_PROP.name());
            LOG.debug("Cluster custom properties update query: [{0}]", query.toString());
            query.setParameter("valueParam", updatedProps.getPropertyIgnoreCase(property));
            query.setParameter("clusterNameParam", updatedCluster.getName());
            query.setParameter("clusterVersionParam", updatedCluster.getVersion());
            query.setParameter("nameParam", property);
            clusterPropUpdateQueries.add(query);
        }
        return clusterPropUpdateQueries;
    }

    private Query createClusterUpdateQuery(ClusterBean updatedCluster, EntityManager entityManager) {
        StringBuilder queryBuilder = new StringBuilder();
        int index = 1;
        List<String> paramNames = new ArrayList<>();
        List<Object> paramValues = new ArrayList<>();
        String key;

        queryBuilder.append("b.changeId = b.changeId+1");

        if (StringUtils.isNotBlank(updatedCluster.getDescription())) {
            key = "description" + index++;
            queryBuilder.append(", b.description = :").append(key);
            paramNames.add(key);
            paramValues.add(updatedCluster.getDescription());
        }

        if (StringUtils.isNotBlank(updatedCluster.getFsEndpoint())) {
            key = "fsEndpoint" + index++;
            queryBuilder.append(", b.fsEndpoint = :").append(key);
            paramNames.add(key);
            paramValues.add(updatedCluster.getFsEndpoint());
        }

        if (StringUtils.isNotBlank(updatedCluster.getHsEndpoint())) {
            key = "hsEndpoint" + index++;
            queryBuilder.append(", b.hsEndpoint = :").append(key);
            paramNames.add(key);
            paramValues.add(updatedCluster.getHsEndpoint());
        }

        if (StringUtils.isNotBlank(updatedCluster.getRangerEndpoint())) {
            key = "rangerEndpoint" + index++;
            queryBuilder.append(", b.rangerEndpoint = :").append(key);
            paramNames.add(key);
            paramValues.add(updatedCluster.getRangerEndpoint());
        }

        // TODO : update for Atlas and Beacon end point are pending.

        if (StringUtils.isNotBlank(updatedCluster.getTags())) {
            key = "tags" + index++;
            queryBuilder.append(", b.tags = :").append(key);
            paramNames.add(key);
            paramValues.add(updatedCluster.getTags());
        }

        // where clause
        key = "name" + index++;
        queryBuilder.append(" where b.name = :").append(key);
        paramNames.add(key);
        paramValues.add(updatedCluster.getName());

        key = "version" + index++;
        queryBuilder.append(" AND b.version = :").append(key);
        paramNames.add(key);
        paramValues.add(updatedCluster.getVersion());

        queryBuilder.append(" AND b.retirementTime IS NULL");

        String query = UPDATE_CLUSTER + queryBuilder.toString();
        LOG.debug("Cluster update query: [{0}]", query);
        Query updateQuery = entityManager.createQuery(query);
        for (int i = 0; i < index-1; i++) {
            updateQuery.setParameter(paramNames.get(i), paramValues.get(i));
        }
        return updateQuery;
    }
}