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

    /** Minimum transaction amount */
    public static final long MINIMUM_TX_AMOUNT = 1 * NQT_ADJUST;

    /** Minimum transaction fee */
    public static final long MINIMUM_TX_FEE = 1 * NQT_ADJUST;

    /** Incorrect request error */
    public static final int INCORRECT_REQUEST = 1;
    /** Missing parameter error */
    public static final int MISSING_PARAMETER = 3;
    /** Incorrect parameter error */
    public static final int INCORRECT_PARAMETER = 4;
    /** Unknown object error */
    public static final int UNKNOWN_OBJECT = 5;
    /** Insufficient funds error */
    public static final int NOT_ENOUGH = 6;
    /** Request is not allowed */
    public static final int NOT_ALLOWED = 7;
    /** Object is not available */
    public static final int OBJECT_NOT_AVAILABLE = 8;
    /** Request function is not available */
    public static final int FUNCTION_NOT_AVAILABLE = 9;

    /** Nxt node host name */
    private static String nodeName;

    /** Nxt node API port */
    private static int nodePort;

    /** Connect timeout */
    private static int nodeConnectTimeout = 5000;

    /** Read timeout */
    private static int nodeReadTimeout = 30000;

    /**
     * Initialize the Nxt core library using default timeout values
     *
     * @param       hostName                Host name or IP address of the node server
     * @param       apiPort                 Port for the node server
     */
    public static void init(String hostName, int apiPort) {
        nodeName = hostName;
        nodePort = apiPort;
    }

    /**
     * Initialize the Nxt core library using the supplied timeout values
     *
     * @param       hostName                Host name or IP address of the node server
     * @param       apiPort                 Port for the node server
     * @param       connectTimeout          HTTP connect timeout in milliseconds
     * @param       readTimeout             HTTP read timeout in milliseconds
     */
    public static void init(String hostName, int apiPort, int connectTimeout, int readTimeout) {
        nodeName = hostName;
        nodePort = apiPort;
        nodeConnectTimeout = connectTimeout;
        nodeReadTimeout = readTimeout;
    }

    /**
     * Broadcast a signed transaction
     *
     * @param       tx                      Signed transaction
     * @return                              Transaction identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static long broadcastTransaction(Transaction tx) throws NxtException {
        long txId;
        try {
            PeerResponse response = issueRequest("broadcastTransaction",
                                                 "transactionBytes="+Utils.toHexString(tx.getBytes(false)));
            txId = response.getId("transaction");
            if (txId != tx.getTransactionId())
                throw new NxtException("Incorrect transaction identifier returned for 'broadcastTransaction'");
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
            if (account.getAccountId() != accountId)
                throw new NxtException("Incorrect account returned for 'getAccount'");
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
            if (Utils.getAccountId(publicKey) != accountId)
                throw new NxtException("Incorrect public key return for 'getAccountPublicKey");
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
     * Get an alias using the alias identifier
     *
     * @param       aliasId                 Alias identifier
     * @return                              Alias or null if the alias is not found
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static Alias getAlias(long aliasId) throws NxtException {
        Alias alias;
        try {
            PeerResponse response = issueRequest("getAlias", "alias="+Utils.idToString(aliasId));
            alias = new Alias(response);
            if (alias.getAliasId() != aliasId)
                throw new NxtException("Incorrect alias returned for 'getAlias' request");
        } catch (IdentifierException exc) {
            throw new NxtException("Invalid alias data returned for 'getAlias'", exc);
        }
        return alias;
    }

    /**
     * Get an alias using the alias name
     *
     * @param       aliasName               Alias name
     * @return                              Alias or null if the alias is not found
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static Alias getAlias(String aliasName) throws NxtException {
        Alias alias;
        try {
            PeerResponse response = issueRequest("getAlias", "aliasName="+aliasName);
            alias = new Alias(response);
            if (!alias.getAliasName().equals(aliasName))
                throw new NxtException("Incorrect alias returned for 'getAlias' request");
        } catch (IdentifierException exc) {
            throw new NxtException("Invalid alias data returned for 'getAlias'", exc);
        }
        return alias;
    }

    /**
     * Get the aliases assigned to the specified account that were created after the specified time.
     *
     * @param       accountId               Account identifier
     * @param       timestamp               Alias timestamp (specify 0 to get all aliases)
     * @return                              Alias list (empty list returned if no aliases are found)
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Alias> getAliases(long accountId, long timestamp) throws NxtException {
        List<Alias> aliasList;
        long aliasTimestamp = Math.max(timestamp-GENESIS_TIMESTAMP, 0);
        try {
            PeerResponse response = issueRequest("getAliases",
                    String.format("account=%s&timestamp=%d", Utils.idToString(accountId), aliasTimestamp));
            List<PeerResponse> aliases = (List<PeerResponse>)response.get("aliases");
            if (aliases == null) {
                aliasList = new ArrayList<>(1);
            } else {
                aliasList = new ArrayList<>(aliases.size());
                for (PeerResponse aliasResponse : aliases) {
                    Alias alias = new Alias(aliasResponse);
                    if (alias.getAccountId() != accountId)
                        throw new NxtException("Incorrect alias account identifier for 'getAliases'");
                    aliasList.add(alias);
                }
            }
        } catch (IdentifierException exc) {
            throw new NxtException("Invalid alias data returned for 'getAliases'", exc);
        }
        return aliasList;
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
     * Assign an alias
     *
     * @param       aliasName               Alias name (maximum length 100, alphanumeric only)
     * @param       aliasUri                Alias URI (maximum length 1000)
     * @param       fee                     Transaction fee (NQT)
     * @param       deadline                Transaction deadline (minutes between 1 and 1440)
     * @param       referencedTxHash        Referenced transaction hash or null
     * @param       passPhrase              Account secret key
     * @return                              Transaction identifier
     * @throws      NxtException            Unable to assign the alias
     */
    public static long assignAlias(String aliasName, String aliasUri, long fee, int deadline,
                                byte[] referencedTxHash, String passPhrase) throws NxtException {
        long txId;
        try {
            TransactionType txType = TransactionType.Messaging.ALIAS_ASSIGNMENT;
            AliasAssignment attachment = new AliasAssignment(aliasName, aliasUri);
            Transaction tx = new Transaction(txType, GENESIS_ACCOUNT_ID, 0, fee, deadline, null, attachment, passPhrase);
            txId = Nxt.broadcastTransaction(tx);
        } catch (KeyException exc) {
            throw new NxtException("Unable to sign transaction", exc);
        }
        return txId;
    }

    /**
     * Lease effective account balance
     *
     * @param       recipientId             Recipient identifier
     * @param       period                  Lease period (blocks between 1440 and 32767)
     * @param       fee                     Transaction fee (NQT)
     * @param       deadline                Transaction deadline (minutes between 1 and 1440)
     * @param       referencedTxHash        Referenced transaction hash or null
     * @param       passPhrase              Account secret key
     * @return                              Transaction identifier
     * @throws      NxtException            Unable to lease account balance
     */
    public static long leaseBalance(long recipientId, int period, long fee, int deadline,
                                byte[] referencedTxHash, String passPhrase) throws NxtException {
        long txId;
        try {
            TransactionType txType = TransactionType.AccountControl.EFFECTIVE_BALANCE_LEASING;
            BalanceLeasing attachment = new BalanceLeasing(period);
            Transaction tx = new Transaction(txType, recipientId, 0, fee, deadline, null, attachment, passPhrase);
            txId = Nxt.broadcastTransaction(tx);
        } catch (KeyException exc) {
            throw new NxtException("Unable to sign transaction", exc);
        }
        return txId;
    }

    /**
     * Send a message
     *
     * @param       recipientId             Recipient identifier
     * @param       message                 Message to be sent (maximum length 1000)
     * @param       fee                     Transaction fee (NQT)
     * @param       deadline                Transaction deadline (minutes between 1 and 1440)
     * @param       referencedTxHash        Referenced transaction hash or null
     * @param       passPhrase              Account secret key
     * @return                              Transaction identifier
     * @throws      NxtException            Unable to send message
     */
    public static long sendMessage(long recipientId, byte[] message, long fee, int deadline,
                                byte[] referencedTxHash, String passPhrase) throws NxtException {
        long txId;
        try {
            TransactionType txType = TransactionType.Messaging.ARBITRARY_MESSAGE;
            ArbitraryMessage attachment = new ArbitraryMessage(message);
            Transaction tx = new Transaction(txType, recipientId, 0, fee, deadline, null, attachment, passPhrase);
            txId = Nxt.broadcastTransaction(tx);
        } catch (KeyException exc) {
            throw new NxtException("Unable to sign transaction", exc);
        }
        return txId;
    }

    /**
     * Send Nxt
     *
     * @param       recipientId             Recipient identifier
     * @param       amount                  Amount to send (NQT)
     * @param       fee                     Transaction fee (NQT)
     * @param       deadline                Transaction deadline (minutes between 1 and 1440)
     * @param       referencedTxHash        Referenced transaction hash or null
     * @param       passPhrase              Account secret key
     * @return                              Transaction identifier
     * @throws      NxtException            Unable to send Nxt
     */
    public static long sendNxt(long recipientId, long amount, long fee, int deadline, byte[] referencedTxHash,
                               String passPhrase) throws NxtException {
        long txId;
        try {
            TransactionType txType = TransactionType.Payment.ORDINARY;
            Transaction tx = new Transaction(txType, recipientId, amount, fee, deadline, null, null, passPhrase);
            txId = broadcastTransaction(tx);
        } catch (KeyException exc) {
            throw new NxtException("Unable to sign transaction", exc);
        }
        return txId;
    }

    /**
     * Set account information
     *
     * @param       accountName             Account name (maximum length 100)
     * @param       accountDescription      Account description (maximum length 1000, may be empty string)
     * @param       fee                     Transaction fee (NQT)
     * @param       deadline                Transaction deadline (minutes between 1 and 1440)
     * @param       referencedTxHash        Referenced transaction hash or null
     * @param       passPhrase              Account secret key
     * @return                              Transaction identifier
     * @throws      NxtException            Unable to set account information
     */
    public static long setAccountInfo(String accountName, String accountDescription, long fee, int deadline,
                                byte[] referencedTxHash, String passPhrase) throws NxtException {
        long txId;
        try {
            TransactionType txType = TransactionType.Messaging.ACCOUNT_INFO;
            AccountInfo attachment = new AccountInfo(accountName, accountDescription);
            Transaction tx = new Transaction(txType, GENESIS_ACCOUNT_ID, 0, fee, deadline,
                                referencedTxHash, attachment, passPhrase);
            txId = broadcastTransaction(tx);
        } catch (KeyException exc) {
            throw new NxtException("Unable to sign transaction", exc);
        }
        return txId;
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
            conn.setConnectTimeout(nodeConnectTimeout);
            conn.setReadTimeout(nodeReadTimeout);
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
