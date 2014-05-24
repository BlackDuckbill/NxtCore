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
 * Arbitrary Message attachment for TransactionType.Messaging.ARBITRARY_MESSAGE
 */
public class ArbitraryMessage implements Attachment {

    /** Message */
    private final byte[] message;

    /**
     * Create an Arbitrary Message attachment
     *
     * @param       message                 Message
     */
    public ArbitraryMessage(byte[] message) {
        if (message == null)
            throw new IllegalArgumentException("No message specified");
        this.message = message;
    }

    /**
     * Create an Arbitrary Message attachment from the JSON response
     *
     * @param       response                JSON response
     * @throws      NumberFormatException   Invalid numeric string
     * @throws      NxtException            Invalid response
     */
    public ArbitraryMessage(PeerResponse response) throws NumberFormatException, NxtException {
        this.message = response.getHexString("message");
        if (message == null)
            throw new NxtException("No message specified");
    }

    /**
     * Return the attachment byte stream
     *
     * @return                              Byte stream
     */
    @Override
    public byte[] getBytes() {
        byte[] bytes = new byte[4+message.length];
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(message.length);
        buf.put(message);
        return bytes;
    }

    /**
     * Return the message
     *
     * @return                              Message
     */
    public byte[] getMessage() {
        return message;
    }
}
