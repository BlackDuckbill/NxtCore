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
 * LockInfo contains information for a server lock
 */
public class LockInfo {

    /** Lock name */
    private final String name;

    /** Lock hash identity */
    private final int hash;

    /** Identifier of thread holding the lock */
    private final long id;

    /**
     * Create the LockInfo from the server response
     *
     * @param       response        Server response
     */
    public LockInfo(PeerResponse response) {
        name = response.getString("name");
        hash = response.getInt("hash");
        id = response.getLong("thread");
    }

    /**
     * Return the lock name
     *
     * @return                      Lock name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the lock hash identity
     *
     * @return                      Hash identity
     */
    public int getHashIdentity() {
        return hash;
    }

    /**
     * Return the thread identifier
     *
     * @return                      Identifier of thread holding the lock
     */
    public long getThreadId() {
        return id;
    }
}
