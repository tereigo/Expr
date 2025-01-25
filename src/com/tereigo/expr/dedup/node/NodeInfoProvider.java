package com.tereigo.expr.dedup.node;

public interface NodeInfoProvider {
    int getNodeId();
    String getSessionName();
    String getNodeName();
}
