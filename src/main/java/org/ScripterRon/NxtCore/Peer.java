/*
 * Copyright 2014-2015 Ronald Hoffman.
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
 * Peer is the response for the 'getPeer' request
 */
public class Peer {

    /** Peer states */
    public static enum State {
        NON_CONNECTED,                      // Never connected
        CONNECTED,                          // Currently connected
        DISCONNECTED                        // Currently disconnected
    }

    /** Announced address */
    private final String announcedAddress;

    /** Application */
    private final String application;

    /** Peer is blacklisted */
    private boolean blacklisted;

    /** Downloaded volume */
    private final long downloadedVolume;

    /** Hallmark */
    private final byte[] hallmark;

    /** Time last updated */
    private final int lastUpdated;

    /** Network address */
    private final String networkAddress;

    /** Platform */
    private final String platform;

    /** Share address */
    private final boolean shareAddress;

    /** Peer state */
    private State state;

    /** Uploaded volume */
    private final long uploadedVolume;

    /** Version */
    private final String version;

    /** Weight */
    private final long weight;

    /**
     * Create a peer
     *
     * @param       networkAddress          Peer network address
     * @param       response                Response for 'getPeer' request
     * @throws      NumberFormatException   Invalid numeric string
     */
    public Peer(String networkAddress, PeerResponse response) throws NumberFormatException {
        this.networkAddress = networkAddress;
        announcedAddress = response.getString("announcedAddress");
        application = response.getString("application");
        blacklisted = response.getBoolean("blacklisted");
        downloadedVolume = response.getLong("downloadedVolume");
        hallmark = response.getHexString("hallmark");
        lastUpdated = response.getInt("lastUpdated");
        platform = response.getString("platform");
        shareAddress = response.getBoolean("shareAddress");
        uploadedVolume = response.getLong("uploadedVolume");
        version = response.getString("version");
        weight = response.getLong("weight");
        switch (response.getInt("state")) {
            case 1:
                state = State.CONNECTED;
                break;
            case 2:
                state = State.DISCONNECTED;
                break;
            default:
                state = State.NON_CONNECTED;
        }
    }

    /**
     * Return the announced address
     *
     * @return                              Announced address (Empty string if no announced address)
     */
    public String getAnnouncedAddress() {
        return announcedAddress;
    }

    /**
     * Return the application name
     *
     * @return                              Application name
     */

    public String getApplication() {
        return application;
    }

    /**
     * Return the downloaded volume
     *
     * @return                              Downloaded volume in bytes
     */
    public long getDownloadedVolume() {
        return downloadedVolume;
    }

    /**
     * Return the hallmark
     *
     * @return                              Hallmark (null if the peer is not hallmarked)
     */
    public byte[] getHallmark() {
        return hallmark;
    }

    /**
     * Return the time the peer was last updated
     *
     * @return                              Updated time in seconds since Jan 1, 1970
     */
    public long getLastUpdated() {
        return lastUpdated + Nxt.GENESIS_TIMESTAMP;
    }

    /**
     * Return the network address
     *
     * @return                              Network address
     */
    public String getNetworkAddress() {
        return networkAddress;
    }

    /**
     * Return the platform
     *
     * @return                              Platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Return the uploaded volume
     *
     * @return                              Uploaded volume in bytes
     */
    public long getUploadedVolume() {
        return uploadedVolume;
    }

    /**
     * Return the peer state
     *
     * @return                              Peer state
     */
    public State getState() {
        return state;
    }

    /**
     * Set the peer state
     *
     * @param       state                   New peer state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Return the application version
     *
     * @return                              Application version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Return the weight
     *
     * @return                              Weight (0 if the peer is not hallmarked)
     */
    public long getWeight() {
        return weight;
    }

    /**
     * Check if address is shared
     *
     * @return                              TRUE if address is shared
     */
    public boolean isAddressShared() {
        return shareAddress;
    }

    /**
     * Check if peer is blacklisted
     *
     * @return                              TRUE if peer is blacklisted
     */
    public boolean isBlacklisted() {
        return blacklisted;
    }

    /**
     * Set blacklist status
     *
     * @param   blacklisted                 TRUE if peer is blacklisted
     */
    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    /**
     * Return the peer hash code
     *
     * @return                              Hash code
     */
    @Override
    public int hashCode() {
        return networkAddress.hashCode();
    }

    /**
     * Compare two peers and return TRUE if they have the same network address
     *
     * @param       obj                     Peer to compare
     * @return                              TRUE if the peer is equal to this peer
     */
    @Override
    public boolean equals(Object obj) {
        return (obj!=null && (obj instanceof Peer) && networkAddress.equals(((Peer)obj).networkAddress));
    }
}
