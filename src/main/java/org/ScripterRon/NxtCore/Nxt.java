/*
 * Copyright 2014 Ronald Hoffman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ScripterRon.NxtCore;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.math.BigInteger;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Make API requests to the local Nxt node and return the results
 */
public class Nxt {

    /** Response container factory */
    private static final ContainerFactory containerFactory = new ResponseFactory();

    /** Genesis block timestamp (November 24, 2013 12:00:00 UTC) */
    public static final long GENESIS_TIMESTAMP;
    static {
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.set(2013, 10, 24, 12, 0, 0);
        GENESIS_TIMESTAMP = cal.getTimeInMillis()/1000;
    }

    /** Genesis account identifier */
    public static final long GENESIS_ACCOUNT_ID = new BigInteger("1739068987193023818").longValue();

    /** NXT <-> NQT */
    public static final long NQT_ADJUST = 100000000L;

    /** API reason codes */
    public static final int INCORRECT_REQUEST = 1;
    public static final int MISSING_PARAMETER = 3;
    public static final int INCORRECT_PARAMETER = 4;
    public static final int UNKNOWN_OBJECT = 5;
    public static final int NOT_ENOUGH = 6;
    public static final int NOT_ALLOWED = 7;
    public static final int NOT_AVAILABLE = 9;

    /** Nxt node host name */
    private static String nodeName;

    /** Nxt node API port */
    private static int nodePort;

    /**
     * Initialize the Nxt core library
     *
     * @param       hostName                Host name or IP address of the node server
     * @param       apiPort                 Port for the node server
     */
    public static void init(String hostName, int apiPort) {
        nodeName = hostName;
        nodePort = apiPort;
    }

    /**
     * Broadcast a signed transaction
     *
     * @param       txBytes                 Signed transaction bytes
     * @return                              Transaction identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static long broadcastTransaction(byte[] txBytes) throws NxtException {
        long txId;
        try {
            PeerResponse response = issueRequest("broadcastTransaction", "transactionBytes="+Utils.toHexString(txBytes));
            txId = response.getId("transaction");
        } catch (IdentifierException exc) {
            throw new NxtException("Invalid transaction identifier returned for 'broadcastTransaction'", exc);
        }
        return txId;
    }

    /**
     * Get an account
     *
     * @param       accountId               Account identifier
     * @return                              Account
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static Account getAccount(long accountId) throws NxtException {
        Account account;
        try {
            PeerResponse response = issueRequest("getAccount", "account="+Utils.idToString(accountId));
            account = new Account(response);
        } catch (IdentifierException | NumberFormatException exc) {
            throw new NxtException("Invalid account data returned for 'getAccount'", exc);
        }
        return account;
    }

    /**
     * Get the public key for an account
     *
     * @param       accountId               Account identifier
     * @return                              Public key or null if the public key has not been set
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static byte[] getAccountPublicKey(long accountId) throws NxtException {
        byte[] publicKey;
        try {
            PeerResponse response = issueRequest("getAccountPublicKey", "account="+Utils.idToString(accountId));
            publicKey = response.getHexString("publicKey");
        } catch (NumberFormatException exc) {
            throw new NxtException("Invalid public key returned for 'getAccountPublicKey'", exc);
        }
        return publicKey;
    }

    /**
     * Get the account block identifiers (blocks forged by the account)
     *
     * @param       accountId               Account identifier
     * @return                              List of account blocks
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Long> getAccountBlocks(long accountId) throws NxtException {
        List<Long> blockList;
        try {
            PeerResponse response = issueRequest("getAccountBlockIds", "account="+Utils.idToString(accountId));
            blockList = response.getIdList("blockIds");
        } catch (IdentifierException exc) {
            throw new NxtException("Invalid block identifier returned for 'getAccountBlockIds'", exc);
        }
        return blockList;
    }

    /**
     * Get the confirmed account transaction identifiers
     *
     * @param       accountId               Account identifier
     * @return                              List of transaction identifiers
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Long> getConfirmedAccountTransactions(long accountId) throws NxtException {
        List<Long> txList;
        try {
            PeerResponse response = issueRequest("getAccountTransactionIds", "account="+Utils.idToString(accountId));
            txList = response.getIdList("transactionIds");
        } catch (IdentifierException exc) {
            throw new NxtException("Invalid transaction identifier returned for 'getAccountTransactionIds'", exc);
        }
        return txList;
    }

    /**
     * Get the unconfirmed account transaction identifiers
     *
     * @param       accountId               Account identifier
     * @return                              List of transaction identifiers
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Long> getUnconfirmedAccountTransactions(long accountId) throws NxtException {
        List<Long> txList;
        try {
            PeerResponse response = issueRequest("getUnconfirmedTransactionIds", "account="+Utils.idToString(accountId));
            txList = response.getIdList("unconfirmedTransactionIds");
        } catch (IdentifierException exc) {
            throw new NxtException("Invalid transaction identifier returned for 'getUnconfirmedTransactionIds'", exc);
        }
        return txList;
    }

    /**
     * Get the account transaction identifiers (confirmed and unconfirmed)
     *
     * @param       accountId               Account identifier
     * @return                              List of account transactions
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Long> getAccountTransactions(long accountId) throws NxtException {
        List<Long> txList;
        List<Long> confList = getConfirmedAccountTransactions(accountId);
        List<Long> unconfList = getUnconfirmedAccountTransactions(accountId);
        int size = confList.size() + unconfList.size();
        if (size == 0)
            return confList;
        txList = new ArrayList<>(confList.size()+unconfList.size());
        txList.addAll(confList);
        txList.addAll(unconfList);
        return txList;
    }

    /**
     * Get a block
     *
     * @param       blockId                 Block identifier
     * @return                              Block
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static Block getBlock(long blockId) throws NxtException {
        Block block;
        try {
            PeerResponse response = issueRequest("getBlock", "block="+Utils.idToString(blockId));
            block = new Block(response);
            if (block.getBlockId() != blockId)
                throw new NxtException("Calculated block identifier incorrect for block "+Utils.idToString(blockId));
        } catch (IdentifierException | NumberFormatException exc) {
            throw new NxtException("Invalid block data returned for 'getBlock'", exc);
        }
        return block;
    }

    /**
     * Get the current node state
     *
     * @return                              Node state
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static NodeState getNodeState() throws NxtException {
        NodeState nodeState;
        try {
            PeerResponse response = issueRequest("getState", null);
            nodeState = new NodeState(response);
        } catch (IdentifierException | NumberFormatException exc) {
            throw new NxtException("Invalid state data returned for 'getState'", exc);
        }
        return nodeState;
    }

    /**
     * Get the current block chain state
     *
     * @return                              Chain state
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static ChainState getChainState() throws NxtException {
        ChainState chainState;
        try {
            PeerResponse response = issueRequest("getBlockchainStatus", null);
            chainState = new ChainState(response);
        } catch (IdentifierException | NumberFormatException exc) {
            throw new NxtException("Invalid state data returned for 'getBlockchainStatus'", exc);
        }
        return chainState;
    }

    /**
     * Get a transaction
     *
     * @param       txId                    Transaction identifier
     * @return                              Transaction
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static Transaction getTransaction(long txId) throws NxtException {
        Transaction tx;
        try {
            PeerResponse response = issueRequest("getTransaction", "transaction="+Utils.idToString(txId));
            tx = new Transaction(response);
            if (tx.getTransactionId() != txId)
                throw new NxtException("Calculated transaction identifier incorrect for tx "+Utils.idToString(txId));
        } catch (IdentifierException | NumberFormatException exc) {
            throw new NxtException("Unable to create transaction from peer response", exc);
        }
        return tx;
    }

    /**
     * Issue the Nxt API request and return the parsed JSON response
     *
     * @param       requestType             Request type
     * @param       requestParams           Request parameters
     * @return                              Parsed JSON response
     * @throws      NxtException            Unable to issue Nxt API request
     */
    private static PeerResponse issueRequest(String requestType, String requestParams) throws NxtException {
        PeerResponse response = null;
        if (nodeName == null)
            throw new NxtException("Nxt library has not been initialized");
        try {
            URL url = new URL(String.format("http://%s:%d/nxt", nodeName, nodePort));
            String request;
            if (requestParams != null)
                request = String.format("requestType=%s&%s", requestType, requestParams);
            else
                request = String.format("requestType=%s", requestType);
            //
            // Issue the request
            //
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.format("%d", request.length()));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.connect();
            try (OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
                out.write(request);
                out.flush();
                int code = conn.getResponseCode();
                if (code != HttpURLConnection.HTTP_OK)
                    throw new NxtException(String.format("Response code %d for %s request\n  %s",
                                                         code, requestType, conn.getResponseMessage()));
            }
            //
            // Parse the response
            //
            try (InputStreamReader in = new InputStreamReader(conn.getInputStream(), "UTF-8")) {
                JSONParser parser = new JSONParser();
                response = (PeerResponse)parser.parse(in, containerFactory);
                Long errorCode = (Long)response.get("errorCode");
                if (errorCode != null) {
                    String errorDesc = (String)response.get("errorDescription");
                    throw new NxtException(String.format("Error %d returned for %s request\n  %s",
                                           errorCode, requestType, errorDesc), errorCode.intValue());
                }
            }
        } catch (MalformedURLException exc) {
            throw new NxtException("Malformed Nxt API URL", exc);
        } catch (ParseException exc) {
            throw new NxtException(String.format("JSON parse exception for %s request", requestType), exc);
        } catch (IOException exc) {
            throw new NxtException(String.format("I/O error on %s request", requestType), exc);
        }
        return response;
    }

    /**
     * JSON container factory
     *
     * We will create PeerResponse for JSONObject and List<Object> for JSONArray
     */
    private static class ResponseFactory implements ContainerFactory {

        /**
         * Create an object container
         *
         * @return                          PeerResponse object
         */
        @Override
        public Map createObjectContainer() {
            return new PeerResponse();
        }

        /**
         * Create an array container
         *
         * @return                          List<Object>
         */
        @Override
        public List<Object> creatArrayContainer() {
            return new ArrayList<>();
        }
    }
}
