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

import org.json.simple.JSONObject;

/**
 * Transaction represents a transaction in a block
 */
public class Transaction {

    /** Transaction identifier */
    private final String txId;

    /** Parsed getState response */
    private final JSONObject response;

    /** Confirmation modification */
    private int modification;

    /**
     * Create the transaction
     *
     * @param       txId            Transaction identifier
     * @param       response        Response for getTransaction request
     */
    public Transaction(String txId, JSONObject response) {
        this.txId = txId;
        this.response = response;
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
     * Return the identifier of the block containing the transaction
     *
     * @return                      Block identifier
     */
    public String getBlockId() {
        return (String)response.get("block");
    }

    /**
     * Return the transaction type
     *
     * @return                      Transaction type
     */
    public int getType() {
        return ((Long)response.get("type")).intValue();
    }

    /**
     * Return the transaction subtype
     *
     * @return                      Transaction subtype
     */
    public int getSubtype() {
        return ((Long)response.get("subtype")).intValue();
    }

    /**
     * Return the transaction amount in NQT
     *
     * @return                      Transaction amount (NQT)
     */
    public long getAmount() {
        return Long.parseLong((String)response.get("amountNQT"));
    }

    /**
     * Return the transaction fee in NQT
     *
     * @return                      Transaction fee (NQT)
     */
    public long getFee() {
        return Long.parseLong((String)response.get("feeNQT"));
    }

    /**
     * Return the transaction timestamp in seconds since the epoch (January 1, 1970)
     *
     * @return                      Transaction timestamp
     */
    public long getTimeStamp() {
        return (Long)response.get("timestamp") + Nxt.genesisTimeStamp;
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
            count = confirmations.intValue() + modification;
        return count;
    }

    /**
     * Modify the number of confirmations for the transaction
     *
     * @param       modification    Confirmation modification
     */
    public void modifyConfirmations(int modification) {
        this.modification = modification;
    }

    /**
     * Return the sender account identifier
     *
     * @return                      Sender account
     */
    public String getSender() {
        return (String)response.get("sender");
    }

    /**
     * Return the recipient account identifier
     *
     * @return                      Recipient account
     */
    public String getRecipient() {
        return (String)response.get("recipient");
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
