package io.bisq.network.p2p.network;

import io.bisq.common.network.NetworkEnvelope;

public interface MessageListener {
    void onMessage(NetworkEnvelope wireEnvelope, Connection connection);
}
