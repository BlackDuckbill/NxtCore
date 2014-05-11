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
 * Block represent a block in the Nxt block chain
 */
public class Block {

    /** Block identifier */
    private final String blockId;

    /** Parsed getState response */
    private final JSONObject response;

    /**
     * Create the block
     *
     * @param       blockId         Block identifier
     * @param       response        Response for getBlock request
     */
    public Block(String blockId, JSONObject response) {
        this.blockId = blockId;
        this.response = response;
    }

    /**
     * Return the block identifier
     *
     * @return                      Block identifier
     */
    public String getBlockId() {
        return blockId;
    }

    /**
     * Return the block version
     *
     * @return                      Block version
     */
    public int getVersion() {
        return ((Long)response.get("version")).intValue();
    }

    /**
     * Return the block height
     *
     * @return                      Block height
     */
    public int getHeight() {
        return ((Long)response.get("height")).intValue();
    }

    /**
     * Return the number of transactions in the block
     *
     * @return                      Number of transactions
     */
    public int getTransactionCount() {
        return ((Long)response.get("numberOfTransactions")).intValue();
    }

    /**
     * Return the block timestamp in seconds since the epoch (January 1, 1970)
     *
     * @return                      Block timestamp
     */
    public long getTimeStamp() {
        return (Long)response.get("timestamp") + Nxt.genesisTimeStamp;
    }

    /**
     * Return the previous block identifier
     *
     * @return                      Previous block identifier
     */
    public String getPreviousBlock() {
        return (String)response.get("previousBlock");
    }

    /**
     * Return the block hash code
     *
     * @return                      Block hash code
     */
    @Override
    public int hashCode() {
        return blockId.hashCode();
    }

    /**
     * Compares two blocks
     *
     * @param       obj             Block to compare
     * @return                      TRUE if the block is equal to this block
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof Block) && blockId.equals(((Block)obj).blockId));
    }
}
