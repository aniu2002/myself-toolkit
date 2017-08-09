/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ftpserver.impl;

import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.ConnectionConfigFactory;


/**
 * <strong>Internal class, do not use directly.</strong>
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a> *
 */
public class DefaultConnectionConfig implements ConnectionConfig {

    private int maxLogins = 10;

    private boolean anonymousLoginEnabled = true;

    private int maxAnonymousLogins = 10;

    private int maxLoginFailures = 3;

    private int loginFailureDelay = 500;

    /**
     * Internal constructor, do not use directly. Use {@link ConnectionConfigFactory} instead
     */
    public DefaultConnectionConfig(boolean anonymousLoginEnabled,
            int loginFailureDelay, int maxLogins, int maxAnonymousLogins,
            int maxLoginFailures) {
        this.anonymousLoginEnabled = anonymousLoginEnabled;
        this.loginFailureDelay = loginFailureDelay;
        this.maxLogins = maxLogins;
        this.maxAnonymousLogins = maxAnonymousLogins;
        this.maxLoginFailures = maxLoginFailures;
    }

    public int getLoginFailureDelay() {
        return loginFailureDelay;
    }

    public int getMaxAnonymousLogins() {
        return maxAnonymousLogins;
    }

    public int getMaxLoginFailures() {
        return maxLoginFailures;
    }

    public int getMaxLogins() {
        return maxLogins;
    }

    public boolean isAnonymousLoginEnabled() {
        return anonymousLoginEnabled;
    }

	@Override
	public void setMaxLogins(int maxUser) {
		this.maxLogins = maxUser;
	}

}
