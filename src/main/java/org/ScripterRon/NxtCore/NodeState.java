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

    /** Parsed getState response */
    private final PeerResponse response;

    /**
     * Create the node state
     *
     * @param       response        Response for getState request
     */
    public NodeState(PeerResponse response) {
        this.response = response;
    }

    /**
     * Returns the Nxt version
     *
     * @return                      Version
     */
    public String getVersion() {
        return response.getString("version");
    }

    /**
     * Returns the number of peers
     *
     * @return                      Number of peers
     */
    public int getPeerCount() {
        return response.getInt("numberOfPeers");
    }

    /**
     * Returns the identifier of the last block
     *
     * @return                      Last block identifier
     */
    public String getLastBlock() {
        return response.getString("lastBlock");
    }

    /**
     * Returns the number of blocks in the block chain
     *
     * @return                      Number of blocks
     */
    public int getBlockCount() {
        return response.getInt("numberOfBlocks");
    }

    /**
     * Returns the number of aliases
     *
     * @return                      Number of aliases
     */
    public long getAliasCount() {
        return response.getLong("numberOfAliases");
    }

    /**
     * Returns the number of transactions
     *
     * @return                      Number of transactions
     */
    public long getTransactionCount() {
        return response.getLong("numberOfTransactions");
    }

    /**
     * Returns the number of orders
     *
     * @return                      Number of orders
     */
    public long getOrderCount() {
        return response.getLong("numberOfOrders");
    }

    /**
     * Returns the number of trades
     *
     * @return                      Number of trades
     */
    public long getTradeCount() {
        return response.getLong("numberOfTrades");
    }

    /**
     * Returns the number of votes
     *
     * @return                      Number of votes
     */
    public long getVoteCount() {
        return response.getLong("numberOfVotes");
    }

    /**
     * Returns the number of polls
     *
     * @return                      Number of polls
     */
    public long getPollCount() {
        return response.getLong("numberOfPolls");
    }

    /**
     * Returns the number of assets
     *
     * @return                      Number of assets
     */
    public long getAssetCount() {
        return response.getLong("numberOfAssets");
    }

    /**
     * Returns the total effective Nxt
     *
     * @return                      Total effective Nxt
     */
    public long getTotalEffectiveNxt() {
        return response.getLong("totalEffectiveBalanceNXT") * Nxt.nqtAdjust;
    }

    /**
     * Returns the number of accounts
     *
     * @return                      Number of accounts
     */
    public long getAccountCount() {
        return response.getLong("numberOfAccounts");
    }

    /**
     * Returns the number of unlocked accounts
     *
     * @return                      Number of unlocked accounts
     */
    public int getUnlockedAccountCount() {
        return response.getInt("numberOfUnlockedAccounts");
    }

    /**
     * Return the cumulative difficulty
     *
     * @return                      Cumulative difficulty
     */
    public BigInteger getCumulativeDifficulty() {
        return new BigInteger(response.getString("cumulativeDifficulty"));
    }
}
