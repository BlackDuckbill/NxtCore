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

import java.math.BigInteger;

/**
 * NodeState is the response for the 'getState' request
 */
public class NodeState {

    /** Application name */
    private final String application;

    /** Application version */
    private final String version;

    /** Last block */
    private final long lastBlockId;

    /** Cumulative difficulty */
    private final BigInteger cumulativeDifficulty;

    /** Last block chain feeder */
    private final String lastFeeder;

    /** Last block chain feeder height */
    private final int lastFeederHeight;

    /** Number of peers */
    private final int peerCount;

    /** Number of blocks */
    private final int blockCount;

    /** Number of transactions */
    private final long txCount;

    /** Number of accounts */
    private final long accountCount;

    /** Number of aliases */
    private final long aliasCount;

    /** Number of orders */
    private final long orderCount;

    /** Number of votes */
    private final long voteCount;

    /** Number of trades */
    private final long tradeCount;

    /** Number of assets */
    private final long assetCount;

    /** Number of polls */
    private final long pollCount;

    /** Number of unlocked accounts */
    private final int unlockedAccounts;

    /** Total effective balance */
    private final long totalEffectiveBalance;

    /** Number of available processors */
    private final int processorCount;

    /** Total memory */
    private final long totalMemory;

    /** Maximum memory */
    private final long maxMemory;

    /** Free memory */
    private final long freeMemory;

    /** Is scanning the block chain */
    private final boolean isScanning;

    /** Node time */
    private final int time;

    /**
     * Create the node state
     *
     * @param       response                Response for getState request
     * @throws      IdentifierException     Invalid object identifier
     * @throws      NumberFormatException   Invalid numeric string
     */
    public NodeState(PeerResponse response) throws IdentifierException, NumberFormatException {
        this.application = response.getString("application");
        this.version = response.getString("version");
        this.lastBlockId = response.getId("lastBlock");
        this.lastFeeder = response.getString("lastBlockchainFeeder");
        this.lastFeederHeight = response.getInt("lastBlockchainFeederHeight");
        this.cumulativeDifficulty = new BigInteger(response.getString("cumulativeDifficulty"));
        this.isScanning = response.getBoolean("isScanning");
        this.unlockedAccounts = response.getInt("numberOfUnlockedAccounts");
        this.totalEffectiveBalance = response.getLong("totalEffectiveBalanceNXT") * Nxt.NQT_ADJUST;
        this.peerCount = response.getInt("numberOfPeers");
        this.blockCount = response.getInt("numberOfBlocks");
        this.txCount = response.getLong("numberOfTransactions");
        this.accountCount = response.getLong("numberOfAccounts");
        this.aliasCount = response.getLong("numberOfAliases");
        this.orderCount = response.getLong("numberOfOrders");
        this.voteCount = response.getLong("numberOfVotes");
        this.tradeCount = response.getLong("numberOfTrades");
        this.assetCount = response.getLong("numberOfAssets");
        this.pollCount = response.getLong("numberOfPolls");
        this.time = response.getInt("time");
        this.processorCount = response.getInt("availableProcessors");
        this.totalMemory = response.getLong("totalMemory");
        this.maxMemory = response.getLong("maxMemory");
        this.freeMemory = response.getLong("freeMemory");
    }

    /**
     * Return the application name
     *
     * @return                      Name
     */
    public String getApplication() {
        return application;
    }

    /**
     * Returns the application version
     *
     * @return                      Version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the number of peers
     *
     * @return                      Number of peers
     */
    public int getPeerCount() {
        return peerCount;
    }

    /**
     * Returns the identifier of the last block
     *
     * @return                      Last block identifier
     */
    public long getLastBlockId() {
        return lastBlockId;
    }

    /**
     * Check if the node is scanning the block chain
     *
     * @return                      TRUE if the node is scanning the block chain
     */
    public boolean isScanning() {
        return isScanning;
    }

    /**
     * Return the last block chain feeder
     *
     * @return                      IP address of the last block chain feeder
     */
    public String lastFeeder() {
        return lastFeeder;
    }

    /**
     * Return the chain height of the last feeder
     *
     * @return                      Chain height
     */
    public int lastFeederHeight() {
        return lastFeederHeight;
    }

    /**
     * Returns the number of blocks in the block chain
     *
     * @return                      Number of blocks
     */
    public int getBlockCount() {
        return blockCount;
    }

    /**
     * Returns the number of aliases
     *
     * @return                      Number of aliases
     */
    public long getAliasCount() {
        return aliasCount;
    }

    /**
     * Returns the number of transactions
     *
     * @return                      Number of transactions
     */
    public long getTransactionCount() {
        return txCount;
    }

    /**
     * Returns the number of orders
     *
     * @return                      Number of orders
     */
    public long getOrderCount() {
        return orderCount;
    }

    /**
     * Returns the number of trades
     *
     * @return                      Number of trades
     */
    public long getTradeCount() {
        return tradeCount;
    }

    /**
     * Returns the number of votes
     *
     * @return                      Number of votes
     */
    public long getVoteCount() {
        return voteCount;
    }

    /**
     * Returns the number of polls
     *
     * @return                      Number of polls
     */
    public long getPollCount() {
        return pollCount;
    }

    /**
     * Returns the number of assets
     *
     * @return                      Number of assets
     */
    public long getAssetCount() {
        return assetCount;
    }

    /**
     * Returns the total effective balance
     *
     * @return                      Total effective balance (NQT)
     */
    public long getTotalEffectiveBalance() {
        return totalEffectiveBalance * Nxt.NQT_ADJUST;
    }

    /**
     * Returns the number of accounts
     *
     * @return                      Number of accounts
     */
    public long getAccountCount() {
        return accountCount;
    }

    /**
     * Returns the number of unlocked accounts
     *
     * @return                      Number of unlocked accounts
     */
    public int getUnlockedAccountCount() {
        return unlockedAccounts;
    }

    /**
     * Return the cumulative difficulty
     *
     * @return                      Cumulative difficulty
     */
    public BigInteger getCumulativeDifficulty() {
        return cumulativeDifficulty;
    }

    /**
     * Return the node time in seconds since the epoch
     *
     * @return                      Node time
     */
    public long getTime() {
        return time + Nxt.GENESIS_TIMESTAMP;
    }

    /**
     * Return the number of available processors
     *
     * @return                      Number of processors
     */
    public int getProcessorCount() {
        return processorCount;
    }

    /**
     * Return the total memory
     *
     * @return                      Total memory
     */
    public long getTotalMemory() {
        return totalMemory;
    }

    /**
     * Return the maximum memory
     *
     * @return                      Maximum memory
     */
    public long getMaxMemory() {
        return maxMemory;
    }

    /**
     * Return the free memory
     *
     * @return                      Free memory
     */
    public long getFreeMemory() {
        return freeMemory;
    }
}
