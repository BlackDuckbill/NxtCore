/*
 * Copyright 2015 Ronald W Hoffman.
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
 * Attachment for TransactionType.MonetarySystem.CURRENCY_MINTING
 */
public class CurrencyMinting extends AbstractAttachment {
    
    /** Version */
    private final int version;

    /** Nonce */
    private final long nonce;
    
    /** Currency identifier */
    private final long currencyId;
    
    /** Minting units expressed as an integer value with an implied decimal point */
    private final long units;
    
    /** Minting counter */
    private final long counter;
    
    /**
     * Create a Currency Minting attachment
     * 
     * @param       currencyId      Currency identifier
     * @param       units           Number of currency units minted
     * @param       counter         Current minting counter
     * @param       nonce           Nonce used for the solution hash
     */
    public CurrencyMinting(long currencyId, long units, long counter, long nonce) {
        this.version = 1;
        this.nonce = nonce;
        this.currencyId = currencyId;
        this.units = units;
        this.counter = counter;
    }
    
    /**
     * Create a Currency Minting attachment from the JSON response
     * 
     * @param       response                JSON response
     * @throws      IdentifierException     Invalid identifier data
     */
    public CurrencyMinting(PeerResponse response) throws IdentifierException {
        this.version = response.getInt("version.CurrencyMinting");
        this.nonce = response.getLongString("nonce");
        this.currencyId = response.getId("currency");
        this.units = response.getLongString("units");
        this.counter = response.getLongString("counter");
    }

    /**
     * Return the attachment byte stream
     *
     * @return                              Byte stream
     */
    @Override
    public byte[] getBytes() {
        byte[] bytes = new byte[(version>0?1:0)+8+8+8+8];
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        if (version > 0)
            buf.put((byte)version);
        buf.putLong(nonce);
        buf.putLong(currencyId);
        buf.putLong(units);
        buf.putLong(counter);
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
     * Return the nonce
     * 
     * @return                              Nonce
     */
    public long getNonce() {
        return nonce;
    }
    
    /**
     * Return the currency identifier
     * 
     * @return                              Currency identifier
     */
    public long getCurrencyId() {
        return currencyId;
    }
    
    /**
     * Return the minting units
     * 
     * @return                              Units expressed as an integer with an implied decimal point
     */
    public long getUnits() {
        return units;
    }
    
    /**
     * Return the minting counter
     * 
     * @return                              Counter
     */
    public long getCounter() {
        return counter;
    }
}
