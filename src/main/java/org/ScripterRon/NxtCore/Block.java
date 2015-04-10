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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Block is the response for the 'getBlock' API request
 */
public class Block {

    /** Block version */
    private final int version;

    /** Block identifier */
    private final long blockId;

    /** Generator identifier */
    private final long generatorId;

    /** Generator Reed-Solomon identifier */
    private final String generatorRsId;

    /** Generator public key */
    private final byte[] generatorPublicKey;

    /** Previous block identifier */
    private final long previousBlockId;

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

    /** Block transactions identifiers */
    private final List<Long> txIdList;

    /** Block transactions */
    private final List<Transaction> txList;

    /**
     * Create the block from the JSON response for 'getBlock'
     *
     * @param       response                Response for getBlock request
     * @throws      IdentifierException     Invalid object identifier
     * @throws      NumberFormatException   Invalid numeric string
     * @throws      NxtException            Invalid block format
     */
    public Block(PeerResponse response) throws IdentifierException, NumberFormatException, NxtException {
        //
        // Set the block fields
        //
        version = response.getInt("version");
        blockId = response.getId("block");
        previousBlockId = response.getId("previousBlock");
        nextBlockId = response.getId("nextBlock");
        totalAmount = response.getLongString("totalAmountNQT");
        totalFee = response.getLongString("totalFeeNQT");
        timestamp = response.getInt("timestamp");
        generatorId = response.getId("generator");
        generatorRsId = response.getString("generatorRS");
        generatorPublicKey = response.getHexString("generatorPublicKey");
        generationSignature = response.getHexString("generationSignature");
        blockSignature = response.getHexString("blockSignature");
        payloadLength = response.getInt("payloadLength");
        payloadHash = response.getHexString("payloadHash");
        height = response.getInt("height");
        baseTarget = response.getLongString("baseTarget");
        txCount = response.getInt("numberOfTransactions");
        //
        // The block transactions are either a list of transaction identifiers
        // or a list of transactions, depending on the value of the 'includeTransactions'
        // parameter for the 'getBlock' request
        //
        Object param = response.getObject("transactions");
        if (param == null || !(param instanceof List) || ((List)param).isEmpty()) {
            txIdList = Collections.emptyList();
            txList = Collections.emptyList();
        } else if (((List)param).get(0) instanceof PeerResponse) {
            List<PeerResponse> txResponses = response.getObjectList("transactions");
            txIdList = new ArrayList<>(txResponses.size());
            txList = new ArrayList<>(txResponses.size());
            for (PeerResponse txResponse : txResponses) {
                Transaction tx = new Transaction(txResponse);
                txList.add(tx);
                txIdList.add(tx.getTransactionId());
            }
        } else {
            txIdList = response.getIdList("transactions");
            txList = Collections.emptyList();
        }
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
     * Return the block identifier as a string
     *
     * @return                      Block identifier string
     */
    public String getBlockIdString() {
        return Utils.idToString(blockId);
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
     * Return the transaction identifier list.  The list will be empty if there
     * are no transactions in the block.
     *
     * @return                      List of transaction identifiers
     */
    public List<Long> getTransactionList() {
        return txIdList;
    }

    /**
     * Return the block transactions.  The list will be empty of there are no
     * transactions in the block or if the 'getBlock' request did not include
     * transactions.
     *
     * @return                      List of transactions
     */
    public List<Transaction> getTransactions() {
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
