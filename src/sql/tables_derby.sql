-- HORTONWORKS DATAPLANE SERVICE AND ITS CONSTITUENT SERVICES
--
-- (c) 2016-2018 Hortonworks, Inc. All rights reserved.
--
-- This code is provided to you pursuant to your written agreement with Hortonworks, which may be the terms of the
-- Affero General Public License version 3 (AGPLv3), or pursuant to a written agreement with a third party authorized
-- to distribute this code.  If you do not have a written agreement with Hortonworks or with an authorized and
-- properly licensed third party, you do not have any rights to this code.
--
-- If this code is provided to you under the terms of the AGPLv3:
-- (A) HORTONWORKS PROVIDES THIS CODE TO YOU WITHOUT WARRANTIES OF ANY KIND;
-- (B) HORTONWORKS DISCLAIMS ANY AND ALL EXPRESS AND IMPLIED WARRANTIES WITH RESPECT TO THIS CODE, INCLUDING BUT NOT
--    LIMITED TO IMPLIED WARRANTIES OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE;
-- (C) HORTONWORKS IS NOT LIABLE TO YOU, AND WILL NOT DEFEND, INDEMNIFY, OR HOLD YOU HARMLESS FOR ANY CLAIMS ARISING
--    FROM OR RELATED TO THE CODE; AND
-- (D) WITH RESPECT TO YOUR EXERCISE OF ANY RIGHTS GRANTED TO YOU FOR THE CODE, HORTONWORKS IS NOT LIABLE FOR ANY
--    DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES INCLUDING, BUT NOT LIMITED TO,
--    DAMAGES RELATED TO LOST REVENUE, LOST PROFITS, LOSS OF INCOME, LOSS OF BUSINESS ADVANTAGE OR UNAVAILABILITY,
--    OR LOSS OR CORRUPTION OF DATA.
--


-- Auto drop and reset tables
-- Derby doesn't support if exists condition on table drop, so user must manually do this step if needed to.
-- noinspection SqlDialectInspection

-- DROP TABLE QUARTZ_BLOB_TRIGGERS;
-- DROP TABLE QUARTZ_CALENDARS;
-- DROP TABLE QUARTZ_CRON_TRIGGERS;
-- DROP TABLE QUARTZ_FIRED_TRIGGERS;
-- DROP TABLE QUARTZ_LOCKS;
-- DROP TABLE QUARTZ_SCHEDULER_STATE;
-- DROP TABLE QUARTZ_SIMPROP_TRIGGERS;
-- DROP TABLE QUARTZ_SIMPLE_TRIGGERS;
-- DROP TABLE QUARTZ_TRIGGERS;
-- DROP TABLE QUARTZ_PAUSED_TRIGGER_GRPS;
-- DROP TABLE QUARTZ_JOB_DETAILS;
-- DROP TABLE BEACON_POLICY;
-- DROP TABLE BEACON_POLICY_PROP;
-- DROP TABLE BEACON_POLICY_INSTANCE;
-- DROP TABLE BEACON_INSTANCE_JOB;

CREATE TABLE QUARTZ_JOB_DETAILS (
  SCHED_NAME        VARCHAR(120) NOT NULL,
  JOB_NAME          VARCHAR(255) NOT NULL,
  JOB_GROUP         VARCHAR(255) NOT NULL,
  DESCRIPTION       VARCHAR(250),
  JOB_CLASS_NAME    VARCHAR(250) NOT NULL,
  IS_DURABLE        VARCHAR(5)   NOT NULL,
  IS_NONCONCURRENT  VARCHAR(5)   NOT NULL,
  IS_UPDATE_DATA    VARCHAR(5)   NOT NULL,
  REQUESTS_RECOVERY VARCHAR(5)   NOT NULL,
  JOB_DATA          BLOB,
  PRIMARY KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

CREATE TABLE QUARTZ_TRIGGERS (
  SCHED_NAME     VARCHAR(120) NOT NULL,
  TRIGGER_NAME   VARCHAR(255) NOT NULL,
  TRIGGER_GROUP  VARCHAR(255) NOT NULL,
  JOB_NAME       VARCHAR(255) NOT NULL,
  JOB_GROUP      VARCHAR(255) NOT NULL,
  DESCRIPTION    VARCHAR(250),
  NEXT_FIRE_TIME BIGINT,
  PREV_FIRE_TIME BIGINT,
  PRIORITY       INTEGER,
  TRIGGER_STATE  VARCHAR(16)  NOT NULL,
  TRIGGER_TYPE   VARCHAR(8)   NOT NULL,
  START_TIME     BIGINT       NOT NULL,
  END_TIME       BIGINT,
  CALENDAR_NAME  VARCHAR(200),
  MISFIRE_INSTR  SMALLINT,
  JOB_DATA       BLOB,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, JOB_NAME, JOB_GROUP) REFERENCES QUARTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

CREATE TABLE QUARTZ_SIMPLE_TRIGGERS (
  SCHED_NAME      VARCHAR(120) NOT NULL,
  TRIGGER_NAME    VARCHAR(255) NOT NULL,
  TRIGGER_GROUP   VARCHAR(255) NOT NULL,
  REPEAT_COUNT    BIGINT       NOT NULL,
  REPEAT_INTERVAL BIGINT       NOT NULL,
  TIMES_TRIGGERED BIGINT       NOT NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES QUARTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QUARTZ_CRON_TRIGGERS (
  SCHED_NAME      VARCHAR(120) NOT NULL,
  TRIGGER_NAME    VARCHAR(255) NOT NULL,
  TRIGGER_GROUP   VARCHAR(255) NOT NULL,
  CRON_EXPRESSION VARCHAR(120) NOT NULL,
  TIME_ZONE_ID    VARCHAR(80),
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES QUARTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QUARTZ_SIMPROP_TRIGGERS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  TRIGGER_NAME  VARCHAR(255) NOT NULL,
  TRIGGER_GROUP VARCHAR(255) NOT NULL,
  STR_PROP_1    VARCHAR(512),
  STR_PROP_2    VARCHAR(512),
  STR_PROP_3    VARCHAR(512),
  INT_PROP_1    INT,
  INT_PROP_2    INT,
  LONG_PROP_1   BIGINT,
  LONG_PROP_2   BIGINT,
  DEC_PROP_1    NUMERIC(13, 4),
  DEC_PROP_2    NUMERIC(13, 4),
  BOOL_PROP_1   VARCHAR(5),
  BOOL_PROP_2   VARCHAR(5),
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
  REFERENCES QUARTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QUARTZ_BLOB_TRIGGERS (
  SCHED_NAME    VARCHAR(120) NOT NULL,
  TRIGGER_NAME  VARCHAR(255) NOT NULL,
  TRIGGER_GROUP VARCHAR(255) NOT NULL,
  BLOB_DATA     BLOB,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES QUARTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QUARTZ_CALENDARS (
  SCHED_NAME    VARCHAR(120) NOT NULL,
  CALENDAR_NAME VARCHAR(200) NOT NULL,
  CALENDAR      BLOB         NOT NULL,
  PRIMARY KEY (SCHED_NAME, CALENDAR_NAME)
);

CREATE TABLE QUARTZ_PAUSED_TRIGGER_GRPS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_GROUP)
);

CREATE TABLE QUARTZ_FIRED_TRIGGERS (
  SCHED_NAME        VARCHAR(120) NOT NULL,
  ENTRY_ID          VARCHAR(95)  NOT NULL,
  TRIGGER_NAME      VARCHAR(255) NOT NULL,
  TRIGGER_GROUP     VARCHAR(255) NOT NULL,
  INSTANCE_NAME     VARCHAR(200) NOT NULL,
  FIRED_TIME        BIGINT       NOT NULL,
  SCHED_TIME        BIGINT       NOT NULL,
  PRIORITY          INTEGER      NOT NULL,
  STATE             VARCHAR(16)  NOT NULL,
  JOB_NAME          VARCHAR(255),
  JOB_GROUP         VARCHAR(255),
  IS_NONCONCURRENT  VARCHAR(5),
  REQUESTS_RECOVERY VARCHAR(5),
  PRIMARY KEY (SCHED_NAME, ENTRY_ID)
);

CREATE TABLE QUARTZ_SCHEDULER_STATE
(
  SCHED_NAME        VARCHAR(120) NOT NULL,
  INSTANCE_NAME     VARCHAR(200) NOT NULL,
  LAST_CHECKIN_TIME BIGINT       NOT NULL,
  CHECKIN_INTERVAL  BIGINT       NOT NULL,
  PRIMARY KEY (SCHED_NAME, INSTANCE_NAME)
);

CREATE TABLE QUARTZ_LOCKS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  LOCK_NAME  VARCHAR(40)  NOT NULL,
  PRIMARY KEY (SCHED_NAME, LOCK_NAME)
);

CREATE TABLE BEACON_POLICY
(
  ID                   VARCHAR(512),
  NAME                 VARCHAR(64),
  DESCRIPTION          VARCHAR(512),
  VERSION              INTEGER,
  CHANGE_ID            INTEGER,
  STATUS               VARCHAR(40),
  LAST_INSTANCE_STATUS VARCHAR(40),
  TYPE                 VARCHAR(40),
  SOURCE_CLUSTER       VARCHAR(255),
  TARGET_CLUSTER       VARCHAR(255),
  SOURCE_DATASET       VARCHAR(4000),
  TARGET_DATASET       VARCHAR(4000),
  CREATED_TIME         TIMESTAMP,
  LAST_MODIFIED_TIME   TIMESTAMP,
  START_TIME           TIMESTAMP,
  END_TIME             TIMESTAMP,
  FREQUENCY            INTEGER,
  NOTIFICATION_TYPE    VARCHAR(255),
  NOTIFICATION_TO      VARCHAR(255),
  RETRY_COUNT          INT,
  RETRY_DELAY          INT,
  TAGS                 VARCHAR(1024),
  EXECUTION_TYPE       VARCHAR(64),
  RETIREMENT_TIME      TIMESTAMP,
  JOBS                 VARCHAR(1024),
  USERNAME             VARCHAR(64),
  PRIMARY KEY (ID)
);

CREATE TABLE BEACON_POLICY_PROP
(
  ID           BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  POLICY_ID    VARCHAR(512),
  CREATED_TIME TIMESTAMP,
  NAME         VARCHAR(512),
  VALUE        VARCHAR(1024),
  TYPE         VARCHAR(20),
  PRIMARY KEY (ID),
  FOREIGN KEY (POLICY_ID) REFERENCES BEACON_POLICY (ID)
);

CREATE TABLE BEACON_POLICY_INSTANCE
(
  ID                 VARCHAR(512) NOT NULL,
  POLICY_ID          VARCHAR(512),
  START_TIME         TIMESTAMP,
  END_TIME           TIMESTAMP,
  RETIREMENT_TIME    TIMESTAMP,
  STATUS             VARCHAR(40),
  MESSAGE            VARCHAR(4000),
  RUN_COUNT          INTEGER,
  CURRENT_OFFSET     INTEGER,
  TRACKING_INFO      VARCHAR(4000),
  PRIMARY KEY (ID),
  FOREIGN KEY (POLICY_ID) REFERENCES BEACON_POLICY (ID)
);

CREATE TABLE BEACON_INSTANCE_JOB
(
  INSTANCE_ID     VARCHAR(512) NOT NULL,
  "offset"          INTEGER      NOT NULL,
  STATUS          VARCHAR(40),
  START_TIME      TIMESTAMP,
  END_TIME        TIMESTAMP,
  MESSAGE         VARCHAR(4000),
  RETIREMENT_TIME TIMESTAMP,
  RUN_COUNT       INTEGER,
  CONTEXT_DATA    VARCHAR(4000),
  PRIMARY KEY (INSTANCE_ID, "offset"),
  FOREIGN KEY (INSTANCE_ID) REFERENCES BEACON_POLICY_INSTANCE (ID)
);


CREATE TABLE BEACON_EVENT
(
  ID                  BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  POLICY_ID           VARCHAR(512),
  INSTANCE_ID         VARCHAR(512),
  EVENT_ENTITY_TYPE   VARCHAR(32),
  EVENT_ID            INTEGER NOT NULL,
  EVENT_SEVERITY      VARCHAR (16),
  EVENT_TIMESTAMP     TIMESTAMP,
  EVENT_MESSAGE       VARCHAR(4000),
  EVENT_INFO          VARCHAR(4000),
  PRIMARY KEY(ID)
);
CREATE TABLE BEACON_CLUSTER (
  NAME               VARCHAR(128) NOT NULL,
  VERSION            INTEGER,
  CHANGE_ID          INTEGER,
  DESCRIPTION        VARCHAR(512),
  BEACON_URI         VARCHAR(512),
  FS_ENDPOINT        VARCHAR(512),
  HS_ENDPOINT        VARCHAR(1024),
  ATLAS_ENDPOINT     VARCHAR(512),
  RANGER_ENDPOINT    VARCHAR(512),
  LOCAL              BOOLEAN,
  TAGS               VARCHAR(4000),
  CREATED_TIME       TIMESTAMP,
  LAST_MODIFIED_TIME TIMESTAMP,
  RETIREMENT_TIME    TIMESTAMP,
  PRIMARY KEY (NAME, VERSION)
);

CREATE TABLE BEACON_CLUSTER_PROP (
  ID              BIGINT GENERATED BY DEFAULT AS IDENTITY ( START WITH 1, INCREMENT BY 1 ),
  CLUSTER_NAME    VARCHAR(128),
  CLUSTER_VERSION INTEGER,
  CREATED_TIME    TIMESTAMP,
  NAME            VARCHAR(512),
  VALUE           VARCHAR(1024),
  TYPE            VARCHAR(20),
  PRIMARY KEY (ID),
  FOREIGN KEY (CLUSTER_NAME, CLUSTER_VERSION) REFERENCES BEACON_CLUSTER (NAME, VERSION)
);

CREATE TABLE BEACON_CLUSTER_PAIR (
  ID                     BIGINT GENERATED BY DEFAULT AS IDENTITY ( START WITH 1, INCREMENT BY 1 ),
  CLUSTER_NAME           VARCHAR(128),
  CLUSTER_VERSION        INTEGER,
  PAIRED_CLUSTER_NAME    VARCHAR(128),
  PAIRED_CLUSTER_VERSION INTEGER,
  STATUS                 VARCHAR(32),
  STATUS_MESSAGE         VARCHAR(512),
  LAST_MODIFIED_TIME     TIMESTAMP,
  PRIMARY KEY (ID),
  FOREIGN KEY (CLUSTER_NAME, CLUSTER_VERSION) REFERENCES BEACON_CLUSTER (NAME, VERSION),
  FOREIGN KEY (PAIRED_CLUSTER_NAME, PAIRED_CLUSTER_VERSION) REFERENCES BEACON_CLUSTER (NAME, VERSION)
);

CREATE TABLE BEACON_CLOUD_CRED (
  ID                  VARCHAR(128),
  NAME                VARCHAR(128),
  PROVIDER            VARCHAR(64),
  AUTH_TYPE           VARCHAR(32),
  CREATION_TIME       TIMESTAMP,
  LAST_MODIFIED_TIME  TIMESTAMP,
  CONFIGURATION       BLOB,
  PRIMARY KEY (ID),
  UNIQUE (NAME)
);

-- Indexes for quartz tables as per 2.3.0 release.
CREATE INDEX IDX_QUARTZ_J_REQ_RECOVERY ON QUARTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QUARTZ_J_GRP ON QUARTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);

CREATE INDEX IDX_QUARTZ_T_J ON QUARTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QUARTZ_T_JG ON QUARTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QUARTZ_T_C ON QUARTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX IDX_QUARTZ_T_G ON QUARTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QUARTZ_T_STATE ON QUARTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX IDX_QUARTZ_T_N_STATE ON QUARTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QUARTZ_T_N_G_STATE ON QUARTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QUARTZ_T_NEXT_FIRE_TIME ON QUARTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX IDX_QUARTZ_T_NFT_ST ON QUARTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX IDX_QUARTZ_T_NFT_MISFIRE ON QUARTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX IDX_QUARTZ_T_NFT_ST_MISFIRE ON QUARTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX IDX_QUARTZ_T_NFT_ST_MISFIRE_GRP ON QUARTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

CREATE INDEX IDX_QUARTZ_FT_TRIG_INST_NAME ON QUARTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX IDX_QUARTZ_FT_INST_JOB_REQ_RCVRY ON QUARTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QUARTZ_FT_J_G ON QUARTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QUARTZ_FT_JG ON QUARTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QUARTZ_FT_T_G ON QUARTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QUARTZ_FT_TG ON QUARTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);

-- Indexes for Beacon managed tables.
CREATE INDEX IDX_BEACON_EV_PID_ETY ON BEACON_EVENT(POLICY_ID, EVENT_ENTITY_TYPE);
CREATE INDEX IDX_BEACON_EV_IID_ETY ON BEACON_EVENT(INSTANCE_ID, EVENT_ENTITY_TYPE);
CREATE INDEX IDX_BEACON_EV_ETY ON BEACON_EVENT(EVENT_ENTITY_TYPE);

CREATE INDEX IDX_BEACON_INS_JOB_ST ON BEACON_INSTANCE_JOB(STATUS);

CREATE INDEX IDX_BEACON_PL_INS_PID_ST ON BEACON_POLICY_INSTANCE(POLICY_ID, STATUS);
CREATE INDEX IDX_BEACON_PL_INS_PID_SRT ON BEACON_POLICY_INSTANCE(POLICY_ID, START_TIME);
CREATE INDEX IDX_BEACON_PL_INS_PID_ET ON BEACON_POLICY_INSTANCE(POLICY_ID, END_TIME);
CREATE INDEX IDX_BEACON_PL_INS_ST ON BEACON_POLICY_INSTANCE(STATUS);

CREATE INDEX IDX_BEACON_PL_PROP_PID ON BEACON_POLICY_PROP(POLICY_ID);

CREATE INDEX IDX_BEACON_PL_PNAME ON BEACON_POLICY(NAME);
CREATE INDEX IDX_BEACON_PL_TY_NAME ON BEACON_POLICY(TYPE, NAME);
CREATE INDEX IDX_BEACON_PL_ST ON BEACON_POLICY(STATUS);
CREATE INDEX IDX_BEACON_PL_SC ON BEACON_POLICY(SOURCE_CLUSTER);
CREATE INDEX IDX_BEACON_PL_TC ON BEACON_POLICY(TARGET_CLUSTER);

CREATE INDEX IDX_BEACON_CC_PR ON BEACON_CLOUD_CRED(PROVIDER);
CREATE INDEX IDX_BEACON_CC_NA ON BEACON_CLOUD_CRED(NAME);

