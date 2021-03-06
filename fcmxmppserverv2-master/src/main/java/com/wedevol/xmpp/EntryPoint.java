package com.wedevol.xmpp;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wedevol.xmpp.bean.CcsOutMessage;
import com.wedevol.xmpp.server.CcsClient;
import com.wedevol.xmpp.util.MessageMapper;
import com.wedevol.xmpp.util.Util;

/**
 * Entry Point class for the XMPP Server
 *
 * @author Charz++
 */
public class EntryPoint extends CcsClient {

    private static final Logger logger = LoggerFactory.getLogger(EntryPoint.class);

    public EntryPoint(String projectId, String apiKey, boolean debuggable, String toRegId) {
        super(projectId, apiKey, debuggable);

        try {
            connect();
        } catch (XMPPException | InterruptedException | KeyManagementException | NoSuchAlgorithmException
                | SmackException | IOException e) {
            logger.error("Error trying to connect. Error: {}", e.getClass());
            logger.error("Error trying to connect. Error: {}", e.getMessage());
            e.printStackTrace();
        }

        // Send a sample downstream message to a device
        final String messageId = Util.getUniqueMessageId();
        Map<String, String> dataPayload = new HashMap<>();
        dataPayload.put(Util.PAYLOAD_ATTRIBUTE_MESSAGE, "This is the simple sample message");
        CcsOutMessage message = new CcsOutMessage(toRegId, messageId, dataPayload);
        final String jsonRequest = MessageMapper.toJsonString(message);
        //sendDownstreamMessage(messageId, jsonRequest);

        try {
            CountDownLatch latch = new CountDownLatch(1);
            latch.await();
        } catch (InterruptedException e) {
            logger.error("An error occurred while latch was waiting. Error: {}", e.getMessage());
        }
    }

    public static void main(String[] args) throws SmackException, IOException {
        final String fcmProjectSenderId = "149763630937";
        final String fcmServerKey = "serverkey";
        final String toRegId = "q";
        new EntryPoint(fcmProjectSenderId, fcmServerKey, false, toRegId);
    }
}
