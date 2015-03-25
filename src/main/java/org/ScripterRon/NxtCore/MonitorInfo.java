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
 * MonitorInfo contains information for a server monitor
 */
public class MonitorInfo {

    /** Monitor name */
    private final String name;

    /** Monitor hash identity */
    private final int hash;

    /** Stack depth where monitor was locked */
    private final int depth;

    /** Stack frame where monitor was locked */
    private final String trace;

    /**
     * Create the MonitorInfo from the server response
     *
     * @param       response        Server response
     */
    public MonitorInfo(PeerResponse response) {
        name = response.getString("name");
        hash = response.getInt("hash");
        depth = response.getInt("depth");
        trace = response.getString("trace");
    }

    /**
     * Return the monitor name
     *
     * @return                      Monitor name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the monitor hash identity
     *
     * @return                      Monitor hash identity
     */
    public int getHashIdentity() {
        return hash;
    }

    /**
     * Return the stack depth where the monitor was locked
     *
     * @return                      Stack depth
     */
    public int getStackDepth() {
        return depth;
    }

    /**
     * Return the stack element where the monitor was locked
     *
     * @return                      Stack element
     */
    public String getStackElement() {
        return trace;
    }
}
