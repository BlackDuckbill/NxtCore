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
 * Account ledger holdings
 */
public enum LedgerHolding {
    UNCONFIRMED_NXT_BALANCE(1, true),
    NXT_BALANCE(2, false),
    UNCONFIRMED_ASSET_BALANCE(3, true),
    ASSET_BALANCE(4, false),
    UNCONFIRMED_CURRENCY_BALANCE(5, true),
    CURRENCY_BALANCE(6, false),
    UNKNOWN(127, false);

    /** Holding code mapping */
    private static final Map<Integer, LedgerHolding> holdingMap = new HashMap<>();
    static {
        for (LedgerHolding holding : values()) {
            holdingMap.put(holding.code, holding);
        }
    }

    /** Holding code */
    private final int code;

    /** Unconfirmed holding */
    private final boolean isUnconfirmed;

    /**
     * Create the holding event
     *
     * @param   code                    Holding code
     * @param   isUnconfirmed           TRUE if the holding is unconfirmed
     */
    private LedgerHolding(int code, boolean isUnconfirmed) {
        this.code = code;
        this.isUnconfirmed = isUnconfirmed;
    }

    /**
     * Check if the holding is unconfirmed
     *
     * @return                          TRUE if the holding is unconfirmed
     */
    public boolean isUnconfirmed() {
        return this.isUnconfirmed;
    }

    /**
     * Return the holding code
     *
     * @return                          Holding code
     */
    public int getCode() {
        return code;
    }

    /**
     * Get the holding from the holding code
     *
     * @param   code                    Holding code
     * @return                          Holding
     */
    public static LedgerHolding fromCode(int code) {
        LedgerHolding holding = holdingMap.get(code);
        return (holding != null ? holding : LedgerHolding.UNKNOWN);
    }
}