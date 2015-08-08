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

import java.util.Map;

/**
 * Account ledger entry
 */
public class LedgerEntry {

    /** Ledger identifier */
    private final long ledgerId;

    /** Account identifier */
    private final long accountId;

    /** Account Reed-Solomon identifier */
    private final String accountRsId;

    /** Event type */
    private final LedgerEvent eventType;

    /** Event identifier */
    private final long eventId;

    /** Holding type */
    private final LedgerHolding holdingType;

    /** Holding identifier */
    private final long holdingId;

    /** Holding change */
    private final long change;

    /** Holding balance */
    private final long balance;

    /** Block height */
    private final int height;

    /** Entry timestamp */
    private final int timestamp;

    /** Associated transaction */
    private final Transaction transaction;

    /**
     * Create the ledger entry
     *
     * @param       response                Response for getAccountLedger/getAccountLedgerEntry request
     * @throws      IdentifierException     Invalid object identifier
     * @throws      NumberFormatException   Invalid hex string
     * @throws      NxtException            Invalid peer response
     */
    public LedgerEntry(PeerResponse response) throws IdentifierException, NumberFormatException, NxtException {
        //
        // Build the ledger event
        //
        this.ledgerId = response.getId("ledgerId");
        this.accountId = response.getId("account");
        this.accountRsId = response.getString("accountRS");
        this.change = response.getLongString("change");
        this.balance = response.getLongString("balance");
        this.height = response.getInt("height");
        this.timestamp = response.getInt("timestamp");
        String eventString = response.getString("eventType");
        if (!eventString.isEmpty()) {
            LedgerEvent enumType;
            try {
                enumType = LedgerEvent.valueOf(eventString);
            } catch (IllegalArgumentException exc) {
                enumType = null;
            }
            this.eventType = enumType;
            this.eventId = response.getId("event");
        } else {
            this.eventType = LedgerEvent.UNKNOWN;
            this.eventId = 0;
        }
        String holdingString = response.getString("holdingType");
        if (!holdingString.isEmpty()) {
            LedgerHolding enumType;
            try {
                enumType = LedgerHolding.valueOf(holdingString);
            } catch (IllegalArgumentException exc) {
                enumType = LedgerHolding.UNKNOWN;
            }
            this.holdingType = enumType;
            this.holdingId = response.getId("holding");
        } else {
            this.holdingType = null;
            this.holdingId = 0;
        }
        //
        // Get the transaction if it was included
        //
        Map<String, Object> txObject = response.getObject("transaction");
        if (!txObject.isEmpty())
            this.transaction = new Transaction(new PeerResponse(txObject));
        else
            this.transaction = null;
    }

    /**
     * Return the ledger identifier
     *
     * @return                          Ledger identifier
     */
    public long getLedgerId() {
        return ledgerId;
    }

    /**
     * Return the account identifier
     *
     * @return                          Account identifier
     */
    public long getAccountId() {
        return accountId;
    }

    /**
     * Return the account Reed-Solomon identifier
     *
     * @return                          Account Reed-Solomon identifier
     */
    public String getAccountRsId() {
        return accountRsId;
    }

    /**
     * Return the holding change
     *
     * @return                          Holding change
     */
    public long getChange() {
        return change;
    }

    /**
     * Return the holding balance
     *
     * @return                          Holding balance
     */
    public long getBalance() {
        return balance;
    }

    /**
     * Return the block height
     *
     * @return                          Block height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Return the ledger event
     *
     * @return                          Ledger event
     */
    public LedgerEvent getLedgerEvent() {
        return eventType;
    }

    /**
     * Return the ledger event identifier
     *
     * @return                          Ledger event identifier or 0 if no identifier
     */
    public long getLedgerEventId() {
        return eventId;
    }

    /**
     * Return the ledger holding
     *
     * @return                          Ledger holding or null if there is no associated holding
     */
    public LedgerHolding getLedgerHolding() {
        return holdingType;
    }

    /**
     * Return the ledger holding identifier
     *
     * @return                          Ledger holding identifier or 0 if there is no holding identifier
     */
    public long getLedgerHoldingId() {
        return holdingId;
    }

    /**
     * Return the ledger entry timestamp
     *
     * @return                          Timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Return the associated transaction
     *
     * @return                          Transaction or null if there is no transaction
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * Return the hash code
     *
     * @return                              Ledger entry hash code
     */
    @Override
    public int hashCode() {
        return Long.hashCode(ledgerId);
    }

    /**
     * Compares two transactions
     *
     * @param       obj                     Transaction to compare
     * @return                              TRUE if the transaction is equal to this transaction
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof LedgerEntry) && ledgerId==((LedgerEntry)obj).ledgerId);
    }
}
