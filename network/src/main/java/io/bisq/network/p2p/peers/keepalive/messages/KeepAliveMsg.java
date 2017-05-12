package io.bisq.network.p2p.peers.keepalive.messages;

import io.bisq.common.app.Version;
import io.bisq.common.network.NetworkEnvelope;

public abstract class KeepAliveMsg implements NetworkEnvelope {
    //TODO add serialVersionUID also in superclasses as changes would break compatibility
    @Override
    public int getMsgVersion() {
        return Version.getP2PMessageVersion();
    }

    @Override
    public String toString() {
        return "KeepAliveMessage{" +
                "messageVersion=" + getMsgVersion() +
                '}';
    }
}
