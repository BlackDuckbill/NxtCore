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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ThreadInfo contains information for a server thread
 */
public class ThreadInfo {

    /** Thread name */
    private final String name;

    /** Thread identifier */
    private final long id;

    /** Thread state */
    private Thread.State state;

    /** Monitor locks held by this thread */
    private final List<MonitorInfo> monitorLocks;

    /** Blocking lock for this thread */
    private final LockInfo blockingLock;

    /** Stack trace elements */
    private final List<String> stackTrace;

    /**
     * Create the ThreadInfo from the server response
     *
     * @param       response        Server response
     */
    public ThreadInfo(PeerResponse response) {
        //
        // Get general thread information
        //
        name = response.getString("name");
        id = response.getLong("id");
        stackTrace = response.getStringList("trace");
        //
        // Get the thread state
        //
        try {
            state = Thread.State.valueOf(response.getString("state"));
        } catch (IllegalArgumentException exc) {
            state = Thread.State.TERMINATED;
        }
        //
        // Get the list of monitor locks
        //
        List<Map<String, Object>> monitors = response.getObjectList("locks");
        monitorLocks = new ArrayList<>(Math.max(monitors.size(), 1));
        monitors.stream().forEach((monitor) -> monitorLocks.add(new MonitorInfo(new PeerResponse(monitor))));
        //
        // Get the blocking lock for this thread
        //
        PeerResponse blocking = (PeerResponse)response.get("blocked");
        if (blocking != null) {
            blockingLock = new LockInfo(blocking);
        } else {
            blockingLock = null;
        }
    }

    /**
     * Return the thread name
     *
     * @return                      Thread name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the thread identifier
     *
     * @return                      Thread identifier
     */
    public long getId() {
        return id;
    }

    /**
     * Return the thread state
     *
     * @return                      Thread state
     */
    public Thread.State getState() {
        return state;
    }

    /**
     * Return the stack trace elements
     *
     * @return                      List of stack trace elements
     */
    public List<String> getStackTrace() {
        return stackTrace;
    }

    /**
     * Return the locks held by the thread
     *
     * @return                      List of held locks
     */
    public List<MonitorInfo> getLocks() {
        return monitorLocks;
    }

    /**
     * Return the blocking lock for the thread
     *
     * @return                      Blocking lock or null if the thread is not blocked
     */
    public LockInfo getBlockingLock() {
        return blockingLock;
    }
}
