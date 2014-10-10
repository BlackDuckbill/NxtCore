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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Account is the response for the 'getAccount' API request
 */
public class Account {

    /** Account identifier */
    private final long accountId;

    /** Reed-Solomon identifier */
    private final String accountRsId;

    /** Name */
    private final String name;

    /** Description */
    private final String description;

    /** Public key */
    private final byte[] publicKey;

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

    /** Asset balances */
    private final List<AssetBalance> assetBalances;

    /** Unconfirmed asset balances */
    private final List<AssetBalance> unconfirmedAssetBalances;

    /**
     * Create the account from the JSON response for 'getAccount'
     *
     * @param       response                Response for getAccount request
     * @throws      IdentifierException     Invalid account identifier
     * @throws      NumberFormatException   Invalid numeric string
     */
    public Account(PeerResponse response) throws IdentifierException, NumberFormatException {
        this.accountId = response.getId("account");
        this.accountRsId = response.getString("accountRS");
        this.name = response.getString("name");
        this.description = response.getString("description");
        this.publicKey = response.getHexString("publicKey");
        this.balance = response.getLongString("balanceNQT");
        this.effectiveBalance = response.getLong("effectiveBalanceNXT") * Nxt.NQT_ADJUST;
        this.unconfirmedBalance = response.getLongString("unconfirmedBalanceNQT");
        this.guaranteedBalance = response.getLongString("guaranteedBalanceNQT");
        this.forgedBalance = response.getLongString("forgedBalanceNQT");
        List<PeerResponse> assetList = response.getObjectList("assetBalances");
        if (assetList.isEmpty()) {
            this.assetBalances = Collections.emptyList();
        } else {
            this.assetBalances = new ArrayList<>(assetList.size());
            for (PeerResponse asset : assetList) {
                long assetId = asset.getId("asset");
                long assetBalance = asset.getLongString("balanceQNT");
                assetBalances.add(new AssetBalance(accountId, assetId, assetBalance));
            }
        }
        assetList = response.getObjectList("unconfirmedAssetBalances");
        if (assetList.isEmpty()) {
            this.unconfirmedAssetBalances = Collections.emptyList();
        } else {
            this.unconfirmedAssetBalances = new ArrayList<>(assetList.size());
            for (PeerResponse asset : assetList) {
                long assetId = asset.getId("asset");
                long assetBalance = asset.getLongString("unconfirmedBalanceQNT");
                unconfirmedAssetBalances.add(new AssetBalance(accountId, assetId, assetBalance));
            }
        }
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
        return accountRsId;
    }

    /**
     * Return the account name
     *
     * @return                      Account name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the account description
     *
     * @return                      Account description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the account public key
     *
     * @return                      Account public key or null if the public key has not been set
     */
    public byte[] getPublicKey() {
        return publicKey;
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
     * Return the forged balance.  The forged balance is included in the account
     * balance but there are no transactions representing the forged block payments.
     *
     * @return                      Forged balanced
     */
    public long getForgedBalance() {
        return forgedBalance;
    }

    /**
     * Return the confirmed asset balances
     *
     * @return                      Asset balance list
     */
    public List<AssetBalance> getConfirmedAssetBalances() {
        return assetBalances;
    }

    /**
     * Return the total asset balances including unconfirmed assets
     *
     * @return                      Asset balance list
     */

    public List<AssetBalance> getAssetBalances() {
        return unconfirmedAssetBalances;
    }

    /**
     * Return the account hash code
     *
     * @return                      Account hash code
     */
    @Override
    public int hashCode() {
        return (int)accountId;
    }

    /**
     * Compares two accounts
     *
     * @param       obj             Account to compare
     * @return                      TRUE if the account is equal to this account
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof Account) && accountId==((Account)obj).accountId);
    }
}
