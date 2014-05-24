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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import java.math.BigInteger;

import java.util.Arrays;

/**
 * Utility functions
 */
public class Utils {

    /** Maximum long value */
    private static final BigInteger two64 = new BigInteger("18446744073709551616");

    /** Hex conversion alphabet */
    private static final char[] hexChars = { '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f' };

    /**
     * Get the account identifier associated with a public key
     *
     * @param       publicKey               Public key
     * @return                              Account identifier
     */
    public static long getAccountId(byte[] publicKey) {
        byte[] publicKeyHash = Crypto.singleDigest(publicKey);
        return fullHashToId(publicKeyHash);
    }

    /**
     * Get the Reed-Solomon string for an account identifier
     *
     * @param       accountId               Account identifier
     * @return                              Reed-Solomon account identifier string
     */
    public static String getAccountRsId(long accountId) {
        return "NXT-" + ReedSolomon.encode(accountId);
    }

    /**
     * Parse the Reed-Solomon account identifier string and return the account identifier
     *
     * @param       accountRsId             Reed-Solomon account string
     * @return                              Account identifier
     * @throws      IdentifierException     Invalid account identifier string
     */
    public static long parseAccountRsId(String accountRsId) throws IdentifierException {
        if (!accountRsId.startsWith("NXT-"))
            throw new IdentifierException("Invalid Reed-Solomon Nxt account identifier");
        return ReedSolomon.decode(accountRsId.substring(4));
    }

    /**
     * Convert a full hash to an object identifier using the first 8 bytes of the hash
     *
     * @param       hash                    Full hash
     * @return                              Object identifier
     */
    public static long fullHashToId(byte[] hash) {
        if (hash == null || hash.length < 8)
            throw new IllegalArgumentException("Invalid hash: " + Arrays.toString(hash));
        BigInteger bigInteger = new BigInteger(1, new byte[] {hash[7], hash[6], hash[5],
                                                              hash[4], hash[3], hash[2],
                                                              hash[1], hash[0]});
        return bigInteger.longValue();
    }

    /**
     * Convert an object identifier to a string
     *
     * @param       objectId                Object identifier
     * @return                              Identifier string
     */
    public static String idToString(long objectId) {
        if (objectId >= 0)
            return String.valueOf(objectId);
        return BigInteger.valueOf(objectId).add(two64).toString();
    }

    /**
     * Convert a string to an object identifier
     *
     * @param       number                  Object identifier string
     * @return                              Object identifier
     * @throws      IdentifierException     Object identifier is not valid
     * @throws      NumberFormatException   Object identifier is not a valid number
     */
    public static long stringToId(String number) throws IdentifierException, NumberFormatException {
        if (number.length() == 0)
            return 0;
        BigInteger bigInt = new BigInteger(number);
        if (bigInt.signum() < 0 || bigInt.compareTo(two64) != -1)
            throw new IdentifierException("Object identifier is not valid");
        return bigInt.longValue();
    }

    /**
     * Parse a hex string and return the decoded bytes
     *
     * @param       hex                     String to parse
     * @return                              Decoded bytes
     * @throws      NumberFormatException   String contains an invalid hex character
     */
    public static byte[] parseHexString(String hex) throws NumberFormatException {
        if ((hex.length()&0x01) == 1)
            throw new NumberFormatException("Hex string length is not a multiple of 2");
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int char1 = hex.charAt(i * 2);
            char1 = char1 > 0x60 ? char1 - 0x57 : char1 - 0x30;
            int char2 = hex.charAt(i * 2 + 1);
            char2 = char2 > 0x60 ? char2 - 0x57 : char2 - 0x30;
            if (char1 < 0 || char2 < 0 || char1 > 15 || char2 > 15)
                throw new NumberFormatException("Invalid hex number: " + hex);
            bytes[i] = (byte)((char1 << 4) + char2);
        }
        return bytes;
    }

    /**
     * Convert a byte array to a hex string
     *
     * @param       bytes                   Bytes to encode
     * @return                              Hex string
     */
    public static String toHexString(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            chars[i * 2] = hexChars[((bytes[i] >> 4) & 0xF)];
            chars[i * 2 + 1] = hexChars[(bytes[i] & 0xF)];
        }
        return String.valueOf(chars);
    }

    /**
     * Encode an integer as a variable-length byte stream
     *
     * @param       value                   Value to encode
     * @return                              Encoded byte stream
     */
    public static byte[] encodeInteger(int value) {
        byte[] bytes;
        if ((value&0xFFFF0000) != 0) {
            // 1 marker + 4 data bytes
            bytes = new byte[] {(byte)254, (byte)value, (byte)(value>>8), (byte)(value>>16), (byte)(value>>24)};
        } else if (value >= 253) {
            // 1 marker + 2 data bytes
            bytes = new byte[]{(byte)253, (byte)value, (byte)(value>>8)};
        } else {
            // Single data byte
            bytes = new byte[]{(byte)value};
        }
        return bytes;
    }

    /**
     * Decode a variable-length encoded integer
     *
     * @param       inStream                Input stream
     * @return                              Decoded length
     * @throws      EOFException            End-of-data while processing serialized length
     * @throws      IOException             Unable to read input stream
     */
    public static int decodeInteger(InputStream inStream) throws EOFException, IOException {
        int value;
        byte[] bytes = new byte[4];
        int ctrl = inStream.read();
        if (ctrl < 0)
            throw new EOFException("End-of-data while processing encoded integer");
        if (ctrl == 254) {
            // 1 marker + 4 data bytes
            ctrl = inStream.read(bytes, 0, 4);
            if (ctrl != 4)
                throw new EOFException("End-of-data while processing encoded integer");
            value = (int)bytes[0]&0xff + (((int)bytes[1]&0xff)<<8) + (((int)bytes[2]&0xff)<<16) +
                                         (((int)bytes[3]&0xff)<<24);
        } else if (ctrl == 253) {
            // 1 marker + 2 data bytes
            ctrl = inStream.read(bytes, 0, 2);
            if (ctrl != 2)
                throw new EOFException("End-of-data while processing encoded integer");
            value = (int)bytes[0]&0xff + (((int)bytes[1]&0xff)<<8);
        } else {
            // Single data byte
            value = ctrl;
        }
        return value;
    }
}
