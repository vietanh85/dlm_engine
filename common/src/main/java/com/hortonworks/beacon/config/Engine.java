/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hortonworks.beacon.config;


import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Properties;

public class Engine {
    Logger LOG = LoggerFactory.getLogger(Engine.class);

    private static final String BUILD_PROPS = "beacon-buildinfo.properties";
    private static final String DEF_VERSION = "1.0-SNAPSHOT";

    private String hostName;
    private Short tlsPort;
    private Short port;
    private String principal;
    private Boolean tlsEnabled;
    private String quartzPrefix;
    private String configStoreUri;
    private String appPath;
    private String localClusterName;


    private int loadNumThreads;
    private int loadTimeout;
    private int resultsPerPage;

    private int socketBufferSize;

    public Engine() {
        setHostName("0.0.0.0");
        setPort((short) 25000);
        setTlsPort((short) 25443);
        setPrincipal("");
        setTlsEnabled(false);
        setConfigStoreUri("file:///tmp/config-store/");
        Class cl = BeaconConfig.class;
        URL resource = cl.getResource("/" + BUILD_PROPS);
        InputStream resourceAsStream = null;
        Properties buildInfo = new Properties();

        if (resource != null) {
            resourceAsStream = cl.getResourceAsStream("/" + BUILD_PROPS);
        } else {
            resource = cl.getResource(BUILD_PROPS);
            if (resource != null) {
                resourceAsStream = cl.getResourceAsStream(BUILD_PROPS);
            }
        }
        if (resourceAsStream != null) {
            try {
                buildInfo.load(resourceAsStream);
            } catch (Exception e) {
                LOG.warn("Unable to build property file " + BUILD_PROPS, e);
            }
        }
        String version = (String) buildInfo.get("build.version");
        if (version == null) {
            version = "1.0-SNAPSHOT";
        }
        setAppPath("webapp/target/beacon-webapp-" + version);
        setLoadNumThreads(10);
        setLoadTimeout(10);
        setResultsPerPage(10);
        setSocketBufferSize(32768);
        SecureRandom random = new SecureRandom();
        long rnd = random.nextLong();
        rnd = rnd == Long.MIN_VALUE ? 0 : Math.abs(rnd);
        setLocalClusterName("cluster" + Long.toString(rnd) + "");
    }

    public void copy(Engine o) {
        setHostName(o.getHostName());
        setPort(o.getPort());
        setTlsPort(o.getTlsPort());
        setPrincipal(o.getPrincipal());
        setTlsEnabled(o.getTlsEnabled());
        setConfigStoreUri(o.getConfigStoreUri());
        setAppPath(o.getAppPath());
        setLoadNumThreads(o.getLoadNumThreads());
        setLoadTimeout(o.getLoadTimeout());
        setResultsPerPage(o.getResultsPerPage());
        setSocketBufferSize(o.getSocketBufferSize());
        setLocalClusterName(o.getLocalClusterName());
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Short getTlsPort() {
        return tlsPort;
    }

    public void setTlsPort(Short tlsPort) {
        this.tlsPort = tlsPort;
    }

    public Short getPort() {
        return port;
    }

    public void setPort(Short port) {
        this.port = port;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Boolean getTlsEnabled() {
        return tlsEnabled;
    }

    public void setTlsEnabled(Boolean tlsEnabled) {
        this.tlsEnabled = tlsEnabled;
    }

    public String getQuartzPrefix() {
        return quartzPrefix;
    }

    public void setQuartzPrefix(String quartzPrefix) {
        this.quartzPrefix = quartzPrefix;
    }

    public String getConfigStoreUri() {
        return configStoreUri;
    }

    public void setConfigStoreUri(String configStoreUri) {
        if (!StringUtils.endsWith(configStoreUri, Path.SEPARATOR)) {
            configStoreUri += Path.SEPARATOR;
        }
        this.configStoreUri = configStoreUri;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public int getLoadNumThreads() {
        return loadNumThreads;
    }

    public void setLoadNumThreads(int loadNumThreads) {
        this.loadNumThreads = loadNumThreads;
    }

    public int getLoadTimeout() {
        return loadTimeout;
    }

    public void setLoadTimeout(int loadTimeout) {
        this.loadTimeout = loadTimeout;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public int getSocketBufferSize() {
        return socketBufferSize;
    }

    public void setSocketBufferSize(int socketBufferSize) {
        this.socketBufferSize = socketBufferSize;
    }


    public String getLocalClusterName() {
        return localClusterName;
    }

    public void setLocalClusterName(String localClusterName) {
        this.localClusterName = localClusterName;
    }
}