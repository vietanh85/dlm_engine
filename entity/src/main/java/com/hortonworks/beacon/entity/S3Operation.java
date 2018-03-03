/**
 *   Copyright  (c) 2016-2017, Hortonworks Inc.  All rights reserved.
 *
 *   Except as expressly permitted in a written agreement between you or your
 *   company and Hortonworks, Inc. or an authorized affiliate or partner
 *   thereof, any use, reproduction, modification, redistribution, sharing,
 *   lending or other exploitation of all or any part of the contents of this
 *   software is strictly prohibited.
 */
package com.hortonworks.beacon.entity;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.hortonworks.beacon.entity.exceptions.ValidationException;
import com.hortonworks.beacon.exceptions.BeaconException;
import org.apache.hadoop.fs.s3a.S3AUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * S3 related operation class.
 */
public final class S3Operation {
    private static final Logger LOG = LoggerFactory.getLogger(BeaconCloudCred.class);

    private AmazonS3Client amazonS3Client;

    private static final String REGEX = "Cannot create enum from (.*) value!";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    public S3Operation() {
        amazonS3Client = new AmazonS3Client();
    }

    public S3Operation(String accessKey, String secretKey) {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        amazonS3Client = new AmazonS3Client(awsCredentials);
    }

    public String getBucketEndPoint(String bucketName) throws BeaconException, URISyntaxException {
        String regionName;
        try {
            regionName = amazonS3Client.getBucketLocation(bucketName);
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            Matcher matcher = PATTERN.matcher(message);
            if (matcher.find()) {
                regionName = matcher.group(1);
            } else {
                throw new ValidationException(message);
            }
        } catch (AmazonClientException e) {
            throw new BeaconException(S3AUtils.translateException("Get Bucket location", bucketName, e));
        }
        LOG.debug("Bucket {} location: {}", bucketName, regionName);
        return getBucketRegionEndPoint(regionName);
    }

    private String getBucketRegionEndPoint(String regionName) {
        StringBuilder regionEndPoint = new StringBuilder();
        // s3.<region-name>.amazonaws.com
        regionEndPoint.append("s3.").append(regionName).append(".amazonaws.com");
        String cnRegionName = "cn";
        if (regionName.startsWith(cnRegionName)) {
            regionEndPoint.append(cnRegionName);
        }
        return regionEndPoint.toString();
    }
}
