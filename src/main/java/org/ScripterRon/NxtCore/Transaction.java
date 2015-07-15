/*
 * Copyright 2014-2015 Ronald Hoffman.
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
import java.util.Map;

/**
 * Transaction represents a transaction in a block
 */
public class Transaction {

    /** Zero hash */
    private static final byte[] nullHash = new byte[32];

    /** Zero signature */
    private static final byte[] nullSignature = new byte[64];

    /** Transaction has a message */
    public static final int TX_MESSAGE = 1;
    /** Transaction has an encrypted message */
    public static final int TX_ENCRYPTED_MESSAGE = 2;
    /** Transaction has a public key announcement */
    public static final int TX_PUBLIC_KEY_ANNOUNCE = 4;
    /** Transaction has encrypted message to self */
    public static final int TX_ENCRYPTED_MESSAGE_TO_SELF = 8;

    /** Transaction version */
    private final long version;

    /** EC block identifier */
    private final long ecBlockId;

    /** EC block height */
    private final int ecBlockHeight;

    /** Transaction identifier */
    private final long txId;

    /** Transaction block index */
    private final int txIndex;

    /** Transaction hash */
    private final byte[] txHash;

    /** Transaction type */
    private final TransactionType txType;

    /** Transaction is phased */
    private final boolean isPhased;

    /** Sender identifier */
    private final long senderId;

    /** Sender RS identifier */
    private final String senderRsId;

    /** Sender public key */
    private final byte[] senderPublicKey;

    /** Recipient identifier */
    private final long recipientId;

    /** Recipient RS identifier */
    private final String recipientRsId;

    /** Amount */
    private final long amount;

    /** Fee */
    private final long fee;

    /** Timestamp */
    private final int timestamp;

    /** Deadline */
    private final int deadline;

    /** Referenced transaction hash */
    private final byte[] referencedTxHash;

    /** Attachment */
    private final Attachment attachment;

    /** Block identifier */
    private final long blockId;

    /** Block timestamp */
    private final int blockTimestamp;

    /** Block height */
    private final int height;

    /** Confirmation count */
    private final int confirmations;

    /** Signature hash */
    private final byte[] signatureHash;

    /** Signature */
    private final byte[] signature;

    /**
     * Create a signed transaction from the JSON response for 'getTransaction'
     *
     * @param       response                Response for getTransaction request
     * @throws      IdentifierException     Invalid object identifier
     * @throws      NumberFormatException   Invalid hex string
     * @throws      NxtException            Invalid peer response
     */
    public Transaction(PeerResponse response) throws IdentifierException, NumberFormatException, NxtException {
        txType = TransactionType.findTransactionType(response.getByte("type"), response.getByte("subtype"));
        if (txType == null)
            throw new NxtException(String.format("Transaction type %d subtype %d is not supported",
                                                 response.getByte("type"), response.getByte("subtype")));
        version = response.getByte("version");
        txId = response.getId("transaction");
        txHash = response.getHexString("fullHash");
        amount = response.getLongString("amountNQT");
        fee = response.getLongString("feeNQT");
        senderId = response.getId("sender");
        senderRsId = response.getString("senderRS");
        long recipient = response.getId("recipient");
        if (recipient != 0)
            recipientId = recipient;
        else
            recipientId = Nxt.GENESIS_ACCOUNT_ID;
        recipientRsId = Utils.getAccountRsId(recipientId);
        timestamp = response.getInt("timestamp");
        deadline = response.getInt("deadline");
        referencedTxHash = response.getHexString("referencedTransactionFullHash");
        senderPublicKey = response.getHexString("senderPublicKey");
        signature = response.getHexString("signature");
        if (signature == null || signature.length != 64)
            throw new NxtException("Transaction signature is not valid");
        signatureHash = response.getHexString("signatureHash");
        txIndex = response.getInt("transactionIndex");
        isPhased = response.getBoolean("phased");
        blockId = response.getId("block");
        if (blockId != 0) {
            blockTimestamp = response.getInt("blockTimestamp");
            height = response.getInt("height");
            confirmations = response.getInt("confirmations");
        } else {
            blockTimestamp = 0;
            height = -1;
            confirmations = -1;
        }
        if (version > 0) {
            ecBlockId = response.getId("ecBlockId");
            ecBlockHeight = response.getInt("ecBlockHeight");
        } else {
            ecBlockId = 0;
            ecBlockHeight = 0;
        }
        Map<String, Object> attachmentResponse = response.getObject("attachment");
        if (!attachmentResponse.isEmpty())
            attachment = txType.loadAttachment(new PeerResponse(attachmentResponse));
        else
            attachment = null;
    }

    /**
     * Create a signed transaction using the supplied values
     *
     * @param       txType                  Transaction type
     * @param       recipientId             Transaction recipient
     * @param       amount                  Transaction amount
     * @param       fee                     Transaction fee
     * @param       deadline                Transaction deadline (max 1440 minutes)
     * @param       referencedTxHash        Referenced transaction hash or null
     * @param       attachment              Transaction attachment or null
     * @param       ecBlock                 Economic clustering block
     * @param       passPhrase              Sender secret phrase
     * @throws      KeyException            Unable to perform cryptographic operation
     */
    public Transaction(TransactionType txType, long recipientId, long amount, long fee,
                                    int deadline, byte[] referencedTxHash, Attachment attachment,
                                    EcBlock ecBlock, String passPhrase) throws KeyException {
        if (deadline > 1440)
            throw new IllegalArgumentException("Maximum deadline is 1440 minutes");
        this.version = 1;
        this.txType = txType;
        this.senderPublicKey = Crypto.getPublicKey(passPhrase);
        this.senderId = Utils.getAccountId(senderPublicKey);
        this.senderRsId = Utils.getAccountRsId(senderId);
        this.recipientId = recipientId;
        this.recipientRsId = Utils.getAccountRsId(recipientId);
        this.referencedTxHash = referencedTxHash;
        this.amount = amount;
        this.fee = fee;
        this.deadline = deadline;
        this.timestamp = (int)((System.currentTimeMillis()+500)/1000 - Nxt.GENESIS_TIMESTAMP);
        this.attachment = attachment;
        this.ecBlockId = ecBlock.getBlockId();
        this.ecBlockHeight = ecBlock.getHeight();
        this.isPhased = false;
        this.txIndex = -1;
        this.blockId = 0;
        this.blockTimestamp = 0;
        this.height = -1;
        this.confirmations = -1;
        //
        // Sign the transaction
        //
        byte[] txBytes = getBytes(true);
        this.signature = Crypto.sign(txBytes, passPhrase);
        this.signatureHash = Crypto.singleDigest(signature);
        //
        // Generate the transaction identifier
        //
        this.txHash = Crypto.singleDigest(txBytes, signatureHash);
        this.txId = Utils.fullHashToId(txHash);
    }

    /**
     * Return the transaction bytes
     *
     * The transaction bytes will be correct only for transactions included in Version 3 blocks or later.
     * Since the transaction does not contain a version number, we have no way of knowing if the transaction
     * was signed using NXT or NQT values for the amount and fee.
     *
     * @param       zeroSignature           TRUE to zero the signature bytes
     * @return                              Transaction bytes or null
     */
    public final byte[] getBytes(boolean zeroSignature) {
        int baseLength = 160 + (version>0 ? 16 : 0);
        int txLength;
        byte[] attachmentBytes;
        if (attachment != null) {
            attachmentBytes = attachment.getBytes();
            txLength = baseLength + attachmentBytes.length;
        } else {
            attachmentBytes = null;
            txLength = baseLength;
        }
        byte[] txBytes = new byte[txLength];
        ByteBuffer txBuffer = ByteBuffer.wrap(txBytes);
        txBuffer.order(ByteOrder.LITTLE_ENDIAN);
        txBuffer.put((txType.getType()));
        txBuffer.put((byte)(txType.getSubtype()|(version<<4)));
        txBuffer.putInt(timestamp);
        txBuffer.putShort((short)deadline);
        txBuffer.put(senderPublicKey);
        txBuffer.putLong(recipientId);
        txBuffer.putLong(amount);
        txBuffer.putLong(fee);
        if (referencedTxHash != null)
            txBuffer.put(referencedTxHash);
        else
            txBuffer.put(nullHash);
        if (!zeroSignature)
            txBuffer.put(signature);
        else
            txBuffer.put(nullSignature);
        if (version > 0) {
            txBuffer.putInt(attachment!=null ? attachment.getFlags() : 0);
            txBuffer.putInt(ecBlockHeight);
            txBuffer.putLong(ecBlockId);
        }
        if (attachmentBytes != null)
            System.arraycopy(attachmentBytes, 0, txBytes, baseLength, attachmentBytes.length);
        return txBytes;
    }

    /**
     * Return the transaction type
     *
     * @return                              Transaction type
     */
    public TransactionType getType() {
        return txType;
    }

    /**
     * Return the transaction identifier
     *
     * @return                              Transaction identifier
     */
    public long getTransactionId() {
        return txId;
    }

    /**
     * Return the transaction identifier as a string
     *
     * @return                              Transaction identifier string
     */
    public String getTransactionIdString() {
        return Utils.idToString(txId);
    }

    /**
     * Return the transaction hash
     *
     * @return                              Transaction hash
     */
    public byte[] getTransactionHash() {
        return txHash;
    }

    /**
     * Return the sender account identifier
     *
     * @return                              Sender account identifier
     */
    public long getSenderId() {
        return senderId;
    }

    /**
     * Return the sender Reed-Solomon account identifier
     *
     * @return                              Sender Reed-Solomon account identifier
     */

    public String getSenderRsId() {
        return senderRsId;
    }

    /**
     * Return the sender public key
     *
     * @return                              Sender public key
     */
    public byte[] getSenderPublicKey() {
        return senderPublicKey;
    }

    /**
     * Return the recipient account identifier
     *
     * @return                              Recipient account identifier
     */
    public long getRecipientId() {
        return recipientId;
    }

    /**
     * Return the recipient Reed-Solomon account identifier
     *
     * @return                              Recipient account identifier
     */
    public String getRecipientRsId() {
        return recipientRsId;
    }

    /**
     * Return the transaction amount
     *
     * @return                              Transaction amount
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Return the transaction fee
     *
     * @return                              Transaction fee
     */
    public long getFee() {
        return fee;
    }

    /**
     * Return the transaction timestamp in seconds since the epoch (January 1, 1970)
     *
     * @return                              Transaction timestamp
     */
    public long getTimestamp() {
        return timestamp + Nxt.GENESIS_TIMESTAMP;
    }

    /**
     * Return the transaction deadline
     *
     * @return                              Transaction deadline in minutes
     */
    public int getDeadline() {
        return deadline;
    }

    /**
     * Return the transaction signature
     *
     * @return                              Transaction signature
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * Return the transaction signature hash
     *
     * @return                              Signature hash
     */
    public byte[] getSignatureHash() {
        return signatureHash;
    }

    /**
     * Return the referenced transaction hash
     *
     * @return                              Referenced transaction hash or null
     */
    public byte[] getReferencedTxHash() {
        return referencedTxHash;
    }

    /**
     * Return the transaction attachment
     *
     * @return                              Transaction attachment or null if no attachment
     */
    public Attachment getAttachment() {
        return attachment;
    }

    /**
     * Check if this is a phased transaction
     *
     * @return                              TRUE if this is a phased transaction
     */
    public boolean isPhased() {
        return isPhased;
    }

    /**
     * Return the transaction block index
     *
     * @return                              Position within block transactions or -1 if not in a block
     */
    public int getTransactionIndex() {
        return txIndex;
    }

    /**
     * Return the block identifier
     *
     * @return                              Block identifier or 0 if not in a block
     */
    public long getBlockId() {
        return blockId;
    }

    /**
     * Return the block timestamp in seconds since the epoch (Jan 1, 1970)
     *
     * @return                              Block timestamp or 0 if not in a block
     */
    public long getBlockTimestamp() {
        return (blockId!=0 ? blockTimestamp+Nxt.GENESIS_TIMESTAMP : 0);
    }

    /**
     * Return the block height
     *
     * @return                              Block height or -1 if not in a block
     */
    public int getHeight() {
        return height;
    }

    /**
     * Return the number of confirmations
     *
     * @return                              Confirmation count or -1 if unconfirmed
     */
    public int getConfirmations() {
        return confirmations;
    }

    /**
     * Return the EC block identifier
     *
     * @return                              EC block identifier
     */
    public long getEcBlockId() {
        return ecBlockId;
    }

    /**
     * Return the EC block height
     *
     * @return                              EC block height
     */
    public int getEcBlockHeight() {
        return ecBlockHeight;
    }

    /**
     * Return the transaction hash code
     *
     * @return                              Transaction hash code
     */
    @Override
    public int hashCode() {
        return (int)txId;
    }

    /**
     * Compares two transactions
     *
     * @param       obj                     Transaction to compare
     * @return                              TRUE if the transaction is equal to this transaction
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof Transaction) && txId==((Transaction)obj).txId);
    }
}
