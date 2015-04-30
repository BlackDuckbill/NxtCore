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
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Routines for processing JSON values
 */
public class JSONValue {

    /**
     * Encode a JSON value
     *
     * @param   value                           JSON value
     * @param   sb                              String builder
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    @SuppressWarnings("unchecked")
    public static void encodeValue(Object value, StringBuilder sb)
                                    throws CharConversionException, UnsupportedEncodingException {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            sb.append('\"');
            escapeString((String)value, sb);
            sb.append('\"');
        } else if (value instanceof Double) {
            if (((Double)value).isInfinite() || ((Double)value).isNaN())
                sb.append("null");
            else
                sb.append(value.toString());
        } else if (value instanceof Float) {
            if (((Float)value).isInfinite() || ((Float)value).isNaN())
                sb.append("null");
            else
                sb.append(value.toString());
        } else if (value instanceof Number) {
            sb.append(value.toString());
        } else if (value instanceof Boolean) {
            sb.append(value.toString());
        } else if (value instanceof JSONAware) {
            ((JSONAware)value).toJSONString(sb);
        } else if (value instanceof Map) {
            JSONObject.toJSONString((Map<String, Object>)value, sb);
        } else if (value instanceof List) {
            JSONArray.toJSONString((List<Object>)value, sb);
        } else {
            throw new UnsupportedEncodingException("Unsupported JSON data type");
        }
    }

    /**
     * Escape control characters in a string and append them to the string buffer
     *
     * @param   string                      String to be written
     * @param   sb                          String builder
     * @throws  CharConversionException     Invalid Unicode character
     */
    private static void escapeString(String string, StringBuilder sb)
                                        throws CharConversionException {
        for (int i=0; i<string.length(); i++) {
            //
            // Check for a valid Unicode codepoint
            //
            int ch = string.codePointAt(i);
            if (!Character.isValidCodePoint(ch))
                throw new CharConversionException("Invalid Unicode character in JSON string value");
            //
            // Process a supplementary codepoint
            //
            if (Character.isSupplementaryCodePoint(ch)) {
                sb.appendCodePoint(ch);
                i++;
                continue;
            }
            //
            // Escape control characters
            //
            char c = string.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    if((c>='\u0000' && c<='\u001F') || (c>='\u007F' && c<='\u009F') || (c>='\u2000' && c<='\u20FF')){
                        String ss = Integer.toHexString(c);
                        sb.append("\\u");
                        for (int k=0; k<4-ss.length(); k++)
                            sb.append('0');
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(c);
                    }
            }
        }
    }
}
