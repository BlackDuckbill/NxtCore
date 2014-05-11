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

/**
 * A NxtException is thrown when an error occurs while processing a Nxt
 * API request
 */
public class NxtException extends Exception {

    /** Nxt reason code */
    private int reasonCode;

    /**
     * Creates a new exception with a detail message
     *
     * @param       msg             Detail message
     */
    public NxtException(String msg) {
        super(msg);
    }

    /**
     * Creates a new exception with a detail message and reason code
     *
     * @param       msg             Detail message
     * @param       reasonCode      Reason code
     */
    public NxtException(String msg, int reasonCode) {
        super(msg);
        this.reasonCode = reasonCode;
    }

    /**
     * Creates a new exception with a detail message and cause
     *
     * @param       msg             Detail message
     * @param       t               Caught exception
     */
    public NxtException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * Creates a new exception with a detail message, reason code and cause
     *
     * @param       msg             Detail message
     * @param       reasonCode      Reason code
     * @param       t               Caught exception
     */
    public NxtException(String msg, int reasonCode, Throwable t) {
        super(msg, t);
        this.reasonCode = reasonCode;
    }

    /**
     * Returns the Nxt reason code
     *
     * @return                      Reason code
     */
    public int getReasonCode() {
        return reasonCode;
    }
}
