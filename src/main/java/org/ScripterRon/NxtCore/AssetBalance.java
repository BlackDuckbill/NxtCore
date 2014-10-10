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
 * AssetBalance represents an asset associated with an account
 */
public class AssetBalance {

    /** Asset account */
    private final long accountId;

    /** Asset identifier */
    private final long assetId;

    /** Asset balance */
    private long assetBalance;

    /**
     * Create a new asset balance
     *
     * @param       accountId               Account identifier
     * @param       assetId                 Asset identifier
     * @param       assetBalance            Asset balance
     */
    public AssetBalance(long accountId, long assetId, long assetBalance) {
        this.accountId = accountId;
        this.assetId = assetId;
        this.assetBalance = assetBalance;
    }

    /**
     * Return the account identifier
     *
     * @return                              Account identifier
     */
    public long getAccountId() {
        return accountId;
    }

    /**
     * Return the asset identifier
     *
     * @return                              Asset identifier
     */
    public long getAssetId() {
        return assetId;
    }

    /**
     * Return the asset balance
     *
     * @return                              Asset balance
     */
    public long getBalance() {
        return assetBalance;
    }

    /**
     * Set the asset balance
     *
     * @param       balance                 Asset balance
     */
    public void setBalance(long balance) {
        assetBalance = balance;
    }

    /**
     * Return the hash code for this asset balance
     *
     * @return                              Hash code
     */
    @Override
    public int hashCode() {
        return (int)(accountId^assetId);
    }

    /**
     * Compare two AssetBalance objects and return TRUE if the account identifier and asset identifier
     * are the same
     *
     * @return                              TRUE if the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        return (obj!=null && (obj instanceof AssetBalance) && accountId==((AssetBalance)obj).accountId &&
                                assetId==((AssetBalance)obj).assetId);
    }
}
