/*
 * Copyright 2014-2015 Ronald Hoffman.
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

import org.json.simple.JSONArray;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Make API requests to the local Nxt node and return the results
 */
public class Nxt {

    /** Logger instance */
    public static final Logger log = LoggerFactory.getLogger("org.ScripterRon.NxtCore");

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

    /** Convert NXT to NQT */
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

    /** Default connect timeout (milliseconds) */
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

    /** Default read timeout (milliseconds) */
    private static final int DEFAULT_READ_TIMEOUT = 30000;

    /** Nxt node host name */
    private static String nodeName = "localhost";

    /** Nxt node API port */
    private static int nodePort = 7876;

    /** Connect timeout */
    private static int nodeConnectTimeout = DEFAULT_CONNECT_TIMEOUT;

    /** Read timeout */
    private static int nodeReadTimeout = DEFAULT_READ_TIMEOUT;

    /** Use HTTPS instead of HTTP */
    private static boolean useHTTPS = false;

    /** Allow certificate host name mismatch */
    private static boolean allowMismatch = false;

    /** Accept any certificates */
    private static boolean acceptAny = false;

    /**
     * Initialize the Nxt core library using default timeout values
     *
     * @param       hostName                Host name or IP address of the node server
     * @param       apiPort                 Port for the node server
     */
    public static void init(String hostName, int apiPort) {
        init(hostName, apiPort, false, false, false, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * Initialize the Nxt core library using default timeout values
     *
     * @param       hostName                Host name or IP address of the node server
     * @param       apiPort                 Port for the node server
     * @param       useSSL                  TRUE to use HTTPS instead of HTTP
     * @param       allowNameMismatch       TRUE to allow certificate host name mismatch
     * @param       acceptAnyCertificate    TRUE if any certificate should be accepted
     */
    public static void init(String hostName, int apiPort, boolean useSSL,
                                            boolean allowNameMismatch, boolean acceptAnyCertificate) {
        init(hostName, apiPort, useSSL, allowNameMismatch, acceptAnyCertificate,
                                            DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
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
        init(hostName, apiPort, false, false, false, connectTimeout, readTimeout);
    }

    /**
     * Initialize the Nxt core library using the supplied timeout values
     *
     * @param       hostName                Host name or IP address of the node server
     * @param       apiPort                 Port for the node server
     * @param       useSSL                  TRUE to use HTTPS instead of HTTP
     * @param       allowNameMismatch       TRUE to allow certificate host name mismatch
     * @param       acceptAnyCertificate    TRUE if any certificate should be accepted
     * @param       connectTimeout          HTTP connect timeout in milliseconds
     * @param       readTimeout             HTTP read timeout in milliseconds
     */
    public static void init(String hostName, int apiPort, boolean useSSL,
                                            boolean allowNameMismatch, boolean acceptAnyCertificate,
                                            int connectTimeout, int readTimeout) {
        nodeName = hostName;
        nodePort = apiPort;
        useHTTPS = useSSL;
        allowMismatch = allowNameMismatch;
        acceptAny = acceptAnyCertificate;
        nodeConnectTimeout = connectTimeout;
        nodeReadTimeout = readTimeout;
        if (useHTTPS)
            sslInit();
        log.info(String.format("API node=%s, API port=%d\n"+
                               "  HTTPS=%s, Allow mismatch=%s, Accept any=%s\n"+
                               "  Connect timeout=%d, Read timeout=%d",
                               hostName, apiPort, useHTTPS, allowMismatch, acceptAny,
                               nodeConnectTimeout, nodeReadTimeout));
    }

    /**
     * SSL initialization
     */
    private static void sslInit() {
        try {
            //
            // Create the SSL context
            //
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager[] tm = (acceptAny ? new TrustManager[] {new AllCertificates()} : null);
            context.init(null, tm, new SecureRandom());
            //
            // Set default values for HTTPS connections
            //
            HttpsURLConnection.setDefaultHostnameVerifier(new NameVerifier());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (NoSuchAlgorithmException exc) {
            log.error("TLS algorithm is not available", exc);
            throw new IllegalStateException("TLS algorithm is not available");
        } catch (KeyManagementException exc) {
            log.error("Unable to initialize SSL context", exc);
            throw new IllegalStateException("Unable to initialize SSL context", exc);
        }
    }

    /**
     * Add a peer to the server peer list and connect to the peer
     *
     * @param       announcedAddress        The announced address of the peer
     * @param       adminPW                 Administrator password
     * @return                              Peer
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static Peer addPeer(String announcedAddress, String adminPW) throws NxtException {
        Peer peer;
        try {
            PeerResponse response = issueRequest("addPeer", String.format("peer=%s&adminPassword=%s",
                                            URLEncoder.encode(announcedAddress, "UTF-8"),
                                            URLEncoder.encode(adminPW, "UTF-8")),
                                            nodeReadTimeout);
            peer = new Peer(response);
        } catch (NumberFormatException exc) {
            log.error("Invalid peer data returned for 'addPeer'", exc);
            throw new NxtException("Invalid peer data returned for 'addPeer'", exc);
        } catch (UnsupportedEncodingException exc) {
            throw new NxtException("Unable to encode request parameters", exc);
        }
        return peer;
    }

    /**
     * Blacklist a peer
     *
     * @param       announcedAddress        The announced address of the peer
     * @param       adminPW                 Administrator password
     * @return                              TRUE if the peer was blacklisted
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static boolean blacklistPeer(String announcedAddress, String adminPW) throws NxtException {
        boolean done;
        try {
            PeerResponse response = issueRequest("blacklistPeer", String.format("peer=%s&adminPassword=%s",
                                            URLEncoder.encode(announcedAddress, "UTF-8"),
                                            URLEncoder.encode(adminPW, "UTF-8")),
                                            nodeReadTimeout);
            done = response.getBoolean("done");
        } catch (UnsupportedEncodingException exc) {
            throw new NxtException("Unable to encode request parameters", exc);
        }
        return done;
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
                                                 "transactionBytes="+Utils.toHexString(tx.getBytes(false)),
                                                 nodeReadTimeout);
            txId = response.getId("transaction");
            if (txId != tx.getTransactionId())
                throw new NxtException("Incorrect transaction identifier returned for 'broadcastTransaction'");
        } catch (IdentifierException exc) {
            log.error("Invalid transaction identifier returned for 'broadcastTransaction'", exc);
            throw new NxtException("Invalid transaction identifier returned for 'broadcastTransaction'", exc);
        }
        return txId;
    }

    /**
     * Register wait events
     *
     * An existing event list can be modified by specifying 'addEvents=true' or 'removeEvents=true'.
     * A new event list will be created if both parameters are false.  An existing event listener
     * will be canceled if all of the registered events are removed.
     *
     * @param       events                  List of events to register
     * @param       addEvents               TRUE to add events to an existing event list
     * @param       removeEvents            TRUE to remove events from an existing event list
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static void eventRegister(List<String> events, boolean addEvents, boolean removeEvents)
                                            throws NxtException {
        try {
            StringBuilder sb = new StringBuilder(1000);
            for (String event : events) {
                if (sb.length() > 0)
                    sb.append("&");
                sb.append("event=").append(URLEncoder.encode(event, "UTF-8"));
            }
            if (addEvents) {
                if (sb.length() > 0)
                    sb.append("&");
                sb.append("add=true");
            }
            if (removeEvents) {
                if (sb.length() > 0)
                    sb.append("&");
                sb.append("remove=true");
            }
            issueRequest("eventRegister", (sb.length()>0 ? sb.toString() : null), nodeReadTimeout);
        } catch (UnsupportedEncodingException exc) {
            throw new NxtException("Unable to encode event name", exc);
        }
    }

    /**
     * Wait for an event
     *
     * @param       timeout                 Wait timeout (seconds)
     * @return                              Event list
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Event> eventWait(int timeout) throws NxtException {
        List<Event> events = new ArrayList<>();
        PeerResponse response = issueRequest("eventWait",
                                            String.format("timeout=%d", timeout), (timeout+5)*1000);
        List<PeerResponse> eventList = response.getObjectList("events");
        eventList.stream().forEach(resp -> events.add(new Event(resp)));
        return events;
    }

    /**
     * Get an account
     *
     * @param       accountIdRs             RS-encoded account identifier
     * @return                              Account
     * @throws      IdentifierException     Invalid account identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static Account getAccount(String accountIdRs) throws IdentifierException, NxtException {
        return getAccount(Utils.parseAccountRsId(accountIdRs));
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
            PeerResponse response = issueRequest("getAccount", "account="+Utils.idToString(accountId),
                                            nodeReadTimeout);
            account = new Account(response);
        } catch (IdentifierException | NumberFormatException exc) {
            log.error("Invalid account data returned for 'getAccount'", exc);
            throw new NxtException("Invalid account data returned for 'getAccount'", exc);
        }
        return account;
    }

    /**
     * Get the account block identifiers (blocks forged by the account)
     *
     * @param       accountIdRs             RS-encoded account identifier
     * @return                              List of account blocks
     * @throws      IdentifierException     Invalid account identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Long> getAccountBlocks(String accountIdRs)
                                            throws IdentifierException, NxtException {
        return getAccountBlocks(Utils.parseAccountRsId(accountIdRs));
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
            PeerResponse response = issueRequest("getAccountBlockIds", "account="+Utils.idToString(accountId),
                                            nodeReadTimeout);
            blockList = response.getIdList("blockIds");
        } catch (IdentifierException exc) {
            log.error("Invalid block identifier returned for 'getAccountBlockIds'", exc);
            throw new NxtException("Invalid block identifier returned for 'getAccountBlockIds'", exc);
        }
        return blockList;
    }

    /**
     * Get the public key for an account
     *
     * @param       accountIdRs             RS-encoded account identifier
     * @return                              Public key or null if the public key has not been set
     * @throws      IdentifierException     Invalid account identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static byte[] getAccountPublicKey(String accountIdRs)
                                            throws IdentifierException, NxtException {
        return getAccountPublicKey(Utils.parseAccountRsId(accountIdRs));
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
            PeerResponse response = issueRequest("getAccountPublicKey", "account="+Utils.idToString(accountId),
                                            nodeReadTimeout);
            publicKey = response.getHexString("publicKey");
        } catch (NumberFormatException exc) {
            log.error("Invalid public key returned for 'getAccountPublicKey'", exc);
            throw new NxtException("Invalid public key returned for 'getAccountPublicKey'", exc);
        }
        return publicKey;
    }

    /**
     * Get the account transaction identifiers (confirmed and unconfirmed)
     *
     * @param       accountIdRs             RS-encoded account identifier
     * @return                              List of account transactions
     * @throws      IdentifierException     Invalid account identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Long> getAccountTransactions(String accountIdRs)
                                            throws IdentifierException, NxtException {
        return getAccountTransactions(Utils.parseAccountRsId(accountIdRs));
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
            PeerResponse response = issueRequest("getAlias", "alias="+Utils.idToString(aliasId),
                                            nodeReadTimeout);
            alias = new Alias(response);
        } catch (IdentifierException exc) {
            log.error("Invalid alias data returned for 'getAlias'", exc);
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
            PeerResponse response = issueRequest("getAlias", "aliasName="+URLEncoder.encode(aliasName, "UTF-8"),
                                            nodeReadTimeout);
            alias = new Alias(response);
        } catch (IdentifierException exc) {
            log.error("Invalid alias data returned for 'getAlias'", exc);
            throw new NxtException("Invalid alias data returned for 'getAlias'", exc);
        } catch (UnsupportedEncodingException exc) {
            throw new NxtException("Unable to encode alias name", exc);
        }
        return alias;
    }

    /**
     * Get the aliases assigned to the specified account that were created after the specified time.
     *
     * @param       accountIdRs             RS-encoded account identifier
     * @param       timestamp               Alias timestamp (specify 0 to get all aliases)
     * @return                              Alias list (empty list returned if no aliases are found)
     * @throws      IdentifierException     Invalid account identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Alias> getAliases(String accountIdRs, long timestamp)
                                            throws IdentifierException, NxtException {
        return getAliases(Utils.parseAccountRsId(accountIdRs), timestamp);
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
                    String.format("account=%s&timestamp=%d", Utils.idToString(accountId), aliasTimestamp),
                                            nodeReadTimeout);
            List<PeerResponse> aliases = response.getObjectList("aliases");
            if (aliases == null) {
                aliasList = new ArrayList<>(1);
            } else {
                aliasList = new ArrayList<>(aliases.size());
                for (PeerResponse aliasResponse : aliases) {
                    Alias alias = new Alias(aliasResponse);
                    aliasList.add(alias);
                }
            }
        } catch (IdentifierException exc) {
            log.error("Invalid alias data returned for 'getAliases'", exc);
            throw new NxtException("Invalid alias data returned for 'getAliases'", exc);
        }
        return aliasList;
    }

    /**
     * Get the account balance
     *
     * @param       accountIdRs             RS-encoded account identifier
     * @return                              AccountBalance
     * @throws      IdentifierException     Invalid account identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static AccountBalance getBalance(String accountIdRs)
                                            throws IdentifierException, NxtException {
        return getBalance(Utils.parseAccountRsId(accountIdRs));
    }

    /**
     * Get the account balance
     *
     * @param       accountId               Account identifier
     * @return                              AccountBalance
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static AccountBalance getBalance(long accountId) throws NxtException {
        AccountBalance balance;
        try {
            PeerResponse response = issueRequest("getBalance", "account="+Utils.idToString(accountId),
                                            nodeReadTimeout);
            balance = new AccountBalance(accountId, response);
        } catch (NumberFormatException exc) {
            log.error("Invalid block data returned for 'getBlock'", exc);
            throw new NxtException("Invalid block data returned for 'getBlock'", exc);
        }
        return balance;
    }

    /**
     * Get a block
     *
     * @param       blockId                 Block identifier
     * @return                              Block
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static Block getBlock(long blockId) throws NxtException {
        return getBlock(blockId, false);
    }

    /** Get a block
     *
     * @param       blockId                 Block identifier
     * @param       includeTransactions     TRUE to include the block transactions or
     *                                      FALSE to include just the transaction identifiers
     * @return                              Block
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static Block getBlock(long blockId, boolean includeTransactions) throws NxtException {
        Block block;
        try {
            PeerResponse response = issueRequest("getBlock",
                                            String.format("block=%s&includeTransactions=%s",
                                                          Utils.idToString(blockId), includeTransactions),
                                            nodeReadTimeout);
            block = new Block(response);
        } catch (IdentifierException | NumberFormatException exc) {
            log.error("Invalid block data returned for 'getBlock'", exc);
            throw new NxtException("Invalid block data returned for 'getBlock'", exc);
        }
        return block;
    }

    /**
     * Get the identifier of the block at a specified height
     *
     * @param       height                  Block height
     * @return                              Block identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static long getBlockId(int height) throws NxtException {
        long blockId;
        try {
            PeerResponse response = issueRequest("getBlockId", String.format("height=%d", height),
                                            nodeReadTimeout);
            blockId = response.getId("block");
        } catch (IdentifierException | NumberFormatException exc) {
            log.error("Invalid block identifier returned for 'getBlockId'", exc);
            throw new NxtException("Invalid block identifier returned for 'getBlockId'", exc);
        }
        return blockId;
    }

    /**
     * Get a list of blocks
     *
     * @param       firstIndex              Start index (chain head is index 0)
     * @param       lastIndex               Stop index
     * @param       includeTransactions     TRUE to include the block transactions or
     *                                      FALSE to include just the transaction identifiers
     * @param       adminPW                 Administrator password
     * @return                              Block list
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Block> getBlocks(int firstIndex, int lastIndex, boolean includeTransactions, String adminPW)
                                            throws NxtException {
        List<Block> blocks = new ArrayList<>(Math.max(lastIndex-firstIndex+1, 1));
        try {
            PeerResponse response = issueRequest("getBlocks",
                    String.format("firstIndex=%d&lastIndex=%d&includeTransactions=%s&adminPassword=%s",
                                  firstIndex, lastIndex, includeTransactions, URLEncoder.encode(adminPW, "UTF-8")),
                    nodeReadTimeout);
            List<PeerResponse> blockResponses = response.getObjectList("blocks");
            for (PeerResponse blockResponse : blockResponses)
                blocks.add(new Block(blockResponse));
        } catch (IdentifierException | NumberFormatException exc) {
            log.error("Invalid block data returned for 'getBlocks'", exc);
            throw new NxtException("Invalid block data returned for 'getBlocks'", exc);
        } catch (UnsupportedEncodingException exc) {
            throw new NxtException("Unable to encode administrator password", exc);
        }
        return blocks;
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
            PeerResponse response = issueRequest("getBlockchainStatus", null, nodeReadTimeout);
            chainState = new ChainState(response);
        } catch (IdentifierException | NumberFormatException exc) {
            log.error("Invalid data returned for 'getBlockchainStatus'", exc);
            throw new NxtException("Invalid state data returned for 'getBlockchainStatus'", exc);
        }
        return chainState;
    }

    /**
     * Get the confirmed account transaction identifiers
     *
     * @param       accountIdRs             RS-encoded account identifier
     * @return                              List of transaction identifiers
     * @throws      IdentifierException     Invalid account identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Long> getConfirmedAccountTransactions(String accountIdRs)
                                            throws IdentifierException, NxtException {
        return getConfirmedAccountTransactions(Utils.parseAccountRsId(accountIdRs));
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
            PeerResponse response = issueRequest("getAccountTransactionIds", "account="+Utils.idToString(accountId),
                                            nodeReadTimeout);
            txList = response.getIdList("transactionIds");
        } catch (IdentifierException exc) {
            log.error("Invalid transaction identifier returned for 'getAccountTransactionIds'", exc);
            throw new NxtException("Invalid transaction identifier returned for 'getAccountTransactionIds'", exc);
        }
        return txList;
    }

    /**
     * Get a currency
     *
     * @param       currencyId              Currency identifier
     * @param       includeCounts           TRUE to include exchange and transfer counts in the response
     * @return                              Currency
     * @throws      IdentifierException     Invalid currency identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static Currency getCurrency(long currencyId, boolean includeCounts) throws IdentifierException, NxtException {
        Currency currency;
        try {
            PeerResponse response = issueRequest("getCurrency", String.format("currency=%s&includeCounts=%s",
                                            Utils.idToString(currencyId), includeCounts?"TRUE":"FALSE"),
                                            nodeReadTimeout);
            currency = new Currency(response);
        } catch (IdentifierException | NumberFormatException exc) {
            log.error("Invalid currency data returned for 'getCurrency'", exc);
            throw new NxtException("Invalid currency data returned for 'getCurrency'", exc);
        }
        return currency;
    }

    /**
     * Get a currency
     *
     * @param       currencyCode            Currency code (3-5 character identifier)
     * @param       includeCounts           TRUE to include exchange and transfer counts in the response
     * @return                              Currency
     * @throws      IdentifierException     Invalid currency identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static Currency getCurrency(String currencyCode, boolean includeCounts) throws IdentifierException, NxtException {
        Currency currency;
        try {
            PeerResponse response = issueRequest("getCurrency", String.format("code=%s&includeCounts=%s",
                                            currencyCode, includeCounts?"TRUE":"FALSE"), nodeReadTimeout);
            currency = new Currency(response);
        } catch (IdentifierException | NumberFormatException exc) {
            log.error("Invalid currency data returned for 'getCurrency'", exc);
            throw new NxtException("Invalid currency data returned for 'getCurrency'", exc);
        }
        return currency;
    }

    /**
     * Get the current Economic Clustering block
     *
     * @return                              EC block
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static EcBlock getEcBlock() throws NxtException {
        EcBlock ecBlock;
        try {
            PeerResponse response = issueRequest("getECBlock", null, nodeReadTimeout);
            ecBlock = new EcBlock(response);
        } catch (IdentifierException exc) {
            log.error("Invalid EC block data returned", exc);
            throw new NxtException("Invalid EC block data returned");
        }
        return ecBlock;
    }

    /**
     * Get the server forging status
     *
     * @param       adminPW                 Administrator password
     * @return                              List of log messages
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Generator> getForging(String adminPW) throws NxtException {
        List<Generator> generators;
        try {
            PeerResponse response = issueRequest("getForging", String.format("adminPassword=%s",
                                            URLEncoder.encode(adminPW, "UTF-8")),
                                            nodeReadTimeout);
            List<PeerResponse> responseList = response.getObjectList("generators");
            generators = new ArrayList<>(responseList.size());
            for (PeerResponse resp : responseList)
                generators.add(new Generator(resp));
        } catch (IdentifierException | NumberFormatException exc) {
            throw new NxtException("Invalid generator data returned", exc);
        } catch (UnsupportedEncodingException exc) {
            throw new NxtException("Unable to encode administrator password", exc);
        }
        return generators;
    }

    /**
     * Get recent server log messages
     *
     * @param       count                   Number of log messages requested
     * @param       adminPW                 Administrator password
     * @return                              List of log messages
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<String> getLog(int count, String adminPW) throws NxtException {
        List<String> messages;
        try {
            PeerResponse response = issueRequest("getLog", String.format("count=%d&adminPassword=%s",
                                            count, URLEncoder.encode(adminPW, "UTF-8")),
                                            nodeReadTimeout);
            messages = response.getStringList("messages");
        } catch (UnsupportedEncodingException exc) {
            throw new NxtException("Unable to encode administrator password", exc);
        }
        return messages;
    }

    /**
     * Get the minting target
     *
     * @param       currencyId              Currency identifier
     * @param       accountIdRs             RS-encoded account identifier
     * @param       units                   Number of units to mint expressed as a whole number with
     *                                      an implied decimal point as defined for the currency
     * @return                              Minting target
     * @throws      IdentifierException     Invalid account identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static MintingTarget getMintingTarget(long currencyId, String accountIdRs, long units)
                                            throws IdentifierException, NxtException {
        return getMintingTarget(currencyId, Utils.parseAccountRsId(accountIdRs), units);
    }

    /**
     * Get the minting target
     *
     * @param       currencyId              Currency identifier
     * @param       accountId               Account identifier
     * @param       units                   Number of units to mint expressed as a whole number with
     *                                      an implied decimal point as defined for the currency
     * @return                              Minting target
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static MintingTarget getMintingTarget(long currencyId, long accountId, long units)
                                            throws NxtException {
        MintingTarget mintingTarget;
        try {
            PeerResponse response = issueRequest("getMintingTarget", String.format("currency=%s&account=%s&units=%s",
                                            Utils.idToString(currencyId), Utils.idToString(accountId), units),
                                            nodeReadTimeout);
            mintingTarget = new MintingTarget(response);
        } catch (IdentifierException | NumberFormatException exc){
            log.error("Invalid minting data returned for 'getMintingTarget'", exc);
            throw new NxtException("Invalid minting data returned for 'getMintingTarget'", exc);
        }
        return mintingTarget;
    }

    /**
     * Get the current node state
     *
     * NOTE: getNodeState() can take a long time to complete due to the database summary information that is returned.
     * You should use getChainState() instead if you don't need the extra information.
     *
     * @return                              Node state
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static NodeState getNodeState() throws NxtException {
        NodeState nodeState;
        try {
            PeerResponse response = issueRequest("getState", null, nodeReadTimeout);
            nodeState = new NodeState(response);
        } catch (IdentifierException | NumberFormatException exc) {
            log.error("Invalid state data returned for 'getState'", exc);
            throw new NxtException("Invalid state data returned for 'getState'", exc);
        }
        return nodeState;
    }

    /**
     * Get a peer
     *
     * @param       networkAddress          The network address of the peer
     * @return                              Peer
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static Peer getPeer(String networkAddress) throws NxtException {
        Peer peer;
        try {
            PeerResponse response = issueRequest("getPeer", "peer="+URLEncoder.encode(networkAddress, "UTF-8"),
                                            nodeReadTimeout);
            peer = new Peer(response);
        } catch (NumberFormatException exc) {
            log.error("Invalid peer data returned for 'getPeer'", exc);
            throw new NxtException("Invalid peer data returned for 'getPeer'", exc);
        } catch (UnsupportedEncodingException exc) {
            throw new NxtException("Unable to encode network address", exc);
        }
        return peer;
    }

    /**
     * Get the current peer list
     *
     * @param       active                  TRUE to return just the peers in the active list
     *                                      (CONNECTED or DISCONNECTED) or FALSE to return all peers
     * @return                              List of network addresses (IPv6 addresses are enclosed in brackets)
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<String> getPeers(boolean active) throws NxtException {
        PeerResponse response = issueRequest("getPeers", "active="+(active?"true":"false"), nodeReadTimeout);
        return response.getStringList("peers");
    }

    /**
     * Get the current peer list containing peers in the requested state
     *
     * @param       state                   Peer state
     * @return                              List of network addresses (IPv6 addresses are enclosed in brackets)
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<String> getPeers(Peer.State state) throws NxtException {
        PeerResponse response = issueRequest("getPeers", "state="+state.name(), nodeReadTimeout);
        return response.getStringList("peers");
    }

    /**
     * Get the current peer information
     *
     * @param       active                  TRUE to return just the peers in the active list
     *                                      (CONNECTED or DISCONNECTED0 or FALSE to return all peers
     * @param       state                   Return peers in this state.  The state will be ignored
     *                                      if active peers are requested (active=true)
     * @return                              Peer list
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Peer> getPeers(boolean active, Peer.State state) throws NxtException {
        List<Peer> peers = new LinkedList<>();
        try {
            PeerResponse response = issueRequest("getPeers",
                                                 String.format("active=%s&state=%s&includePeerInfo=true",
                                                               active, state.name()),
                                                 nodeReadTimeout);
            List<PeerResponse> peerResponses = response.getObjectList("peers");
            for (PeerResponse peerResponse : peerResponses)
                peers.add(new Peer(peerResponse));
        } catch (NumberFormatException exc) {
            log.error("Invalid peer data returned for 'getPeers'", exc);
            throw new NxtException("Invalid peer data returned for 'getPeers'", exc);
        }
        return peers;
    }

    /**
     * Get server stack traces
     *
     * @param       depth                   Stack trace depth
     * @param       adminPW                 Administrator password
     * @return                              Stack traces
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static StackTraces getStackTraces(int depth, String adminPW) throws NxtException {
        StackTraces stackTraces;
        try {
            PeerResponse response = issueRequest("getStackTraces", String.format("depth=%d&adminPassword=%s",
                                            depth, URLEncoder.encode(adminPW, "UTF-8")), nodeReadTimeout);
            stackTraces = new StackTraces(response);
        } catch (UnsupportedEncodingException exc) {
            throw new NxtException("Unable to encode administrator password", exc);
        }
        return stackTraces;
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
            PeerResponse response = issueRequest("getTransaction", "transaction="+Utils.idToString(txId),
                                            nodeReadTimeout);
            tx = new Transaction(response);
            if (tx.getTransactionId() != txId)
                throw new NxtException("Calculated transaction identifier incorrect for tx "+Utils.idToString(txId));
        } catch (IdentifierException | NumberFormatException exc) {
            log.error("Unable to create transaction from peer response", exc);
            throw new NxtException("Unable to create transaction from peer response", exc);
        }
        return tx;
    }

    /**
     * Get the unconfirmed account transaction identifiers
     *
     * @param       accountIdRs             RS-encoded account identifier
     * @return                              List of transaction identifiers
     * @throws      IdentifierException     Invalid account identifier
     * @throws      NxtException            Unable to issue Nxt API request
     */
    public static List<Long> getUnconfirmedAccountTransactions(String accountIdRs)
                                            throws IdentifierException, NxtException {
        return getUnconfirmedAccountTransactions(Utils.parseAccountRsId(accountIdRs));
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
            PeerResponse response = issueRequest("getUnconfirmedTransactionIds",
                                            "account="+Utils.idToString(accountId),
                                            nodeReadTimeout);
            txList = response.getIdList("unconfirmedTransactionIds");
        } catch (IdentifierException exc) {
            log.error("Invalid transaction identifier returned for 'getUnconfirmedTransactionIds'", exc);
            throw new NxtException("Invalid transaction identifier returned for 'getUnconfirmedTransactionIds'", exc);
        }
        return txList;
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
            EcBlock ecBlock = getEcBlock();
            Transaction tx = new Transaction(txType, GENESIS_ACCOUNT_ID, 0, fee, deadline, null, attachment,
                                            ecBlock, passPhrase);
            txId = Nxt.broadcastTransaction(tx);
        } catch (KeyException exc) {
            log.error("Unable to sign transaction", exc);
            throw new NxtException("Unable to sign transaction", exc);
        }
        return txId;
    }

    /**
     * Mint currency
     *
     * @param       currencyId              Currency identifier
     * @param       units                   Number of units minted
     * @param       counter                 Minting counter
     * @param       nonce                   Target solution nonce
     * @param       fee                     Transaction fee (NQT)
     * @param       deadline                Transaction deadline (minutes between 1 and 1440)
     * @param       referencedTxHash        Referenced transaction hash or null
     * @param       passPhrase              Account secret key
     * @return                              Transaction identifier
     * @throws      NxtException            Unable to assign the alias
     */
    public static long currencyMint(long currencyId, long units, long counter, long nonce,
                                long fee, int deadline, byte[] referencedTxHash, String passPhrase)
                                throws NxtException {
        long txId;
        try {
            TransactionType txType = TransactionType.MonetarySystem.CURRENCY_MINTING;
            CurrencyMinting attachment = new CurrencyMinting(currencyId, units, counter, nonce);
            EcBlock ecBlock = getEcBlock();
            Transaction tx = new Transaction(txType, GENESIS_ACCOUNT_ID, 0, fee, deadline, null, attachment,
                                            ecBlock, passPhrase);
            txId = Nxt.broadcastTransaction(tx);
        } catch (KeyException exc) {
            log.error("Unable to sign transaction", exc);
            throw new NxtException("Unable to sign transaction", exc);
        }
        return txId;
    }

    /**
     * Lease effective account balance
     *
     * @param       recipientIdRs           RS-encoded recipient identifier
     * @param       period                  Lease period (blocks between 1440 and 32767)
     * @param       fee                     Transaction fee (NQT)
     * @param       deadline                Transaction deadline (minutes between 1 and 1440)
     * @param       referencedTxHash        Referenced transaction hash or null
     * @param       passPhrase              Account secret key
     * @return                              Transaction identifier
     * @throws      IdentifierException     Invalid recipient identifier
     * @throws      NxtException            Unable to lease account balance
     */
    public static long leaseBalance(String recipientIdRs, int period, long fee, int deadline,
                                            byte[] referencedTxHash, String passPhrase)
                                            throws IdentifierException, NxtException {
        return leaseBalance(Utils.parseAccountRsId(recipientIdRs), period, fee, deadline,
                                            referencedTxHash, passPhrase);
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
            EcBlock ecBlock = getEcBlock();
            Transaction tx = new Transaction(txType, recipientId, 0, fee, deadline, null, attachment,
                                            ecBlock, passPhrase);
            txId = Nxt.broadcastTransaction(tx);
        } catch (KeyException exc) {
            log.error("Unable to sign transaction", exc);
            throw new NxtException("Unable to sign transaction", exc);
        }
        return txId;
    }

    /**
     * Send a binary message
     *
     * @param       recipientIdRs           RS-encoded recipient identifier
     * @param       message                 Message to be sent (maximum length 1000)
     * @param       fee                     Transaction fee (NQT)
     * @param       deadline                Transaction deadline (minutes between 1 and 1440)
     * @param       referencedTxHash        Referenced transaction hash or null
     * @param       passPhrase              Account secret key
     * @return                              Transaction identifier
     * @throws      IdentifierException     Invalid recipient identifier
     * @throws      NxtException            Unable to send message
     */
    public static long sendMessage(String recipientIdRs, byte[] message, long fee, int deadline,
                                            byte[] referencedTxHash, String passPhrase)
                                            throws IdentifierException, NxtException {
        return sendMessage(Utils.parseAccountRsId(recipientIdRs), message, fee, deadline,
                                            referencedTxHash, passPhrase);
    }

    /**
     * Send a binary message
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
                                            byte[] referencedTxHash, String passPhrase)
                                            throws NxtException {
        long txId;
        try {
            TransactionType txType = TransactionType.Messaging.ARBITRARY_MESSAGE;
            ArbitraryMessage attachment = new ArbitraryMessage(message);
            EcBlock ecBlock = getEcBlock();
            Transaction tx = new Transaction(txType, recipientId, 0, fee, deadline, null, attachment,
                                            ecBlock, passPhrase);
            txId = Nxt.broadcastTransaction(tx);
        } catch (KeyException exc) {
            log.error("Unable to sign transaction", exc);
            throw new NxtException("Unable to sign transaction", exc);
        }
        return txId;
    }

    /**
     * Send a text message
     *
     * @param       recipientIdRs           RS-encoded recipient identifier
     * @param       message                 Message to be sent (maximum length 1000)
     * @param       fee                     Transaction fee (NQT)
     * @param       deadline                Transaction deadline (minutes between 1 and 1440)
     * @param       referencedTxHash        Referenced transaction hash or null
     * @param       passPhrase              Account secret key
     * @return                              Transaction identifier
     * @throws      IdentifierException     Invalid recipient identifier
     * @throws      NxtException            Unable to send message
     */
    public static long sendMessage(String recipientIdRs, String message, long fee, int deadline,
                                            byte[] referencedTxHash, String passPhrase)
                                            throws IdentifierException, NxtException {
        return sendMessage(Utils.parseAccountRsId(recipientIdRs), message, fee, deadline,
                                            referencedTxHash, passPhrase);
    }

    /**
     * Send a text message
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
    public static long sendMessage(long recipientId, String message, long fee, int deadline,
                                            byte[] referencedTxHash, String passPhrase)
                                            throws NxtException {
        long txId;
        try {
            TransactionType txType = TransactionType.Messaging.ARBITRARY_MESSAGE;
            ArbitraryMessage attachment = new ArbitraryMessage(message);
            EcBlock ecBlock = getEcBlock();
            Transaction tx = new Transaction(txType, recipientId, 0, fee, deadline, null, attachment,
                                            ecBlock, passPhrase);
            txId = Nxt.broadcastTransaction(tx);
        } catch (KeyException exc) {
            log.error("Unable to sign transaction", exc);
            throw new NxtException("Unable to sign transaction", exc);
        }
        return txId;
    }

    /**
     * Send Nxt
     *
     * @param       recipientIdRs           RS-encoded recipient identifier
     * @param       amount                  Amount to send (NQT)
     * @param       fee                     Transaction fee (NQT)
     * @param       deadline                Transaction deadline (minutes between 1 and 1440)
     * @param       referencedTxHash        Referenced transaction hash or null
     * @param       passPhrase              Account secret key
     * @return                              Transaction identifier
     * @throws      IdentifierException     Invalid recipient identifier
     * @throws      NxtException            Unable to send Nxt
     */
    public static long sendNxt(String recipientIdRs, long amount, long fee, int deadline,
                                            byte[] referencedTxHash, String passPhrase)
                                            throws IdentifierException, NxtException {
        return sendNxt(Utils.parseAccountRsId(recipientIdRs), amount, fee, deadline,
                                            referencedTxHash, passPhrase);
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
    public static long sendNxt(long recipientId, long amount, long fee, int deadline,
                                            byte[] referencedTxHash, String passPhrase)
                                            throws NxtException {
        long txId;
        try {
            TransactionType txType = TransactionType.Payment.ORDINARY;
            EcBlock ecBlock = getEcBlock();
            Transaction tx = new Transaction(txType, recipientId, amount, fee, deadline, null, null,
                                            ecBlock, passPhrase);
            txId = broadcastTransaction(tx);
        } catch (KeyException exc) {
            log.error("Unable to sign transaction", exc);
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
            EcBlock ecBlock = getEcBlock();
            Transaction tx = new Transaction(txType, GENESIS_ACCOUNT_ID, 0, fee, deadline,
                                            referencedTxHash, attachment, ecBlock, passPhrase);
            txId = broadcastTransaction(tx);
        } catch (KeyException exc) {
            log.error("Unable to sign transaction", exc);
            throw new NxtException("Unable to sign transaction", exc);
        }
        return txId;
    }

    /**
     * Issue the Nxt API request and return the parsed JSON response
     *
     * @param       requestType             Request type
     * @param       requestParams           Request parameters
     * @param       readTimeout             Read timeout (milliseconds)
     * @return                              Parsed JSON response
     * @throws      NxtException            Unable to issue Nxt API request
     */
    private static PeerResponse issueRequest(String requestType, String requestParams, int readTimeout)
                                            throws NxtException {
        PeerResponse response = null;
        if (nodeName == null)
            throw new NxtException("Nxt library has not been initialized");
        try {
            URL url = new URL(String.format("%s://%s:%d/nxt", (useHTTPS ? "https" : "http"), nodeName, nodePort));
            String request;
            if (requestParams != null)
                request = String.format("requestType=%s&%s", requestType, requestParams);
            else
                request = String.format("requestType=%s", requestType);
            byte[] requestBytes = request.getBytes("UTF-8");
            log.debug(String.format("Issue HTTP request to %s:%d: %s", nodeName, nodePort, request));
            //
            // Issue the request
            //
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Cache-Control", "no-cache, no-store");
            conn.setRequestProperty("Content-Length", Integer.toString(requestBytes.length));
            conn.setRequestProperty("Accept-Encoding", "gzip");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(nodeConnectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.connect();
            try (OutputStream out = conn.getOutputStream()) {
                out.write(requestBytes);
                out.flush();
                int code = conn.getResponseCode();
                if (code != HttpURLConnection.HTTP_OK) {
                    String errorText = String.format("Response code %d for %s request\n  %s",
                                                     code, requestType, conn.getResponseMessage());
                    log.error(errorText);
                    throw new NxtException(errorText);
                }
            }
            //
            // Parse the response
            //
            String contentEncoding = conn.getHeaderField("Content-Encoding");
            try (InputStream in = conn.getInputStream()) {
                InputStreamReader reader;
                if ("gzip".equals(contentEncoding))
                    reader = new InputStreamReader(new GZIPInputStream(in), "UTF-8");
                else
                    reader = new InputStreamReader(in, "UTF-8");
                JSONParser parser = new JSONParser();
                response = (PeerResponse)parser.parse(reader, containerFactory);
                Long errorCode = (Long)response.get("errorCode");
                if (errorCode != null) {
                    String errorDesc = (String)response.get("errorDescription");
                    String errorText = String.format("Error %d returned for %s request: %s",
                                           errorCode, requestType, errorDesc);
                    log.error(errorText);
                    throw new NxtException(errorText, errorCode.intValue());
                }
            }
            if (log.isDebugEnabled())
                log.debug(String.format("Request complete: Content-Encoding %s\n%s",
                                        contentEncoding, Utils.formatJSON(response)));
        } catch (MalformedURLException exc) {
            throw new NxtException("Malformed Nxt API URL", exc);
        } catch (ParseException exc) {
            String errorText = String.format("JSON parse exception for %s request: Position %d, Code %d",
                                             requestType, exc.getPosition(), exc.getErrorType());
            log.error(errorText);
            throw new NxtException(errorText);
        } catch (IOException exc) {
            String errorText = String.format("I/O error on %s request", requestType);
            log.error(errorText, exc);
            throw new NxtException(errorText, exc);
        }
        return response;
    }

    /**
     * Certificate host name verifier
     */
    private static class NameVerifier implements HostnameVerifier {

        /**
         * Check if a certificate host name mismatch is allowed
         *
         * @param       hostname            URL host name
         * @param       session             SSL session
         * @return                          TRUE if the mismatch is allowed
         */
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return allowMismatch;
        }
    }

    /**
     * Certificate trust manager to accept all certificates
     */
    private static class AllCertificates implements X509TrustManager {

        /**
         * Return a list of accepted certificate issuers
         *
         * Since we accept all certificates, we will return an empty certificate list.
         *
         * @return                          Empty certificate list
         */
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        /**
         * Build the certificate path to a trusted root certificate
         *
         * Since we accept all certificates, we will simply return
         *
         * @param   certs                   Certificate chain
         * @param   authType                Authentication type
         */
        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType)
                                            throws CertificateException {
        }

        /**
         * Build the certificate path to a trusted root certificate
         *
         * Since we accept all certificates, we will simply return
         *
         * @param   certs                   Certificate chain
         * @param   authType                Authentication type
         */
        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType)
                                            throws CertificateException {
        }
    }

    /**
     * JSON container factory
     *
     * We will create PeerResponse for JSONObject and List<Object> for JSONArray
     */
    @SuppressWarnings("unchecked")
    private static class ResponseFactory implements ContainerFactory {

        /**
         * Create an object container
         *
         * @return                          PeerResponse
         */
        @Override
        public Map<String, Object> createObjectContainer() {
            return new PeerResponse();
        }

        /**
         * Create an array container
         *
         * @return                          List<Object>
         */
        @Override
        public List<Object> creatArrayContainer() {
            return new JSONArray();
        }
    }
}
