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

/**
 * Economic clustering is used to prevent Nothing-at-Stake attacks by excluding
 * transactions generated on a block chain fork.
 */
public class EcBlock {

    /** Block identifier */
    private final long blockId;

    /** Block height */
    private final int height;

    /** Time when EC block determined */
    private final int timestamp;

    /**
     * Create an Economic Clustering block
     *
     * @param       blockId                 Block identifier
     * @param       height                  Block height
     * @param       timestamp               Time when EC block determined (seconds since genesis block)
     */
    public EcBlock(long blockId, int height, int timestamp) {
        this.blockId = blockId;
        this.height = height;
        this.timestamp = timestamp;
    }

    /**
     * Create an Economic Clustering block
     *
     * @param       response                Response for getECBlock
     * @throws      IdentifierException     Invalid object identifier
     */
    public EcBlock(PeerResponse response) throws IdentifierException {
        this.blockId = response.getId("ecBlockId");
        this.height = response.getInt("ecBlockHeight");
        this.timestamp = response.getInt("timestamp");
    }

    /**
     * Return the block identifier
     *
     * @return                              Block identifier
     */
    public long getBlockId() {
        return blockId;
    }

    /**
     * Return the block height
     *
     * @return                              Block height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Return the time when the EC block was determined
     *
     * @return                              Time in seconds since the epoch (January 1, 1970)
     */
    public long getTimestamp() {
        return timestamp + Nxt.GENESIS_TIMESTAMP;
    }
}
