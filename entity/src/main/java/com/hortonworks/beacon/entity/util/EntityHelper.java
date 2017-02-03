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

package com.hortonworks.beacon.entity.util;

import com.hortonworks.beacon.client.entity.Entity;
import com.hortonworks.beacon.client.entity.EntityType;
import com.hortonworks.beacon.entity.store.ConfigurationStore;
import com.hortonworks.beacon.exceptions.BeaconException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

public final class EntityHelper {
    private EntityHelper() {
    }

    public static Properties getCustomProperties(final Properties properties, final Set<String> entityElements) {
        Properties customProperties = new Properties();
        for (Map.Entry<Object, Object> property : properties.entrySet()) {
            if (!entityElements.contains(property.getKey().toString().toLowerCase())) {
                customProperties.put(property.getKey(), property.getValue());
            }

        }
        return customProperties;
    }

    public static List<String> getTags(Entity entity) {
        String rawTags = entity.getTags();

        List<String> tags = new ArrayList<String>();
        if (!StringUtils.isEmpty(rawTags)) {
            for (String tag : rawTags.split(",")) {
                tags.add(tag.trim());
            }
        }

        return tags;
    }

    public static <T extends Entity> T getEntity(String type, String entityName) throws BeaconException {
        EntityType entityType;
        try {
            entityType = EntityType.getEnum(type);
        } catch (IllegalArgumentException e) {
            throw new BeaconException("Invalid entity type: " + type, e);
        }
        return getEntity(entityType, entityName);
    }

    public static <T extends Entity> T getEntity(EntityType type, String entityName) throws BeaconException {
        if (StringUtils.isBlank(entityName)) {
            throw new BeaconException(entityName + " cannot be null or empty");
        }
        ConfigurationStore configStore = ConfigurationStore.getInstance();
        T entity = configStore.getEntity(type, entityName);
        if (entity == null) {
            throw new NoSuchElementException(entityName + " (" + type + ") not found");
        }
        return entity;
    }
}
