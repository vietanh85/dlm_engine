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

package com.hortonworks.beacon.store;


import com.hortonworks.beacon.config.BeaconConfig;
import com.hortonworks.beacon.config.Store;
import com.hortonworks.beacon.exceptions.BeaconException;
import com.hortonworks.beacon.service.BeaconService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.MessageFormat;
import java.util.Properties;

public final class BeaconStore implements BeaconService {

    private static final Logger LOG = LoggerFactory.getLogger(BeaconStore.class);
    private static EntityManagerFactory factory = null;

    private BeaconStore() {
        init();
    }

    private static class Holder {
        private static final BeaconStore _instance = new BeaconStore();
    }

    public static BeaconStore getInstance() {
        return Holder._instance;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    public void init() {
        LOG.info("initializing BeaconStore.");
        BeaconConfig config =  BeaconConfig.getInstance();
        Store store = config.getStore();

        String user = store.getUser();
        String password = store.getPassword();
        String driver = store.getDriver();
        String url = store.getUrl();
        int maxConn = store.getMaxConnections();

        String dataSource = "org.apache.commons.dbcp.BasicDataSource";

        String connProps = "DriverClassName={0},Url={1},Username={2},Password={3},MaxActive={4}";
        connProps = MessageFormat.format(connProps, driver, url, user, password, maxConn);
        connProps += ",TestOnBorrow=false,TestOnReturn=false,TestWhileIdle=false";

        Properties props = new Properties();
        props.setProperty("openjpa.ConnectionProperties", connProps);
        props.setProperty("openjpa.ConnectionDriverName", dataSource);

        String dbType = url.substring("jdbc:".length());
        dbType = dbType.substring(0, dbType.indexOf(":"));
        String unitName = "beacon-" + dbType;
        factory = Persistence.createEntityManagerFactory(unitName, props);
        LOG.info("BeaconStore is initialized successfully for type: {}", dbType);
    }

    @Override
    public void destroy() throws BeaconException {
        if (factory != null && factory.isOpen()) {
            factory.close();
            LOG.info("BeaconStore is destroyed.");
        }
    }

    public EntityManager getEntityManager() {
        return factory.createEntityManager();
    }
}