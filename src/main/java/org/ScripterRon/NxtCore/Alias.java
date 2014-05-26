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
 * Alias is the response for the 'getAlias' and 'getAliases' requests
 */
public class Alias implements Comparable<Alias> {

    /** Alias identifier */
    private long aliasId;

    /** Alias name */
    private final String aliasName;

    /** Alias URI */
    private String aliasUri;

    /** Account identifier */
    private final long accountId;

    /** Account Reed-Solomon identifier */
    private final String accountRsId;

    /** Alias timestamp */
    private int timestamp;

    /**
     * Create an alias
     *
     * @param       aliasName               Alias name
     * @param       aliasUri                Alias URI
     * @param       accountId               Account identifier
     */
    public Alias(String aliasName, String aliasUri, long accountId) {
        this.aliasName = aliasName;
        this.aliasUri = aliasUri;
        this.accountId = accountId;
        this.accountRsId = Utils.getAccountRsId(accountId);
    }

    /**
     * Create an alias from the JSON response
     *
     * @param       response                JSON response
     * @throws      IdentifierException     Invalid object identifier
     */
    public Alias(PeerResponse response) throws IdentifierException {
        this.aliasId = response.getId("alias");
        this.aliasName = response.getString("aliasName");
        this.aliasUri = response.getString("aliasURI");
        this.timestamp = response.getInt("timestamp");
        this.accountId = response.getId("account");
        this.accountRsId = response.getString("accountRS");
    }

    /**
     * Return the alias identifier.  The alias identifier is the transaction identifier for the
     * transaction that first assigned the alias.  It remains the same when the alias is
     * updated by a subsequent transaction.
     *
     * @return                              Alias identifier
     */
    public long getAliasId() {
        return aliasId;
    }

    /**
     * Set the alias identifier
     *
     * @param       aliasId                 Alias identifier
     */
    public void setAliasId(long aliasId) {
        this.aliasId = aliasId;
    }

    /**
     * Return the alias name
     *
     * @return                              Alias name
     */
    public String getAliasName() {
        return aliasName;
    }

    /**
     * Return the alias URI
     *
     * @return                              Alias URI
     */
    public String getAliasUri() {
        return aliasUri;
    }

    /**
     * Set the alias URI
     *
     * @param       aliasUri                Alias URI
     */
    public void setAliasUri(String aliasUri) {
        this.aliasUri = aliasUri;
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
     * Return the account Reed-Solomon identifier
     *
     * @return                              Account RS identifier
     */
    public String getAccountRsId() {
        return accountRsId;
    }

    /**
     * Return the alias timestamp in seconds since the Unix epoch (January 1, 1970)
     *
     * @return                              Seconds since epoch
     */
    public long getTimestamp() {
        return timestamp + Nxt.GENESIS_TIMESTAMP;
    }

    /**
     * Set the alias timestamp in seconds since the Unix epoch
     *
     * @param       timestamp               Alias timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = (int)(timestamp - Nxt.GENESIS_TIMESTAMP);
    }

    /**
     * Return the hash code based on the alias name
     *
     * @return                      Alias hash code
     */
    @Override
    public int hashCode() {
        return aliasName.hashCode();
    }

    /**
     * Compares two aliases for equality based on the alias name
     *
     * @param       obj             Alias to compare
     * @return                      TRUE if the alias is equal to this alias
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof Alias) && aliasName.equals(((Alias)obj).aliasName));
    }

    /**
     * Compares two aliases based on the alias name
     *
     * @param       cmp             Comparison alias
     * @return                      -1 if less than, 0 if equal to and 1 if greater than
     */
    @Override
    public int compareTo(Alias cmp) {
        return aliasName.compareTo(cmp.aliasName);
    }
}
