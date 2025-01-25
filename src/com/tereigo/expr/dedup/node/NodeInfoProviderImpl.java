package com.tereigo.expr.dedup.node;

public class NodeInfoProviderImpl implements NodeInfoProvider {

    private int nodeId = -1;
    private String sessionName = "";
    private String nodeName = "";

    public void setNodeId(final int nodeId) {
        this.nodeId = nodeId;
    }

    public void setSessionName(final String sessionName) {
        this.sessionName = sessionName;
    }

    public void setNodeName(final String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public int getNodeId() {
        return nodeId;
    }

    @Override
    public String getSessionName() {
        return sessionName;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }
}
