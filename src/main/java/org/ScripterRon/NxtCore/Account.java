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

/**
 * Account is the response for the 'getAccount' API request
 */
public class Account {
    /** Parsed getState response */
    private final PeerResponse response;

    /** Account identifier */
    private final String accountId;

    /**
     * Create the account
     *
     * @param       response        Response for getAccount request
     */
    public Account(PeerResponse response) {
        this.response = response;
        this.accountId = response.getString("account");
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
     * Return the account Reed-Solomon identifier
     *
     * @return                      Account identifier
     */
    public String getAccountRsId() {
        return response.getString("accountRS");
    }

    /**
     * Return the account name
     *
     * @return                      Account name
     */
    public String getName() {
        return response.getString("name");
    }

    /**
     * Return the account description
     *
     * @return                      Account description
     */
    public String getDescription() {
        return response.getString("description");
    }

    /**
     * Return the account public key
     *
     * @return                      Account public key.  An empty byte array will be
     *                              returned if the public key has not been set.
     */
    public byte[] getPublicKey() {
        return Utils.parseHexString(response.getString("publicKey"));
    }

    /**
     * Return the confirmed account balance
     *
     * @return                      Account balance
     */
    public long getConfirmedBalance() {
        return response.getLongString("balanceNQT");
    }

    /**
     * Return the effective account balance used for forging
     *
     * @return                      Effective account balance
     */
    public long getEffectiveBalance() {
        return response.getLong("effectiveBalanceNXT") * Nxt.nqtAdjust;
    }

    /**
     * Return the total account balance (includes unconfirmed transactions)
     *
     * @return                      Unconfirmed account balance
     */
    public long getBalance() {
        return response.getLongString("unconfirmedBalanceNQT");
    }

    /**
     * Return the forged balance.  The forged balance is included in the account
     * balance but there are no transactions representing the forged block payments.
     *
     * @return                      Forged balanced
     */
    public long getForgedBalance() {
        return response.getLongString("forgedBalanceNQT");
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
