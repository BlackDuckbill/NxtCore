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

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
    public static final long genesisTimestamp;
    static {
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.set(2013, 10, 24, 12, 0, 0);
        genesisTimestamp = cal.getTimeInMillis()/1000;
    }

    /** NXT <-> NQT */
    public static final long nqtAdjust = 100000000L;

    /** Transaction type mapping - key is type */
    public static final Map<Integer, String> transactionTypes = new HashMap<>();

    /** Transaction subtype mapping - key is type*100+subtype */
    public static final Map<Integer, String> transactionSubtypes = new HashMap<>();

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
     * Initialize the Nxt constants
     *
     * @param       hostName        Host name or IP address of the node server
     * @param       apiPort         Port for the node server
     * @throws      NxtException    Unable to issue Nxt API request
     */
    public static void initConstants(String hostName, int apiPort) throws NxtException {
        nodeName = hostName;
        nodePort = apiPort;
        JSONObject response = issueRequest("getConstants", null);
        //
        // Initialize the transaction type/subtype mapping
        //
        List<JSONObject> txTypes = (List<JSONObject>)response.get("transactionTypes");
        for (JSONObject txType : txTypes) {
            int type = ((Long)txType.get("value")).intValue();
            transactionTypes.put(type, (String)txType.get("description"));
            List<JSONObject> txSubtypes = (List<JSONObject>)txType.get("subtypes");
            for (JSONObject txSubtype : txSubtypes) {
                int subtype = ((Long)txSubtype.get("value")).intValue();
                transactionSubtypes.put(type*100+subtype, (String)txSubtype.get("description"));
            }
        }
    }

    /**
     * Broadcast a signed transaction
     *
     * @param       txBytes         Signed transaction bytes
     * @return                      Transaction identifier
     * @throws      NxtException    Unable to issue Nxt API request
     */
    public static String broadcastTransaction(String txBytes) throws NxtException {
        JSONObject response = issueRequest("broadcastTransaction",
                                           String.format("transactionBytes=%s", txBytes));
        return (String)response.get("transaction");
    }

    /**
     * Get an account
     *
     * @param       accountID       Account identifier
     * @return                      Account
     * @throws      NxtException    Unable to issue Nxt API request
     */
    public static Account getAccount(String accountID) throws NxtException {
        PeerResponse response = issueRequest("getAccount", String.format("account=%s", accountID));
        return new Account(response);
    }

    /**
     * Get the account block identifiers
     *
     * @param       accountID       Account identifier
     * @return                      List of account blocks
     * @throws      NxtException    Unable to issue Nxt API request
     */
    public static List<String> getAccountBlocks(String accountID) throws NxtException {
        JSONObject response = issueRequest("getAccountBlockIds", String.format("account=%s", accountID));
        return (List<String>)response.get("blockIds");
    }

    /**
     * Get the account transaction identifiers (confirmed and unconfirmed)
     *
     * @param       accountID       Account identifier
     * @return                      List of account transactions
     * @throws      NxtException    Unable to issue Nxt API request
     */
    public static List<String> getAccountTransactions(String accountID) throws NxtException {
        JSONObject response = issueRequest("getAccountTransactionIds", String.format("account=%s", accountID));
        List<String> txList = (List<String>)response.get("transactionIds");
        response = issueRequest("getUnconfirmedTransactionIds", String.format("account=%s", accountID));
        txList.addAll((List<String>)response.get("unconfirmedTransactionIds"));
        return txList;
    }

    /**
     * Get a block
     *
     * @param       blockID         Block identifier
     * @return                      Block
     * @throws      NxtException    Unable to issue Nxt API request
     */
    public static Block getBlock(String blockID) throws NxtException {
        PeerResponse response = issueRequest("getBlock", String.format("block=%s", blockID));
        return new Block(blockID, response);
    }

    /**
     * Get the current node state
     *
     * @return                      Node state
     * @throws      NxtException    Unable to issue Nxt API request
     */
    public static NodeState getState() throws NxtException {
        PeerResponse response = issueRequest("getState", null);
        return new NodeState(response);
    }

    /**
     * Get a transaction
     *
     * @param       txID            Transaction identifier
     * @return                      Transaction
     * @throws      NxtException    Unable to issue Nxt API request
     */
    public static Transaction getTransaction(String txID) throws NxtException {
        PeerResponse response = issueRequest("getTransaction", String.format("transaction=%s", txID));
        return new Transaction(response);
    }

    /**
     * Issue the Nxt API request and return the parsed JSON response
     *
     * @param       requestType     Request type
     * @param       requestParams   Request parameters
     * @return                      Parsed JSON response
     * @throws      NxtException    Unable to issue Nxt API request
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
