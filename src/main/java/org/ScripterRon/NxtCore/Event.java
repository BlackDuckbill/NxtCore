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
 * Event represents a server event returned by the 'eventWait' API function
 */
public class Event {

    /** Event name */
    private final String name;

    /** Event object identifiers */
    private final List<String> ids;

    /**
     * Create a server event
     *
     * @param   response            Server response
     */
    public Event(PeerResponse response) {
        name = response.getString("name");
        ids = response.getStringList("ids");
    }

    /**
     * Return the event name
     *
     * @return                      Event name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the event object identifiers
     *
     * @return                      Object identifier list
     */
    public List<String> getIds() {
        return ids;
    }
}
