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

import java.io.CharConversionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * JSON object container (extends HashMap)
 *
 * @param   <K>                                 Map entry key
 * @param   <V>                                 Map entry value
 */
public class JSONObject<K, V> extends HashMap<K, V> implements JSONAware {

    /**
     * Create an object container
     */
    public JSONObject() {
        super();
    }

    /**
     * Create an object container with an inital capacity
     *
     * @param   initCapacity                    Initial capacity
     */
    public JSONObject(int initCapacity) {
        super(initCapacity);
    }

    /**
     * Create a formatted string from a JSON object
     *
     * @return                                  JSON string
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    @Override
    public String toJSONString() throws CharConversionException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(512);
        toJSONString(this, sb);
        return sb.toString();
    }

    /**
     * Create a formatted string from a JSON object
     *
     * @param   sb                              String builder
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    @Override
    public void toJSONString(StringBuilder sb) throws CharConversionException, UnsupportedEncodingException {
        toJSONString(this, sb);
    }

    /**
     * Write a JSON-formatted string to the supplied writer
     *
     * @param   writer                          Output writer
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  IOException                     I/O error occurred
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    @Override
    public void writeJSONString(Writer writer)
                                throws CharConversionException, UnsupportedEncodingException, IOException {
        writer.write(toJSONString());
    }

    /**
     * Create a formatted string from a map
     *
     * @param   map                             Map
     * @param   sb                              String builder
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    @SuppressWarnings("unchecked")
    public static void toJSONString(Map<? extends Object, ? extends Object> map, StringBuilder sb)
                                    throws CharConversionException, UnsupportedEncodingException {
        if (map == null) {
            sb.append("null");
            return;
        }
        Set<Map.Entry<Object, Object>> entries;
        Iterator<Map.Entry<Object, Object>> it;
        Map.Entry<Object, Object> entry;
        entries = (Set)map.entrySet();
        it = entries.iterator();
        boolean firstElement = true;
        sb.append('{');
        while (it.hasNext()) {
            entry = it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (!(key instanceof String))
                throw new UnsupportedEncodingException("JSON map key is not a string");
            if (firstElement)
                firstElement = false;
            else
                sb.append(',');
            sb.append('\"').append((String)key).append("\":");
            JSONValue.encodeValue(value, sb);
        }
        sb.append('}');
    }
}
