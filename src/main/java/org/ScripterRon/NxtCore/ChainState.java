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
 * ChainState is the response for the 'getBlockchainStatus' request
 */
public class ChainState {

    /** Application name */
    private final String application;

    /** Application version */
    private final String version;

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

    /** Node time */
    private final int time;

    /** Cumulative difficulty */
    private final BigInteger cumulativeDifficulty;

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
        this.lastBlockId = response.getId("lastBlock");
        this.lastFeeder = response.getString("lastBlockchainFeeder");
        this.lastFeederHeight = response.getInt("lastBlockchainFeederHeight");
        this.isScanning = response.getBoolean("isScanning");
        this.blockCount = response.getInt("numberOfBlocks");
        this.time = response.getInt("time");
        this.cumulativeDifficulty = new BigInteger(response.getString("cumulativeDifficulty"));
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
     * Returns the number of blocks in the block chain
     *
     * @return                      Number of blocks
     */
    public int getBlockCount() {
        return blockCount;
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
}
