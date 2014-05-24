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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.List;

/**
 * Block is the response for the 'getBlock' API request
 */
public class Block {

    /** Block version */
    private final int version;

    /** Block identifier */
    private final long blockId;

    /** Block hash */
    private final byte[] blockHash;

    /** Generator identifier */
    private final long generatorId;

    /** Generator Reed-Solomon identifier */
    private final String generatorRsId;

    /** Generator public key */
    private final byte[] generatorPublicKey;

    /** Previous block identifier */
    private final long previousBlockId;

    /** Previous block hash */
    private final byte[] previousBlockHash;

    /** Next block identifier */
    private final long nextBlockId;

    /** Total amount */
    private final long totalAmount;

    /** Total fee */
    private final long totalFee;

    /** Payload length */
    private final int payloadLength;

    /** Payload hash */
    private final byte[] payloadHash;

    /** Block timestamp */
    private final int timestamp;

    /** Generation signature */
    private final byte[] generationSignature;

    /** Block signature */
    private final byte[] blockSignature;

    /** Block height */
    private final int height;

    /** Base target */
    private final long baseTarget;

    /** Transaction count */
    private final int txCount;

    /** Block transactions */
    private final List<Long> txList;

    /**
     * Create the block from the JSON response for 'getBlock'
     *
     * @param       response                Response for getBlock request
     * @param       generatorPublicKey      Generator public key
     * @throws      IdentifierException     Invalid object identifier
     * @throws      NumberFormatException   Invalid numeric string
     * @throws      NxtException            Invalid block format
     */
    public Block(PeerResponse response, byte[] generatorPublicKey)
                                throws IdentifierException, NumberFormatException, NxtException {
        this.version = response.getInt("version");
        if (version > 3)
            throw new NxtException(String.format("Block version %d is not supported", version));
        this.previousBlockId = response.getId("previousBlock");
        this.previousBlockHash = response.getHexString("previousBlockHash");
        this.nextBlockId = response.getId("nextBlock");
        this.totalAmount = response.getLongString("totalAmountNQT");
        this.totalFee = response.getLongString("totalFeeNQT");
        this.timestamp = response.getInt("timestamp");
        this.generatorId = response.getId("generator");
        this.generatorRsId = response.getString("generatorRS");
        this.generatorPublicKey = generatorPublicKey;
        this.generationSignature = response.getHexString("generationSignature");
        this.blockSignature = response.getHexString("blockSignature");
        this.payloadLength = response.getInt("payloadLength");
        this.payloadHash = response.getHexString("payloadHash");
        this.height = response.getInt("height");
        this.baseTarget = response.getLong("baseTarget");
        this.txCount = response.getInt("numberOfTransactions");
        this.txList = response.getIdList("transactions");
        //
        // Calculate the block identifier and the block hash
        //
        blockHash = Crypto.singleDigest(getBytes(false));
        blockId = Utils.fullHashToId(blockHash);
    }

    /**
     * Get the block byte stream
     *
     * @param       excludeSignature        TRUE to exclude the block signature
     * @return                              Byte stream
     */
    public final byte[] getBytes(boolean excludeSignature) {
        byte[] bytes = new byte[152 + (version>=3 ? 16 : 8) + (excludeSignature ? 0 : 64)];
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(version);
        buf.putInt(timestamp);
        buf.putLong(previousBlockId);
        buf.putInt(txCount);
        if (version >= 3) {                                         // 8-byte NQT values for version >= 3
            buf.putLong(totalAmount);
            buf.putLong(totalFee);
        } else {                                                    // 4-byte NXT values for version < 3
            buf.putInt((int)(totalAmount/Nxt.NQT_ADJUST));
            buf.putInt((int)(totalFee/Nxt.NQT_ADJUST));
        }
        buf.putInt(payloadLength);
        buf.put(payloadHash);
        buf.put(generatorPublicKey);
        buf.put(generationSignature);                               // Version 1 is 64 bytes, otherwise 32 bytes
        if (version > 1)                                            // Previous block hash added in version 2
            buf.put(previousBlockHash!=null ? previousBlockHash : new byte[32]);
        if (!excludeSignature)
            buf.put(blockSignature);
        return bytes;
    }

    /**
     * Return the block version
     *
     * @return                      Block version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Return the block timestamp in seconds since the epoch (January 1, 1970)
     *
     * @return                      Block timestamp
     */
    public long getTimestamp() {
        return timestamp + Nxt.GENESIS_TIMESTAMP;
    }

    /**
     * Return the block identifier
     *
     * @return                      Block identifier
     */
    public long getBlockId() {
        return blockId;
    }

    /**
     * Return the block hash
     *
     * @return                      Block hash
     */
    public byte[] getBlockHash() {
        return blockHash;
    }

    /**
     * Return the next block identifier
     *
     * @return                      Next block identifier or 0 if there is no next block
     */
    public long getNextBlockId() {
        return nextBlockId;
    }

    /**
     * Return the previous block identifier
     *
     * @return                      Previous block identifier or 0 if there is no previous block
     */
    public long getPreviousBlockId() {
        return previousBlockId;
    }

    /**
     * Return the previous block hash
     *
     * @return                      Previous block hash or null if there is no previous block
     */
    public byte[] getPreviousBlockHash() {
        return previousBlockHash;
    }

    /**
     * Return the block height
     *
     * @return                      Block height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Return the payload length
     *
     * @return                      Payload length
     */
    public int getPayloadLength() {
        return payloadLength;
    }

    /**
     * Return the payload hash
     *
     * @return                      Payload hash
     */
    public byte[] getPayloadHash() {
        return payloadHash;
    }

    /**
     * Return the number of transactions in the block
     *
     * @return                      Number of transactions
     */
    public int getTransactionCount() {
        return txCount;
    }

    /**
     * Return the transaction list
     *
     * @return                      List of transaction identifiers
     */
    public List<Long> getTransactionList() {
        return txList;
    }

    /**
     * Return the total amount for the transactions in the block
     *
     * @return                      Total transaction amount
     */
    public long getTotalAmount() {
        return totalAmount;
    }

    /**
     * Return the total fee for the transactions in the block
     *
     * @return                      Total transaction fee
     */
    public long getTotalFee() {
        return totalFee;
    }

    /**
     * Return the block generator identifier
     *
     * @return                      Block generator identifier
     */
    public long getGeneratorId() {
        return generatorId;
    }

    /**
     * Return the block generator Reed-Solomon identifier
     *
     * @return                      Block generator identifier
     */
    public String getGeneratorRsId() {
        return generatorRsId;
    }

    /**
     * Return the generator public key
     *
     * @return                      Generator public key
     */
    public byte[] getGeneratorPublicKey() {
        return generatorPublicKey;
    }

    /**
     * Return the block generation signature
     *
     * @return                      Block generation signature
     */
    public byte[] getGenerationSignature() {
        return generationSignature;
    }

    /**
     * Return the block signature
     *
     * @return                      Block signature
     */
    public byte[] getBlockSignature() {
        return blockSignature;
    }

    /**
     * Return the base target for the block
     *
     * @return                      Base target
     */
    public long getBaseTarget() {
        return baseTarget;
    }

    /**
     * Return the block hash code
     *
     * @return                      Block hash code
     */
    @Override
    public int hashCode() {
        return (int)blockId;
    }

    /**
     * Compares two blocks
     *
     * @param       obj             Block to compare
     * @return                      TRUE if the block is equal to this block
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof Block) && blockId==((Block)obj).blockId);
    }
}
