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
 * StackTraces is created from the response for the GetStackTraces API
 */
public class StackTraces {

    /** Thread information */
    private final List<ThreadInfo> threadInfo;

    /** Blocking locks */
    private final List<LockInfo> blockingLocks;

    /**
     * Create the StackTraces from the server response
     *
     * @param       response        Server response for GetStackTraces
     */
    public StackTraces(PeerResponse response) {
        //
        // Get the thread information
        //
        List<Map<String, Object>> threads = response.getObjectList("threads");
        threadInfo = new ArrayList<>(Math.max(threads.size(), 1));
        threads.stream().forEach((thread) -> threadInfo.add(new ThreadInfo(new PeerResponse(thread))));
        //
        // Get the lock information
        //
        List<Map<String, Object>> locks = response.getObjectList("locks");
        blockingLocks = new ArrayList<>(Math.max(locks.size(), 1));
        locks.stream().forEach((lock) -> blockingLocks.add(new LockInfo(new PeerResponse(lock))));
    }

    /**
     * Return the thread information
     *
     * @return                      Thread information
     */
    public List<ThreadInfo> getThreadInfo() {
        return threadInfo;
    }

    /**
     * Return the blocking locks
     *
     * @return                      Blocking locks
     */
    public List<LockInfo> getBlockingLocks() {
        return blockingLocks;
    }
}
