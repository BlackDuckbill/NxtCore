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

import java.util.List;

/**
 * Block is the response for the 'getBlock' API request
 */
public class Block {

    /** Block identifier */
    private final String blockId;

    /** Parsed getState response */
    private final PeerResponse response;

    /**
     * Create the block
     *
     * @param       blockId         Block identifier from request
     * @param       response        Response for getBlock request
     */
    public Block(String blockId, PeerResponse response) {
        this.response = response;
        this.blockId = blockId;
    }

    /**
     * Return the block version
     *
     * @return                      Block version
     */
    public int getVersion() {
        return response.getInt("version");
    }

    /**
     * Return the block timestamp in seconds since the epoch (January 1, 1970)
     *
     * @return                      Block timestamp
     */
    public long getTimestamp() {
        return response.getLong("timestamp") + Nxt.genesisTimestamp;
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
     * Return the next block identifier
     *
     * @return                      Next block identifier.  An empty string is returned
     *                              if there is no next block.
     */
    public String getNextBlockId() {
        return response.getString("nextBlock");
    }

    /**
     * Return the previous block identifier
     *
     * @return                      Previous block identifier.  An empty string is returned
     *                              if there is no previous block.
     */
    public String getPreviousBlockId() {
        return response.getString("previousBlock");
    }

    /**
     * Return the previous block hash
     *
     * @return                      Previous block hash.  An empty array is returned if
     *                              there is no previous block.
     */
    public byte[] getPreviousBlockHash() {
        return Utils.parseHexString(response.getString("previousBlockHash"));
    }

    /**
     * Return the block height
     *
     * @return                      Block height
     */
    public int getHeight() {
        return response.getInt("height");
    }

    /**
     * Return the payload length
     *
     * @return                      Payload length
     */
    public int getPayloadLength() {
        return response.getInt("payloadLength");
    }

    /**
     * Return the payload hash
     *
     * @return                      Payload hash
     */
    public byte[] getPayloadHash() {
        return Utils.parseHexString(response.getString("payloadHash"));
    }

    /**
     * Return the number of transactions in the block
     *
     * @return                      Number of transactions
     */
    public int getTransactionCount() {
        return response.getInt("numberOfTransactions");
    }

    /**
     * Return the transaction list
     *
     * @return                      Transaction list
     */
    public List<String> getTransactionList() {
        return response.getStringList("transactions");
    }

    /**
     * Return the total amount for the transactions in the block
     *
     * @return                      Total transaction amount
     */
    public long getTotalAmount() {
        return response.getLong("totalAmountNQT");
    }

    /**
     * Return the total fee for the transactions in the block
     *
     * @return                      Total transaction fee
     */
    public long getTotalFee() {
        return response.getLong("totalFeeNQT");
    }

    /**
     * Return the block generator identifier
     *
     * @return                      Block generator identifier
     */
    public String getGeneratorId() {
        return response.getString("generator");
    }

    /**
     * Return the block generator Reed-Solomon identifier
     *
     * @return                      Block generator identifier
     */
    public String getGeneratorRsId() {
        return response.getString("generatorRS");
    }

    /**
     * Return the block generation signature
     *
     * @return                      Block generation signature
     */
    public byte[] getGeneratorSignature() {
        return Utils.parseHexString(response.getString("generationSignature"));
    }

    /**
     * Return the block signature
     *
     * @return                      Block signature
     */
    public byte[] getBlockSignature() {
        return Utils.parseHexString(response.getString("blockSignature"));
    }

    /**
     * Return the base target for the block
     *
     * @return                      Base target
     */
    public long getBaseTarget() {
        return response.getLong("baseTarget");
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
