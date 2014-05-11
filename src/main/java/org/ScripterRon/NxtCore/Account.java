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
 * Account contains the current account state retrieve from the Nxt node
 */
public class Account {

    /** NXT <-> NQT */
    public static final long nqtAdjust = 100000000L;

    /** Account identifier */
    private final String accountId;

    /** Parsed getState response */
    private final JSONObject response;

    /**
     * Create the account
     *
     * @param       accountId       Account identifier
     * @param       response        Response for getAccount request
     */
    public Account(String accountId, JSONObject response) {
        this.accountId = accountId;
        this.response = response;
    }

    /**
     * Return the account identifier
     *
     * @return                      Account identifier
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Return the account name
     *
     * @return                      Account name
     */
    public String getName() {
        String name = (String)response.get("name");
        return (name!=null ? name : "");
    }

    /**
     * Return the account description
     *
     * @return                      Account description
     */
    public String getDescription() {
        String desc = (String)response.get("description");
        return (desc!=null ? desc : "");
    }

    /**
     * Return the account public key
     *
     * @return                      Account public key
     */
    public String getPublicKey() {
        return (String)response.get("publicKey");
    }

    /**
     * Return the confirmed account balance
     *
     * @return                      Account balance
     */
    public long getConfirmedBalance() {
        return Long.parseLong((String)response.get("balanceNQT"));
    }

    /**
     * Return the effective account balance
     *
     * @return                      Effective account balance
     */
    public long getEffectiveBalance() {
        return Long.parseLong((String)response.get("effectiveBalanceNXT")) * nqtAdjust;
    }

    /**
     * Return the total account balance
     *
     * @return                      Unconfirmed account balance
     */
    public long getBalance() {
        return Long.parseLong((String)response.get("unconfirmedBalanceNQT"));
    }

    /**
     * Return the account hash code
     *
     * @return                      Account hash code
     */
    @Override
    public int hashCode() {
        return accountId.hashCode();
    }

    /**
     * Compares two accounts
     *
     * @param       obj             Account to compare
     * @return                      TRUE if the account is equal to this account
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof Account) && accountId.equals(((Account)obj).accountId));
    }
}
