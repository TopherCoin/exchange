package io.bitsquare.p2p.peers;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import io.bitsquare.app.Log;
import io.bitsquare.common.UserThread;
import io.bitsquare.p2p.Message;
import io.bitsquare.p2p.network.Connection;
import io.bitsquare.p2p.network.MessageListener;
import io.bitsquare.p2p.network.NetworkNode;
import io.bitsquare.p2p.peers.messages.maintenance.MaintenanceMessage;
import io.bitsquare.p2p.peers.messages.maintenance.PingMessage;
import io.bitsquare.p2p.peers.messages.maintenance.PongMessage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class MaintenanceManager implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(MaintenanceManager.class);

    private Timer sendPingTimer;
    private PeerGroup peerGroup;
    private NetworkNode networkNode;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    public MaintenanceManager(PeerGroup peerGroup, NetworkNode networkNode) {
        this.peerGroup = peerGroup;
        this.networkNode = networkNode;

        networkNode.addMessageListener(this);
        startMaintenanceTimer();
    }


    public void shutDown() {
        Log.traceCall();
        if (sendPingTimer != null)
            sendPingTimer.cancel();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    // MessageListener implementation
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onMessage(Message message, Connection connection) {
        if (message instanceof MaintenanceMessage) {
            Log.traceCall(message.toString());
            log.debug("Received message " + message + " at " + peerGroup.getMyAddress() + " from " + connection.getPeerAddress());
            if (message instanceof PingMessage) {
                SettableFuture<Connection> future = networkNode.sendMessage(connection, new PongMessage(((PingMessage) message).nonce));
                Futures.addCallback(future, new FutureCallback<Connection>() {
                    @Override
                    public void onSuccess(Connection connection) {
                        log.trace("PongMessage sent successfully");
                    }

                    @Override
                    public void onFailure(@NotNull Throwable throwable) {
                        log.info("PongMessage sending failed " + throwable.getMessage());
                        peerGroup.removePeer(connection.getPeerAddress());
                    }
                });
            } else if (message instanceof PongMessage) {
                if (connection.getPeerAddress() != null) {
                    Peer peer = peerGroup.getAuthenticatedPeers().get(connection.getPeerAddress());
                    if (peer != null) {
                        if (((PongMessage) message).nonce != peer.getPingNonce()) {
                            log.warn("PongMessage invalid: self/peer " + peerGroup.getMyAddress() + "/" + connection.getPeerAddress());
                            peerGroup.removePeer(peer.address);
                        }
                    }
                }
            }
        }
    }

    private void startMaintenanceTimer() {
        Log.traceCall();
        if (sendPingTimer != null)
            sendPingTimer.cancel();

        sendPingTimer = UserThread.runAfterRandomDelay(() -> {
            pingPeers();
            startMaintenanceTimer();
        }, 5, 10, TimeUnit.MINUTES);
    }


    private void pingPeers() {
        Set<Peer> connectedPeersList = new HashSet<>(peerGroup.getAuthenticatedPeers().values());
        if (!connectedPeersList.isEmpty()) {
            Log.traceCall();
            connectedPeersList.stream()
                    .filter(e -> (new Date().getTime() - e.connection.getLastActivityDate().getTime()) > PeerGroup.INACTIVITY_PERIOD_BEFORE_PING)
                    .forEach(e -> UserThread.runAfterRandomDelay(() -> {
                        SettableFuture<Connection> future = networkNode.sendMessage(e.connection, new PingMessage(e.getPingNonce()));
                        Futures.addCallback(future, new FutureCallback<Connection>() {
                            @Override
                            public void onSuccess(Connection connection) {
                                log.trace("PingMessage sent successfully");
                            }

                            @Override
                            public void onFailure(@NotNull Throwable throwable) {
                                log.info("PingMessage sending failed " + throwable.getMessage());
                                peerGroup.removePeer(e.address);
                            }
                        });
                    }, 1, 10));
        }
    }
}
