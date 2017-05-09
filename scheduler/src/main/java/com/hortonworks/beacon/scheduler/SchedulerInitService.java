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

package com.hortonworks.beacon.scheduler;

import com.hortonworks.beacon.config.BeaconConfig;
import com.hortonworks.beacon.config.DbStore;
import com.hortonworks.beacon.config.Scheduler;
import com.hortonworks.beacon.exceptions.BeaconException;
import com.hortonworks.beacon.log.BeaconLog;
import com.hortonworks.beacon.scheduler.quartz.BeaconQuartzScheduler;
import com.hortonworks.beacon.service.BeaconService;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * Beacon scheduler initialization service.
 * Scheduler will be in the stand by mode and needs to be started with {@link SchedulerStartService}.
 * The service depends on the DB to be setup with Quartz tables.
 */
public final class SchedulerInitService implements BeaconService {

    private static final BeaconLog LOG = BeaconLog.getLog(SchedulerInitService.class);
    public static final String SERVICE_NAME = SchedulerInitService.class.getName();
    private static final SchedulerInitService INSTANCE = new SchedulerInitService();

    private static final String THREAD_POOL_CLASS_VALUE = "org.quartz.simpl.SimpleThreadPool";
    private static final String JOB_FACTORY_CLASS_VALUE = "org.quartz.simpl.SimpleJobFactory";
    private static final String DRIVER_DELEGATION_CLASS_VALUE = "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
    private static final String JOB_STORE_CLASS_VALUE = "org.quartz.impl.jdbcjobstore.JobStoreTX";
    private static final String DATA_SOURCE = "beaconDataSource";
    private static final String INSTANCE_ID = "beaconScheduler";

    enum QuartzProperties {
        THREAD_POOL_CLASS("org.quartz.threadPool.class"),
        THREAD_POOL_COUNT("org.quartz.threadPool.threadCount"),
        INSTANCE_ID("org.quartz.scheduler.instanceId"),
        JOB_FACTORY_CLASS("org.quartz.scheduler.jobFactory.class"),
        DRIVER_DELEGATE_CLASS("org.quartz.jobStore.driverDelegateClass"),
        TABLE_PREFIX("org.quartz.jobStore.tablePrefix"),
        DATA_SOURCE("org.quartz.jobStore.dataSource"),
        JOB_STORE_CLASS("org.quartz.jobStore.class"),
        DRIVER("org.quartz.dataSource.beaconDataSource.driver"),
        URL("org.quartz.dataSource.beaconDataSource.URL"),
        USER("org.quartz.dataSource.beaconDataSource.user"),
        PASSWORD("org.quartz.dataSource.beaconDataSource.password"),
        MAX_CONNECTION("org.quartz.dataSource.beaconDataSource.maxConnections");

        private String property;

        QuartzProperties(String property) {
            this.property = property;
        }

        public String getProperty() {
            return property;
        }
    }

    private BeaconQuartzScheduler scheduler;

    private SchedulerInitService() {
        scheduler = BeaconQuartzScheduler.get();
    }

    public BeaconQuartzScheduler getScheduler() {
        return scheduler;
    }

    public static SchedulerInitService get() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return SERVICE_NAME;
    }

    @Override
    public void init() throws BeaconException {
        DbStore dbStore = BeaconConfig.getInstance().getDbStore();
        Scheduler schedulerConfig = BeaconConfig.getInstance().getScheduler();
        if (schedulerConfig == null) {
            throw new IllegalStateException("Beacon scheduler configuration is not provided.");
        }
        Properties properties = new Properties();
        properties.setProperty(QuartzProperties.THREAD_POOL_CLASS.getProperty(), THREAD_POOL_CLASS_VALUE);
        properties.setProperty(QuartzProperties.JOB_FACTORY_CLASS.getProperty(), JOB_FACTORY_CLASS_VALUE);
        properties.setProperty(QuartzProperties.THREAD_POOL_COUNT.getProperty(), schedulerConfig.getQuartzThreadPool());
        if (StringUtils.isNotBlank(schedulerConfig.getQuartzPrefix())) {
            properties.setProperty(QuartzProperties.TABLE_PREFIX.getProperty(), schedulerConfig.getQuartzPrefix());
            properties.setProperty(QuartzProperties.DRIVER_DELEGATE_CLASS.getProperty(), DRIVER_DELEGATION_CLASS_VALUE);
            properties.setProperty(QuartzProperties.JOB_STORE_CLASS.getProperty(), JOB_STORE_CLASS_VALUE);
            properties.setProperty(QuartzProperties.INSTANCE_ID.getProperty(), INSTANCE_ID);
            properties.setProperty(QuartzProperties.DATA_SOURCE.getProperty(), DATA_SOURCE);
            LOG.info("Beacon quartz scheduler database driver: [{}={}]", QuartzProperties.DRIVER.getProperty(),
                    dbStore.getDriver());
            properties.setProperty(QuartzProperties.DRIVER.getProperty(), dbStore.getDriver());
            // remove the "create=true" from derby database
            if (dbStore.getUrl().contains("jdbc:derby")) {
                properties.setProperty(QuartzProperties.URL.getProperty(), dbStore.getUrl().split(";")[0]);
            } else {
                properties.setProperty(QuartzProperties.URL.getProperty(), dbStore.getUrl());
            }
            LOG.info("Beacon quartz scheduler database url: [{}={}]", QuartzProperties.URL.getProperty(),
                    properties.getProperty(QuartzProperties.URL.getProperty()));
            LOG.info("Beacon quartz scheduler database user: [{}={}]", QuartzProperties.USER.getProperty(),
                    dbStore.getUser());
            properties.setProperty(QuartzProperties.USER.getProperty(), dbStore.getUser());
            properties.setProperty(QuartzProperties.PASSWORD.getProperty(), dbStore.resolvePassword());
            properties.setProperty(QuartzProperties.MAX_CONNECTION.getProperty(),
                    String.valueOf(dbStore.getMaxConnections()));
        }

        if (scheduler != null && !scheduler.isStarted()) {
            scheduler.initializeScheduler(properties);
        }
    }

    @Override
    public void destroy() throws BeaconException {
        if (scheduler != null && scheduler.isStarted()) {
            scheduler.stopScheduler();
        }
    }
}