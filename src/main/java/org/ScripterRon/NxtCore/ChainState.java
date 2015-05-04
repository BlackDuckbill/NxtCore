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

import java.math.BigInteger;

/**
 * ChainState is the response for the 'getBlockchainStatus' request
 */
public class ChainState {

    /** Application name */
    private final String application;

    /** Application version */
    private final String version;

    /** Mainnet or testnet */
    private final boolean isTestnet;

    /** Last block identifier */
    private final long lastBlockId;

    /** Last block chain feeder */
    private final String lastFeeder;

    /** Last block chain feeder height */
    private final int lastFeederHeight;

    /** Is scanning */
    private final boolean isScanning;

    /** Number of blocks */
    private final int blockCount;

    /** Node time expressed as seconds since the genesis block time */
    private final int time;

    /** Cumulative difficulty */
    private final BigInteger cumulativeDifficulty;

    /** Minimum rollback height */
    private final int minRollbackHeight;

    /** Maximum rollback */
    private final int maxRollback;

    /** Include expired prunable data */
    private final boolean includeExpiredPrunable;

    /** Max prunable data lifetime */
    private final int maxPrunableLifetime;

    /**
     * Create the chain state from the response for the 'getBlockchainStatus' request
     *
     * @param       response                JSON response
     * @throws      IdentifierException     Invalid object identifier
     * @throws      NumberFormatException   Invalid numeric string
     */
    public ChainState(PeerResponse response) throws IdentifierException, NumberFormatException {
        this.application = response.getString("application");
        this.version = response.getString("version");
        this.isTestnet = response.getBoolean("isTestnet");
        this.lastBlockId = response.getId("lastBlock");
        this.lastFeeder = response.getString("lastBlockchainFeeder");
        this.lastFeederHeight = response.getInt("lastBlockchainFeederHeight");
        this.isScanning = response.getBoolean("isScanning");
        this.blockCount = response.getInt("numberOfBlocks");
        this.time = response.getInt("time");
        this.cumulativeDifficulty = new BigInteger(response.getString("cumulativeDifficulty"));
        this.minRollbackHeight = response.getInt("currentMinRollbackHeight");
        this.maxRollback = response.getInt("maxRollback");
        this.includeExpiredPrunable = response.getBoolean("includeExpiredPrunable");
        this.maxPrunableLifetime = response.getInt("maxPrunableLifetime");
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
     * Return the application version
     *
     * @return                      Version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Check if running on the testnet
     *
     * @return                      TRUE if running on the testnet
     */
    public boolean isTestnet() {
        return isTestnet;
    }

    /**
     * Return the number of blocks in the block chain
     *
     * @return                      Number of blocks
     */
    public int getBlockCount() {
        return blockCount;
    }

    /**
     * Return the identifier of the last block
     *
     * @return                      Last block identifier
     */
    public long getLastBlockId() {
        return lastBlockId;
    }

    /**
     * Return the minimum rollback height
     *
     * @return                      Minimum rollback height
     */
    public int getMinRollbackHeight() {
        return minRollbackHeight;
    }

    /**
     * Return the maximum rollback
     *
     * @return                      Maximum rollback
     */
    public int getMaxRollback() {
        return maxRollback;
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
     * Return the cumulative difficulty
     *
     * @return                      Cumulative difficulty
     */
    public BigInteger getCumulativeDifficulty() {
        return cumulativeDifficulty;
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
     * Check if expired prunable data is included
     *
     * @return                      TRUE if expired prunable data is included
     */
    public boolean includeExpiredPrunable() {
        return includeExpiredPrunable;
    }

    /**
     * Return maximum prunable data lifetime
     *
     * @return                      Maximum prunable data lifetime
     */
    public int getMaxPrunableLifetime() {
        return maxPrunableLifetime;
    }
}
