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

/**
 * Balance Leasing attachment for TransactionType.AccountControl.EFFECTIVE_BALANCE_LEASING
 */
public class BalanceLeasing extends AbstractAttachment {

    /** Attachment version */
    private final int version;

    /** Leasing period */
    private final int period;

    /**
     * Create a Balance Leasing attachment
     *
     * @param       period                  Leasing period in blocks (1440 or greater, less than 32768)
     */
    public BalanceLeasing(int period) {
        if (period < 1440 || period > 32767)
            throw new IllegalArgumentException("Leasing period must be between 1440 and 32767");
        this.period = period;
        this.version = 1;
    }

    /**
     * Create a Balance Leasing attachment from the JSON response
     *
     * @param       response                JSON response
     */
    public BalanceLeasing(PeerResponse response) {
        this.version = response.getByte("version.EffectiveBalanceLeasing");
        this.period = response.getInt("period");
    }

    /**
     * Return the attachment byte stream
     *
     * @return                              Byte stream
     */
    @Override
    public byte[] getBytes() {
        byte[] bytes = new byte[(version>0?1:0)+2];
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        if (version > 0)
            buf.put((byte)version);
        buf.putShort((short)period);
        return bytes;
    }

    /**
     * Return the attachment version
     *
     * @return                              Version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Return the leasing period
     *
     * @return                              Leasing period in blocks
     */
    public int getPeriod() {
        return period;
    }
}
