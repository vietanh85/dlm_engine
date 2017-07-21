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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.hortonworks.beacon.log.BeaconLog;

/**
 * This class contains File Reader utility.
 */
public final class FileReaderUtil {
    private static BeaconLog logger = BeaconLog.getLog(FileReaderUtil.class);
    private static boolean isDebugEnabled = logger.isDebugEnabled();

    private FileReaderUtil(){
    }
    public static List<String> readFile(InputStream policyStoreStream) throws IOException {
        if (isDebugEnabled) {
            logger.debug("==> FileReaderUtil readFile()");
        }
        List<String> list = new ArrayList<>();
        List<String> fileLines = IOUtils.readLines(policyStoreStream, StandardCharsets.UTF_8);
        if (fileLines != null) {
            for (String line : fileLines) {
                if ((!line.startsWith("#")) && Pattern.matches(".+;;.*;;.*;;.+", line)) {
                    list.add(line);
                }
            }
        }

        if (isDebugEnabled) {
            logger.debug("<== FileReaderUtil readFile()");
            logger.debug("Policies read :: " + list);
        }

        return list;
    }
}