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

import java.util.HashMap;
import java.util.Map;

/**
 * Account ledger events
 */
public enum LedgerEvent {
    // Block and Transaction
        BLOCK_GENERATED(1, false),
        REJECT_PHASED_TRANSACTION(2, true),
    // TYPE_PAYMENT
        ORDINARY_PAYMENT(3, true),
    // TYPE_MESSAGING
        ACCOUNT_INFO(4, true),
        ALIAS_ASSIGNMENT(5, true),
        ALIAS_BUY(6, true),
        ALIAS_DELETE(7, true),
        ALIAS_SELL(8, true),
        ARBITRARY_MESSAGE(9, true),
        HUB_ANNOUNCEMENT(10, true),
        PHASING_VOTE_CASTING(11, true),
        POLL_CREATION(12, true),
        VOTE_CASTING(13, true),
    // TYPE_COLORED_COINS
        ASSET_ASK_ORDER_CANCELLATION(14, true),
        ASSET_ASK_ORDER_PLACEMENT(15, true),
        ASSET_BID_ORDER_CANCELLATION(16, true),
        ASSET_BID_ORDER_PLACEMENT(17, true),
        ASSET_DIVIDEND_PAYMENT(18, true),
        ASSET_ISSUANCE(19, true),
        ASSET_TRADE(20, true),
        ASSET_TRANSFER(21, true),
    // TYPE_DIGITAL_GOODS
        DIGITAL_GOODS_DELISTED(22, true),
        DIGITAL_GOODS_DELISTING(23, true),
        DIGITAL_GOODS_DELIVERY(24, true),
        DIGITAL_GOODS_FEEDBACK(25, true),
        DIGITAL_GOODS_LISTING(26, true),
        DIGITAL_GOODS_PRICE_CHANGE(27, true),
        DIGITAL_GOODS_PURCHASE(28, true),
        DIGITAL_GOODS_PURCHASE_EXPIRED(29, false),
        DIGITAL_GOODS_QUANTITY_CHANGE(30, true),
        DIGITAL_GOODS_REFUND(31, true),
    // TYPE_ACCOUNT_CONTROL
        ACCOUNT_CONTROL_EFFECTIVE_BALANCE_LEASING(32, true),
    // TYPE_CURRENCY
        CURRENCY_DELETION(33, true),
        CURRENCY_DISTRIBUTION(34, false),
        CURRENCY_EXCHANGE(35, true),
        CURRENCY_EXCHANGE_BUY(36, true),
        CURRENCY_EXCHANGE_SELL(37, true),
        CURRENCY_ISSUANCE(38, true),
        CURRENCY_MINTING(39, true),
        CURRENCY_OFFER_EXPIRED(40, false),
        CURRENCY_OFFER_REPLACED(41, true),
        CURRENCY_PUBLISH_EXCHANGE_OFFER(42, true),
        CURRENCY_RESERVE_CLAIM(43, true),
        CURRENCY_RESERVE_INCREASE(44, true),
        CURRENCY_TRANSFER(45, true),
        CURRENCY_UNDO_CROWDFUNDING(46, false),
    // TYPE_DATA
        TAGGED_DATA_UPLOAD(47, true),
        TAGGED_DATA_EXTEND(48, true),
    // UNKNOWN
        UNKNOWN(127, false);

    /** Event code mapping */
    private static final Map<Integer, LedgerEvent> eventMap = new HashMap<>();
    static {
        for (LedgerEvent event : values()) {
            eventMap.put(event.code, event);
        }
    }

    /** Event code */
    private final int code;

    /** Event identifier is a transaction */
    private final boolean isTransaction;

    /**
     * Create the ledger event
     *
     * @param   code                    Event code
     * @param   isTransaction           Event identifier is a transaction
     */
    private LedgerEvent(int code, boolean isTransaction) {
        this.code = code;
        this.isTransaction = isTransaction;
    }

    /**
     * Check if the event identifier is a transaction
     *
     * @return                          TRUE if the event identifier is a transaction
     */
    public boolean isTransaction() {
        return isTransaction;
    }

    /**
     * Return the event code
     *
     * @return                          Event code
     */
    public int getCode() {
        return code;
    }

    /**
     * Get the event from the event code
     *
     * @param   code                    Event code
     * @return                          Event
     */
    public static LedgerEvent fromCode(int code) {
        LedgerEvent event = eventMap.get(code);
        return (event != null ? event : LedgerEvent.UNKNOWN);
    }
}
