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

import java.util.List;

/**
 * A currency defined by the Nxt Monetary System 
 */
public class Currency {
    
    /** Currency types */
    private enum CurrencyType {
        EXCHANGEABLE(0x01),
        CONTROLLABLE(0x02),
        RESERVABLE(0x04),
        CLAIMABLE(0x08),
        MINTABLE(0x10),
        NON_SHUFFLEABLE(0x20);
        
        private final int code;
        private CurrencyType(int code) {this.code = code;}
        
        public int getCode() {return code;}
    }
    
    /** Currency identifier */
    private final long currencyId;
    
    /** Currency code */
    private final String currencyCode;
    
    /** Currency name */
    private final String name;
    
    /** Currency description */
    private final String description;
    
    /** Currency types */
    private final int types;
    
    /** Mint algorithm */
    private final int algorithm;
    
    /** Currency decimals */
    private final int decimals;
    
    /** Issuer account */
    private final long accountId;
    
    /** Creation height */
    private final int creationHeight;
    
    /** Issuance height */
    private final int issuanceHeight;
    
    /** Minimum difficulty */
    private final int minDifficulty;
    
    /** Maximum difficulty */
    private final int maxDifficulty;
    
    /** Initial supply */
    private final long initialSupply;
    
    /** Current supply */
    private final long currentSupply;
    
    /** Reserve supply */
    private final long reserveSupply;
    
    /** Maximum supply */
    private final long maxSupply;
    
    /** Minimum Nxt reserve per unit */
    private final long minUnitReserve;
    
    /** Current Nxt reserve per unit */
    private final long currentUnitReserve;
    
    /** Number of exchanges */
    private final int exchangeCount;
    
    /** Number of transfers */
    private final int transferCount;
    
    /**
     * Create the currency from the JSON response for 'getCurrency'
     * 
     * @param       response                Response for getAccount request
     * @throws      IdentifierException     Invalid account identifier
     * @throws      NumberFormatException   Invalid numeric string
     */
    public Currency(PeerResponse response) throws IdentifierException, NumberFormatException {
        this.currencyId = response.getId("currency");
        this.currencyCode = response.getString("code");
        this.name = response.getString("name");
        this.description = response.getString("description");
        this.decimals = response.getInt("decimals");
        this.algorithm = response.getInt("algorithm");
        this.accountId = response.getId("account");
        this.creationHeight = response.getInt("creationHeight");
        this.issuanceHeight = response.getInt("issuanceHeight");
        this.minDifficulty = response.getInt("minDifficulty");
        this.maxDifficulty = response.getInt("maxDifficulty");
        this.initialSupply = response.getLongString("initialSupply");
        this.currentSupply = response.getLongString("currentSupply");
        this.reserveSupply = response.getLongString("reserverSupply");
        this.maxSupply = response.getLongString("maxSupply");
        this.minUnitReserve = response.getLongString("minReservePerUnitNQT");
        this.currentUnitReserve = response.getLongString("currentReservePerUnitNQT");
        this.exchangeCount = response.getInt("numberOfExchanges");
        this.transferCount = response.getInt("numberOfTransfers");
        List<String> typeStrings = response.getStringList("types");
        int buildTypes = 0;
        for (CurrencyType type : CurrencyType.values()) {
            if (typeStrings.contains(type.name()))
                buildTypes |= type.getCode();
        }
        this.types = buildTypes;
    }
    
    /**
     * Return the currency identifier
     * 
     * @return                      Currency identifier
     */
    public long getCurrencyId() {
        return currencyId;
    }
    
    /**
     * Return the currency code
     * 
     * @return                      3-5 character currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    /**
     * Return the currency name
     * 
     * @return                      Currency name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Return the currency description
     * 
     * @return                      Currency description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Return the currency issuer account identifier
     * 
     * @return                      Currency issuer account identifier
     */
    public long getAccountId() {
        return accountId;
    }
    
    /**
     * Return the currency issuer account RS identifier
     * 
     * @return                      Currency issuer account RS identifier
     */
    public String getAccountRsId() {
        return Utils.getAccountRsId(accountId);
    }
    
    /**
     * Return the number of decimals places for the currency
     * 
     * @return                      Number of decimal places
     */
    public int getDecimals() {
        return decimals;
    }
    
    /**
     * Return the current minting algorithm
     * 
     * @return                      Minting algorithm
     */
    public int getAlgorithm() {
        return algorithm;
    }

    /**
     * Return the creation height
     * 
     * @return                      Creation height
     */
    public int getCreationHeight() {
        return creationHeight;
    }
    
    /**
     * Return the issuance height
     * 
     * @return                      Issuance height
     */
    public int getIssuanceHeight() {
        return issuanceHeight;
    }
    
    /**
     * Return the minimum difficulty
     * 
     * @return                      Minimum difficulty
     */
    public int getMinDifficulty() {
        return minDifficulty;
    }

    /**
     * Return the maximum difficulty
     * 
     * @return                      Maximum difficulty
     */
    public int getMaxDifficulty() {
        return maxDifficulty;
    }

    /**
     * Return the initial supply
     * 
     * @return                      Initial supply expressed as a whole number with an implied decimal point
     *                              as defined for the currency
     */
    public long getInitialSupply() {
        return initialSupply;
    }
    
    /**
     * Return the current supply
     * 
     * @return                      Current supply expressed as a whole number with an implied decimal point
     *                              as defined for the currency
     */
    public long getCurrentSupply() {
        return currentSupply;
    }
    
    /**
     * Return the reserve supply
     * 
     * @return                      Reserve supply expressed as a whole number with an implied decimal point
     *                              as defined for the currency
     */
    public long getReserveSupply() {
        return reserveSupply;
    }
    
    /**
     * Return the maximum supply
     * 
     * @return                      Maximum supply expressed as a whole number with an implied decimal point
     *                              as defined for the currency
     */
    public long getMaxSupply() {
        return maxSupply;
    }

    /**
     * Return minimum Nxt reserve per currency unit
     * 
     * @return                      Minimum Nxt reserve
     */
    public long getMinUnitReserve() {
        return minUnitReserve;
    }
    
    /**
     * Return current Nxt reserve per currency unit
     * 
     * @return                      Current Nxt reserve
     */
    public long getCurrentUnitReserve() {
        return currentUnitReserve;
    }

    /**
     * Return the currency exchange count
     * 
     * @return                      Exchange count
     */
    public int getExchangeCount() {
        return exchangeCount;
    }
    
    /**
     * Return the currency transfer count
     * 
     * @return                      Transfer count
     */
    public int getTransferCount() {
        return transferCount;
    }
    
    /**
     * Check if the currency is exchangeable
     * 
     * @return                      TRUE if the currency is exchangeable
     */
    public boolean isExchangeable() {
        return (types&CurrencyType.EXCHANGEABLE.getCode())!=0;
    }
    
    /**
     * Check if the currency is controllable
     * 
     * @return                      TRUE if the currency is controllable
     */
    public boolean isControllable() {
        return (types&CurrencyType.CONTROLLABLE.getCode())!=0;
    }
    
    /**
     * Check if the currency is reservable
     * 
     * @return                      TRUE if the currency is reservable
     */
    public boolean isReservable() {
        return (types&CurrencyType.RESERVABLE.getCode())!=0;
    }
    
    /**
     * Check if the currency is claimable
     * 
     * @return                      TRUE if the currency is claimable
     */
    public boolean isClaimable() {
        return (types&CurrencyType.CLAIMABLE.getCode())!=0;
    }
    
    /**
     * Check if the currency is mintable
     * 
     * @return                      TRUE if the currency is mintable
     */
    public boolean isMintable() {
        return (types&CurrencyType.MINTABLE.getCode())!=0;
    }
    
    /**
     * Check if the currency is non-shuffleable
     * 
     * @return                      TRUE if the currency is non-shuffleable
     */
    public boolean isNonShuffleable() {
        return (types&CurrencyType.NON_SHUFFLEABLE.getCode())!=0;
    }    

    /**
     * Return the currency hash code
     *
     * @return                      Currency hash code
     */
    @Override
    public int hashCode() {
        return (int)currencyId;
    }

    /**
     * Compares two currencies
     *
     * @param       obj             Currency to compare
     * @return                      TRUE if the currency is equal to this currency
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof Currency) && currencyId==((Currency)obj).currencyId);
    }    
}
