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
 * Generator is a single generator returned for the 'getForging' API request
 */
public class Generator {

    /** Account identifier */
    private final long accountId;

    /** Deadline (seconds) */
    private final long deadline;

    /** Remaining deadline (seconds) */
    private final long remaining;

    /** Hit */
    private final long hit;

    /**
     * Create the generator from the 'getForging' API response
     *
     * @param       response                Generator returned by 'getForging' API request
     * @throws      IdentifierException     Invalid account identifier
     * @throws      NumberFormatException   Invalid numeric value in response
     */
    public Generator(PeerResponse response) throws IdentifierException, NumberFormatException {
        accountId = response.getId("account");
        deadline = response.getLong("deadline");
        remaining = response.getLong("remaining");
        hit = response.getLong("hitTime");
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
     * Return the forging deadline
     *
     * @return                      Deadline in seconds
     */
    public long getDeadline() {
        return deadline;
    }

    /**
     * Return the remaining time for the forging deadline
     *
     * @return                      Remaining time in seconds
     */
    public long getRemaining() {
        return remaining;
    }

    /**
     * Return the forging hit value
     *
     * @return                      Hit value
     */
    public long getHit() {
        return hit;
    }
}
