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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Attachment for TransactionType.Messaging.ALIAS_SELL
 */
public class AliasSell implements Attachment {

    /** Version */
    private final int version;

    /** Name */
    private final String name;

    /** Price */
    private final long price;

    /**
     * Create an Alias Sell attachment
     *
     * @param       name                    Alias name (maximum length 100, alphanumeric characters)
     * @param       price                   Sell price (NQT)
     */
    public AliasSell(String name, long price) {
        if (name == null)
            throw new IllegalArgumentException("Required parameter not specified");
        this.version = 1;
        this.name = name.trim();
        this.price = price;
        if (this.name.isEmpty())
            throw new IllegalArgumentException("Alias name not specified");
        if (this.name.length() > 100)
            throw new IllegalArgumentException("Maximum alias name length is 100");
        if (!this.name.matches("\\p{Alnum}*"))
            throw new IllegalArgumentException("Alias name must consist of only alphanumeric characters");
    }

    /**
     * Create an Alias Sell attachment from the JSON response
     *
     * @param       response                JSON response
     */
    public AliasSell(PeerResponse response) {
        this.version = response.getByte("version.AliasSell");
        this.name = response.getString("alias");
        this.price = response.getLong("priceNQT");
    }

    /**
     * Return the attachment byte stream
     *
     * @return                              Byte stream
     */
    @Override
    public byte[] getBytes() {
        byte[] bytes;
        try {
            byte[] nameBytes = name.getBytes("UTF-8");
            bytes = new byte[(version>0?1:0)+1+nameBytes.length+8];
            ByteBuffer buf = ByteBuffer.wrap(bytes);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            if (version > 0)
                buf.put((byte)version);
            buf.put((byte)nameBytes.length);
            buf.put(nameBytes);
            buf.putLong(price);
        } catch (UnsupportedEncodingException exc) {
            throw new RuntimeException(exc.getClass().getName()+": "+exc.getMessage());
        }
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
     * Return the alias name
     *
     * @return                              Alias name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the sell price
     *
     * @return                              Alias price
     */
    public long getPrice() {
        return price;
    }
}
