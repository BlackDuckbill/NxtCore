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
 * Attachment for TransactionType.Messaging.ALIAS_ASSIGNMENT
 */
public class AliasAssignment extends AbstractAttachment {

    /** Version */
    private final int version;

    /** Name */
    private final String name;

    /** URI */
    private final String uri;

    /**
     * Create an Alias Assignment attachment
     *
     * @param       name                    Alias name (maximum length 100, alphanumeric characters)
     * @param       uri                     Alias URI (maximum length 1000, may be empty string)
     */
    public AliasAssignment(String name, String uri) {
        if (name == null || uri == null)
            throw new IllegalArgumentException("Required parameter not specified");
        this.version = 1;
        this.name = name.trim();
        this.uri = uri.trim();
        if (this.name.isEmpty())
            throw new IllegalArgumentException("Alias name not specified");
        if (this.name.length() > 100)
            throw new IllegalArgumentException("Maximum alias name length is 100");
        if (this.uri.length() > 1000)
            throw new IllegalArgumentException("Maximum alias URI length is 1000");
        if (!this.name.matches("\\p{Alnum}*"))
            throw new IllegalArgumentException("Alias name must consist of only alphanumeric characters");
    }

    /**
     * Create an Alias Assignment attachment from the JSON response
     *
     * @param       response                JSON response
     */
    public AliasAssignment(PeerResponse response) {
        this.version = response.getByte("version.AliasAssignment");
        this.name = response.getString("alias");
        this.uri = response.getString("uri");
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
            byte[] uriBytes = uri.getBytes("UTF-8");
            bytes = new byte[(version>0?1:0)+1+nameBytes.length+2+uriBytes.length];
            ByteBuffer buf = ByteBuffer.wrap(bytes);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            if (version > 0)
                buf.put((byte)version);
            buf.put((byte)nameBytes.length);
            buf.put(nameBytes);
            buf.putShort((short)uriBytes.length);
            buf.put(uriBytes);
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
     * Return the alias URI
     *
     * @return                              Alias URI
     */
    public String getURI() {
        return uri;
    }
}
