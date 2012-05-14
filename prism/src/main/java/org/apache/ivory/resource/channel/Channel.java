package org.apache.ivory.resource.channel;

import org.apache.ivory.IvoryException;

public interface Channel {

    <T> T invoke(String methodName, Object... args) throws IvoryException;
}
