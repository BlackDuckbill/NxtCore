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

import java.util.List;

/**
 * Account information attachment for TransactionType.Messaging.ACCOUNT_INFO
 */
public class AccountInfo implements Attachment {

    /** Account name */
    private final String name;

    /** Account description */
    private final String description;

    /**
     * Create an Account Info attachment
     *
     * @param       name                    Account name (maximum of 100 characters)
     * @param       description             Account description (maximum of 1000 characters, may be empty string)
     */
    public AccountInfo(String name, String description) {
        if (name == null || description == null)
            throw new IllegalArgumentException("Required parameter not specified");
        this.name = name.trim();
        if (this.name.length() > 100)
            throw new IllegalArgumentException("Maximum account name length is 100 characters");
        this.description = description.trim();
        if (this.description.length() > 1000)
            throw new IllegalArgumentException("Maximum account description length is 1000 characters");
    }

    /**
     * Create an Account Info attachment from the JSON response
     *
     * @param       response                JSON response
     * @throws      IdentifierException     Invalid numeric string
     * @throws      NxtException            Invalid response
     */
    public AccountInfo(PeerResponse response) throws IdentifierException, NxtException {
        this.name = response.getString("name");
        this.description = response.getString("description");
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
            byte[] descBytes = description.getBytes("UTF-8");
            bytes = new byte[1+nameBytes.length+2+descBytes.length];
            ByteBuffer buf = ByteBuffer.wrap(bytes);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put((byte)nameBytes.length);
            buf.put(nameBytes);
            buf.putShort((short)descBytes.length);
            buf.put(descBytes);
        } catch (UnsupportedEncodingException exc) {
            throw new RuntimeException(exc.getClass().getName()+": "+exc.getMessage());
        }
        return bytes;
    }

    /**
     * Return the account name
     *
     * @return                              Account name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the account description
     *
     * @return                              Account description
     */
    public String getDescription() {
        return description;
    }
}
