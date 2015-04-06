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

/**
 * AccountBalance is the response for the 'getBalance' API request
 */
public class AccountBalance {

    /** Account identifier */
    private final long accountId;

    /** Balance */
    private final long balance;

    /** Effective balance */
    private final long effectiveBalance;

    /** Unconfirmed balance */
    private final long unconfirmedBalance;

    /** Guaranteed balance */
    private final long guaranteedBalance;

    /** Forged balance */
    private final long forgedBalance;

    /**
     * Create the account balance from the JSON response for 'getBalance'
     *
     * @param       accountId               Account identifier
     * @param       response                Response for getAccount request
     * @throws      NumberFormatException   Invalid numeric string
     */
    public AccountBalance(long accountId, PeerResponse response) throws NumberFormatException {
        this.accountId = accountId;
        balance = response.getLongString("balanceNQT");
        effectiveBalance = response.getLong("effectiveBalanceNXT") * Nxt.NQT_ADJUST;
        unconfirmedBalance = response.getLongString("unconfirmedBalanceNQT");
        guaranteedBalance = response.getLongString("guaranteedBalanceNQT");
        forgedBalance = response.getLongString("forgedBalanceNQT");
    }

    /**
     * Return the account identifier
     *
     * @return                      Account identifier
     */
    public long getAccountId() {
        return accountId;
    }

    /**
     * Return the account Reed-Solomon identifier
     *
     * @return                      Account Reed-Solomon identifier
     */
    public String getAccountRsId() {
        return Utils.getAccountRsId(accountId);
    }

    /**
     * Return the confirmed account balance
     *
     * @return                      Account balance
     */
    public long getConfirmedBalance() {
        return balance;
    }

    /**
     * Return the effective account balance used for forging
     *
     * @return                      Effective account balance
     */
    public long getEffectiveBalance() {
        return effectiveBalance;
    }

    /**
     * Return the total account balance (includes unconfirmed transactions)
     *
     * @return                      Unconfirmed account balance
     */
    public long getBalance() {
        return unconfirmedBalance;
    }

    /**
     * Return the guaranteed account balance
     *
     * @return                      Guaranteed account balance
     */
    public long getGuaranteedBalance() {
        return guaranteedBalance;
    }

    /**
     * Return the forged balance.  The forged balance is included in the account
     * balance but there are no transactions representing the forged block payments.
     *
     * @return                      Forged balanced
     */
    public long getForgedBalance() {
        return forgedBalance;
    }
}
