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

package com.hortonworks.beacon.authorize.simple;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.hortonworks.beacon.authorize.BeaconAccessRequest;
import com.hortonworks.beacon.authorize.BeaconActionTypes;
import com.hortonworks.beacon.authorize.BeaconAuthorizationException;
import com.hortonworks.beacon.authorize.BeaconAuthorizer;
import com.hortonworks.beacon.authorize.BeaconAuthorizerFactory;
import com.hortonworks.beacon.authorize.BeaconResourceTypes;
import com.hortonworks.beacon.log.BeaconLog;

/**
 * This class contains Authorization utility.
 */

public final class BeaconAuthorizationUtils {
    private static final BeaconLog LOG = BeaconLog.getLog(BeaconAuthorizationUtils.class);
    private static boolean isDebugEnabled = LOG.isDebugEnabled();
    public static final String BASE_API = "api/beacon/";
    private static final String BASE_URL = "/" + BASE_API;

    private BeaconAuthorizationUtils(){
    }
    public static String getApi(String contextPath) {
        if (isDebugEnabled) {
            LOG.debug("==> getApi({})", contextPath);
        }
        if (contextPath.startsWith(BASE_URL)) {
            contextPath = contextPath.substring(BASE_URL.length());
        } else {
            // strip of leading '/'
            if (contextPath.startsWith("/")) {
                contextPath = contextPath.substring(1);
            }
        }
        String[] split = contextPath.split("/", 3);

        String api = split[0];
        if (Pattern.matches("v\\d", api)) {
            api = split[1];
        }

        if (isDebugEnabled) {
            LOG.debug("<== getApi({}): {}", contextPath, api);
        }

        return api;
    }

    public static BeaconActionTypes getBeaconAction(String method) {
        BeaconActionTypes action = null;

        switch (method.toUpperCase()) {
            case "POST":
                action = BeaconActionTypes.CREATE;
                break;
            case "GET":
                action = BeaconActionTypes.READ;
                break;
            case "PUT":
                action = BeaconActionTypes.UPDATE;
                break;
            case "DELETE":
                action = BeaconActionTypes.DELETE;
                break;
            default:
                if (isDebugEnabled) {
                    LOG.debug("getBeaconAction(): Invalid HTTP method '{}", method);
                }
                break;
        }

        if (isDebugEnabled) {
            LOG.debug("<== BeaconAuthorizationFilter getBeaconAction HTTP Method {} mapped to BeaconAction : {}",
                    method, action);
        }
        return action;
    }

    public static Set<BeaconResourceTypes> getBeaconResourceType(String contextPath) {
        Set<BeaconResourceTypes> resourceTypes = new HashSet<>();
        if (isDebugEnabled) {
            LOG.debug("==> getBeaconResourceType  for {}", contextPath);
        }
        String api = getApi(contextPath);
        if ((api.startsWith("cluster"))) {
            resourceTypes.add(BeaconResourceTypes.CLUSTER);
        } else if (api.startsWith("policy/schedule")) {
            resourceTypes.add(BeaconResourceTypes.SCHEDULE);
        } else if (api.startsWith("policy")) {
            resourceTypes.add(BeaconResourceTypes.POLICY);
        } else if (api.startsWith("events")) {
            resourceTypes.add(BeaconResourceTypes.EVENT);
        } else if (api.startsWith("logs")) {
            resourceTypes.add(BeaconResourceTypes.LOGS);
        }else {
            LOG.error("Unable to find Beacon Resource corresponding to : {}\nSetting {}",
                    api, BeaconResourceTypes.UNKNOWN.name());
            resourceTypes.add(BeaconResourceTypes.UNKNOWN);
        }

        if (isDebugEnabled) {
            LOG.debug("<== Returning BeaconResources {} for api {}", resourceTypes, api);
        }
        return resourceTypes;
    }

    public static boolean isAccessAllowed(BeaconResourceTypes resourcetype, BeaconActionTypes actionType,
            String userName, Set<String> groups, HttpServletRequest request) {
        BeaconAuthorizer authorizer = null;
        boolean isaccessAllowed = false;

        Set<BeaconResourceTypes> resourceTypes = new HashSet<>();
        resourceTypes.add(resourcetype);
        BeaconAccessRequest beaconRequest = new BeaconAccessRequest(resourceTypes, "*", actionType, userName, groups,
                BeaconAuthorizationUtils.getRequestIpAddress(request));
        try {
            authorizer = BeaconAuthorizerFactory.getBeaconAuthorizer();
            if (authorizer != null) {
                isaccessAllowed = authorizer.isAccessAllowed(beaconRequest);
            }
        } catch (BeaconAuthorizationException e) {
            LOG.error("Unable to obtain BeaconAuthorizer. ", e);
        }

        return isaccessAllowed;
    }

    public static String getRequestIpAddress(HttpServletRequest httpServletRequest) {
        try {
            InetAddress inetAddr = InetAddress.getByName(httpServletRequest.getRemoteAddr());
            return inetAddr.getHostAddress();
        } catch (UnknownHostException ex) {
            LOG.error("Error occured when retrieving IP address", ex);
            return "";
        }
    }
}