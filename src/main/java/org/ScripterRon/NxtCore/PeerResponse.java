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

import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;

/**
 * PeerResponse is used for the JSON-encoded responses returned by the Nxt node
 */
public class PeerResponse extends JSONObject {

    /** Empty list */
    private static final List<String> emptyList = Collections.emptyList();

    /**
     * Create the response object
     */
    public PeerResponse() {
        super();
    }

    /**
     * Return a boolean value
     *
     * @param       key                     JSON key
     * @return                              Boolean value (FALSE if key not found)
     */
    public boolean getBoolean(String key) {
        Object value = get(key);
        return (value!=null && (value instanceof Boolean) ? (Boolean)value : false);
    }

    /**
     * Return an integer value
     *
     * @param       key                     JSON key
     * @return                              Integer value (0 if key not found)
     */
    public int getInt(String key) {
        Object value = get(key);
        return (value!=null && (value instanceof Long) ? ((Long)value).intValue() : 0);
    }

    /**
     * Return a long value
     *
     * @param       key                     JSON key
     * @return                              Long value (0 if key not found)
     */
    public long getLong(String key) {
        Object value = get(key);
        return (value!=null && (value instanceof Long) ? (Long)value : 0);
    }

    /**
     * Return a long value encoded as a string
     *
     * @param       key                     JSON key
     * @return                              Long value (0 if key not found)
     */
    public long getLongString(String key) {
        Object value = get(key);
        return (value!=null && (value instanceof String) && ((String)value).length()>0 ?
                Long.parseLong((String)value) : 0);
    }

    /**
     * Return a string value
     *
     * @param       key                     JSON key
     * @return                              String value (empty string if key not found)
     */
    public String getString(String key) {
        Object value = get(key);
        return (value!=null && (value instanceof String) ? (String)value : "");
    }

    /**
     * Return a string list value
     *
     * @param       key                     JSON key
     * @return                              String list (empty list if key not found)
     */
    public List<String> getStringList(String key) {
        Object value = get(key);
        return (value!=null && (value instanceof List) ? (List<String>)value : emptyList);
    }
}
