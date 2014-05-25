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
 * Poll Creation attachment for TransactionType.Messaging.POLL_CREATION
 */
public class PollCreation implements Attachment {

    /** Name */
    private final String name;

    /** Description */
    private final String description;

    /** Options */
    private final String[] options;

    /** Minimum number of options */
    private final int minOptions;

    /** Maximum number of options */
    private final int maxOptions;

    /** Options are binary */
    private final boolean optionsAreBinary;

    /**
     * Create a Poll Creation attachment
     *
     * @param       name                    Poll name (max 100 characters)
     * @param       description             Poll description (max 1000 characters)
     * @param       options                 Poll options (max 100 options, each option max 100 characters)
     * @param       minOptions              Minimum number of options (max 100)
     * @param       maxOptions              Maximum number of options (max 100)
     * @param       optionsAreBinary        TRUE if options are binary
     */
    public PollCreation(String name, String description, String[] options, int minOptions, int maxOptions,
                                            boolean optionsAreBinary) {
        if (name == null || description == null || options == null)
            throw new IllegalArgumentException("Required parameter not specified");
        if (name.length() > 100)
            throw new IllegalArgumentException("Maximum poll name length is 100 characters");
        if (description.length() > 1000)
            throw new IllegalArgumentException("Maximum poll description length is 1000 characters");
        if (options.length > 100)
            throw new IllegalArgumentException("Maximum number of poll options is 100");
        for (String option : options)
            if (option.length() > 100)
                throw new IllegalArgumentException("Maximum length of a poll option is 100");
        if (minOptions > 100 || maxOptions > 100)
            throw new IllegalArgumentException("Minimum/maximum poll option count is greater than 100");
        this.name = name;
        this.description = description;
        this.options = options;
        this.minOptions = minOptions;
        this.maxOptions = maxOptions;
        this.optionsAreBinary = optionsAreBinary;
    }

    /**
     * Create a Poll Creation attachment from the JSON response
     *
     * @param       response                JSON response
     */
    public PollCreation(PeerResponse response) {
        this.name = response.getString("name");
        this.description = response.getString("description");
        List<String> optionList = response.getStringList("options");
        this.options = new String[optionList.size()];
        int index = 0;
        for (String option : optionList)
            options[index++] = option;
        this.minOptions = (byte)response.getInt("minNumberOfOptions");
        this.maxOptions = (byte)response.getInt("maxNumberOfOptions");
        this.optionsAreBinary = response.getBoolean("optionsAreBinary");
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
            byte[][] optionByteList = new byte[options.length][];
            int index = 0;
            int optionLength = 0;
            for (String option : options) {
                optionByteList[index] = option.getBytes("UTF-8");
                optionLength += 2+optionByteList[index++].length;
            }
            bytes = new byte[2+nameBytes.length+2+descBytes.length+1+optionLength+1+1+1];
            ByteBuffer buf = ByteBuffer.wrap(bytes);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.putShort((short)nameBytes.length);
            buf.put(nameBytes);
            buf.putShort((short)descBytes.length);
            buf.put(descBytes);
            buf.put((byte)optionByteList.length);
            for (byte[] optionBytes : optionByteList) {
                buf.putShort((short)optionBytes.length);
                buf.put(optionBytes);
            }
            buf.put((byte)minOptions);
            buf.put((byte)maxOptions);
            buf.put(optionsAreBinary ? (byte)1 : (byte)0);
        } catch (UnsupportedEncodingException exc) {
            throw new RuntimeException(exc.getClass().getName()+": "+exc.getMessage());
        }
        return bytes;
    }

    /**
     * Return the poll name
     *
     * @return                              Poll name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the poll description
     *
     * @return                              Poll description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the poll options
     *
     * @return                              Poll options
     */
    public String[] getOptions() {
        return options;
    }

    /**
     * Return the minimum number of poll options
     *
     * @return                              Minimum number of options
     */
    public int getMinOptions() {
        return minOptions;
    }

    /**
     * Return the maximum number of poll options
     *
     * @return                              Maximum number of options
     */
    public int getMaxOptions() {
        return maxOptions;
    }

    /**
     * Check if options are binary
     *
     * @return                              TRUE if options are binary
     */
    public boolean areOptionsBinary() {
        return optionsAreBinary;
    }
}
