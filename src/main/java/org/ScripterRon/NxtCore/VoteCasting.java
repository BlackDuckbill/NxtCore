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
 * Vote Casting attachment for TransactionType.Messaging.VOTE_CASTING
 */
public class VoteCasting implements Attachment {

    /** Poll identifier */
    private final long pollId;

    /** Poll votes */
    private final byte[] votes;

    /**
     * Create a Vote Casting attachment
     *
     * @param       pollId                  Poll identifier
     * @param       votes                   Poll votes (one byte for each poll option)
     */
    public VoteCasting(long pollId, byte[] votes) {
        if (votes == null)
            throw new IllegalArgumentException("Poll votes not specified");
        this.pollId = pollId;
        this.votes = votes;
    }

    /**
     * Create a Vote Casting attachment from the JSON response
     *
     * @param       response                JSON response
     * @throws      IdentifierException     Invalid numeric string
     * @throws      NxtException            Invalid response
     */
    public VoteCasting(PeerResponse response) throws IdentifierException, NxtException {
        this.pollId = response.getId("pollId");
        List<Long> voteList = response.getLongList("vote");
        votes = new byte[voteList.size()];
        int index = 0;
        for (Long vote : voteList)
            votes[index++] = vote.byteValue();
    }

    /**
     * Return the attachment byte stream
     *
     * @return                              Byte stream
     */
    @Override
    public byte[] getBytes() {
        byte[] bytes = new byte[8+1+votes.length];
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putLong(pollId);
        buf.put((byte)votes.length);
        buf.put(votes);
        return bytes;
    }

    /**
     * Return the poll identifier
     *
     * @return                              Poll identifier
     */
    public long getPollId() {
        return pollId;
    }

    /**
     * Return the votes as a byte array with one byte for each poll option
     *
     * @return                              Vote byte array
     */
    public byte[] getVotes() {
        return votes;
    }
}
