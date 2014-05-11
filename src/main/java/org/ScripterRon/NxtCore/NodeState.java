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

/**
 * NodeState contains the current state of the local Nxt node
 */
public class NodeState {

    /** Parsed getState response */
    private final JSONObject response;

    /**
     * Create the node state
     *
     * @param       response        Response for getState request
     */
    public NodeState(JSONObject response) {
        this.response = response;
    }

    /**
     * Returns the Nxt version
     *
     * @return                      Version
     */
    public String getVersion() {
        return (String)response.get("version");
    }

    /**
     * Returns the number of peers
     *
     * @return                      Number of peers
     */
    public int getPeerCount() {
        return ((Long)response.get("numberOfPeers")).intValue();
    }

    /**
     * Returns the identifier of the last block
     *
     * @return                      Last block identifier
     */
    public String getLastBlock() {
        return (String)response.get("lastBlock");
    }

    /**
     * Returns the number of blocks in the block chain
     *
     * @return                      Number of blocks
     */
    public int getBlockCount() {
        return ((Long)response.get("numberOfBlocks")).intValue();
    }

    /**
     * Returns the number of aliases
     *
     * @return                      Number of aliases
     */
    public long getAliasCount() {
        return (Long)response.get("numberOfAliases");
    }

    /**
     * Returns the number of transactions
     *
     * @return                      Number of transactions
     */
    public long getTransactionCount() {
        return (Long)response.get("numberOfTransactions");
    }

    /**
     * Returns the number of orders
     *
     * @return                      Number of orders
     */
    public long getOrderCount() {
        return (Long)response.get("numberOfOrders");
    }

    /**
     * Returns the number of trades
     *
     * @return                      Number of trades
     */
    public long getTradeCount() {
        return (Long)response.get("numberOfTrades");
    }

    /**
     * Returns the number of votes
     *
     * @return                      Number of votes
     */
    public long getVoteCount() {
        return (Long)response.get("numberOfVotes");
    }

    /**
     * Returns the number of polls
     *
     * @return                      Number of polls
     */
    public long getPollCount() {
        return (Long)response.get("numberOfPolls");
    }

    /**
     * Returns the number of assets
     *
     * @return                      Number of assets
     */
    public long getAssetCount() {
        return (Long)response.get("numberOfAssets");
    }

    /**
     * Returns the total effective Nxt
     *
     * @return                      Total effective Nxt
     */
    public long getTotalEffectiveNxt() {
        return (Long)response.get("totalEffectiveBalanceNXT");
    }

    /**
     * Returns the number of accounts
     *
     * @return                      Number of accounts
     */
    public long getAccountCount() {
        return (Long)response.get("numberOfAccounts");
    }

    /**
     * Returns the number of unlocked accounts
     *
     * @return                      Number of unlocked accounts
     */
    public long getUnlockedAccountCount() {
        return (Long)response.get("numberOfUnlockedAccounts");
    }
}
