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

import java.math.BigInteger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.json.simple.JSONObject;

/**
 * Transaction represents a transaction in a block
 */
public class Transaction {

    /** Transaction identifier */
    private final String txId;

    /** Parsed getState response */
    private final PeerResponse response;

    /** Signed transaction */
    private byte[] txBytes;

    /**
     * Create the transaction from a JSON response
     *
     * @param       response        Response for getTransaction request
     */
    public Transaction(PeerResponse response) {
        this.txId = response.getString("transaction");
        this.response = response;
    }

    /**
     * Create a signed transaction using the supplied values
     *
     * @param       type                Transaction type
     * @param       subtype             Transaction subtype
     * @param       recipientId         Transaction recipient
     * @param       amount              Transaction amount
     * @param       fee                 Transaction fee
     * @param       deadline            Transaction deadline (minutes)
     * @param       referencedTxHash    Referenced transaction hash or null
     * @param       attachment          Transaction attachment or null
     * @param       passPhrase          Sender secret phrase
     * @throws      KeyException        Unable to perform cryptographic operation
     */
    public Transaction(int type, int subtype, long recipientId, long amount, long fee,
                                    int deadline, byte[] referencedTxHash, byte[] attachment,
                                    String passPhrase) throws KeyException {
        byte[] publicKey = Crypto.getPublicKey(passPhrase);
        int timestamp = (int)((System.currentTimeMillis()+500)/1000 - Nxt.genesisTimestamp);
        //
        // Create the unsigned transaction for an ordinary payment
        //
        int baseLength = 160;
        int txLength = (attachment!=null ? baseLength+attachment.length : baseLength);
        txBytes = new byte[txLength];
        ByteBuffer txBuffer = ByteBuffer.wrap(txBytes);
        txBuffer.order(ByteOrder.LITTLE_ENDIAN);
        txBuffer.put((byte)type);
        txBuffer.put((byte)subtype);
        txBuffer.putInt(timestamp);
        txBuffer.putShort((short)deadline);
        txBuffer.put(publicKey);
        txBuffer.putLong(recipientId);
        txBuffer.putLong(amount);
        txBuffer.putLong(fee);
        if (referencedTxHash != null)
            txBuffer.put(referencedTxHash);
        if (attachment != null)
            System.arraycopy(attachment, 0, txBytes, baseLength, attachment.length);
        //
        // Create the signature for the transaction
        //
        byte[] signature = Crypto.sign(txBytes, passPhrase);
        byte[] signatureHash = Crypto.singleDigest(signature);
        //
        // Generate the transaction identifier
        //
        byte[] txHash = Crypto.singleDigest(txBytes, signatureHash);
        BigInteger bigId = new BigInteger(1, new byte[] {txHash[7], txHash[6], txHash[5], txHash[4],
                                                         txHash[3], txHash[2], txHash[1], txHash[0]});
        txId = bigId.toString();
        String fullHash = Utils.toHexString(txHash);
        //
        // Create the signed transaction
        //
        System.arraycopy(signature, 0, txBytes, baseLength-64, 64);
        //
        // Create the JSON object
        //
        response = new PeerResponse();
        response.put("type", type);
        response.put("subtype", subtype);
        response.put("timestamp", timestamp-Nxt.genesisTimestamp);
        response.put("deadline", deadline);
        response.put("senderPublicKey", Utils.toHexString(publicKey));
        response.put("sender", Utils.getAccountId(publicKey));
        response.put("recipient", Utils.idToString(recipientId));
        response.put("amountNQT", String.valueOf(amount));
        response.put("feeNQT", String.valueOf(fee));
        response.put("signature", Utils.toHexString(signature));
        response.put("signatureHash", Utils.toHexString(signatureHash));
        response.put("fullHash", fullHash);
        response.put("transaction", txId);
    }

    /**
     * Return the transaction bytes.  The transaction bytes are not available if the
     * transaction was created from the response to a 'getTransaction' request.
     *
     * @return                      Signed transaction bytes or null
     */
    public byte[] getBytes() {
        return txBytes;
    }

    /**
     * Return the transaction identifier
     *
     * @return                      Transaction identifier
     */
    public String getTransactionId() {
        return txId;
    }

    /**
     * Return the transaction hash
     *
     * @return                      Transaction hash
     */
    public byte[] getTransactionHash() {
        return Utils.parseHexString(response.getString("fullHash"));
    }

    /**
     * Return the identifier of the block containing the transaction
     *
     * @return                      Block identifier
     */
    public String getBlockId() {
        return response.getString("block");
    }

    /**
     * Return the block timestamp in seconds since the epoch (Jan 1, 1970)
     *
     * @return                      Block timestamp
     */
    public long getBlockTimestamp() {
        return response.getLong("blockTimestamp") + Nxt.genesisTimestamp;
    }

    /**
     * Return the transaction height
     *
     * @return                      Transaction height
     */
    public int getHeight() {
        return response.getInt("height");
    }

    /**
     * Return the transaction type
     *
     * @return                      Transaction type
     */
    public int getType() {
        return response.getInt("type");
    }

    /**
     * Return the transaction subtype
     *
     * @return                      Transaction subtype
     */
    public int getSubtype() {
        return response.getInt("subtype");
    }

    /**
     * Return the transaction amount
     *
     * @return                      Transaction amount
     */
    public long getAmount() {
        return response.getLongString("amountNQT");
    }

    /**
     * Return the transaction fee
     *
     * @return                      Transaction fee
     */
    public long getFee() {
        return response.getLongString("feeNQT");
    }

    /**
     * Return the transaction timestamp in seconds since the epoch (January 1, 1970)
     *
     * @return                      Transaction timestamp
     */
    public long getTimeStamp() {
        return response.getLong("timestamp") + Nxt.genesisTimestamp;
    }

    /**
     * Return the transaction deadline
     *
     * @return                      Transaction deadline in minutes
     */
    public int getDeadline() {
        return response.getInt("deadline");
    }

    /**
     * Return the number of confirmations
     *
     * @return                      Confirmation count or -1 if unconfirmed
     */
    public int getConfirmations() {
        int count = -1;
        Long confirmations = (Long)response.get("confirmations");
        if (confirmations != null)
            count = confirmations.intValue();
        return count;
    }

    /**
     * Return the sender account identifier
     *
     * @return                      Sender account identifier
     */
    public String getSenderId() {
        return response.getString("sender");
    }

    /**
     * Return the sender Reed-Solomon account identifier
     *
     * @return                      Sender account identifier
     */
    public String getSenderRsId() {
        return response.getString("senderRS");
    }

    /**
     * Return the sender public key
     *
     * @return                      Sender public key
     */
    public byte[] getSenderPublicKey() {
        return Utils.parseHexString(response.getString("senderPublicKey"));
    }

    /**
     * Return the recipient account identifier
     *
     * @return                      Recipient account identifier
     */
    public String getRecipientId() {
        return response.getString("recipient");
    }

    /**
     * Return the recipient Reed-Solomon account identifier
     *
     * @return                      Recipient account identifier
     */
    public String getRecipientRsId() {
        return response.getString("recipientRS");
    }

    /**
     * Return the transaction signature
     *
     * @return                      Transaction signature
     */
    public byte[] getSignature() {
        return Utils.parseHexString(response.getString("signature"));
    }

    /**
     * Return the transaction signature hash
     *
     * @return                      Signature hash
     */
    public byte[] getSignatureHash() {
        return Utils.parseHexString(response.getString("signatureHash"));
    }

    /**
     * Return the transaction attachment
     *
     * @return                      Transaction attachment or null if no attachment
     */
    public JSONObject getAttachment() {
        return (JSONObject)response.get("attachment");
    }

    /**
     * Return the transaction hash code
     *
     * @return                      Transaction hash code
     */
    @Override
    public int hashCode() {
        return txId.hashCode();
    }

    /**
     * Compares two transactions
     *
     * @param       obj             Transaction to compare
     * @return                      TRUE if the transaction is equal to this transaction
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof Transaction) && txId.equals(((Transaction)obj).txId));
    }
}
