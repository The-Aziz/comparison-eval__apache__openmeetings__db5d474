/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.web.util;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getCryptClassName;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.apache.openmeetings.util.OmFileHelper;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OmTestHelper {
	private static final Logger log = LoggerFactory.getLogger(OmTestHelper.class);
	private static final String TIME_ZONE = "Europe/Berlin";
	public static final String ADMIN_USERNAME = "admin";
	public static final String REGULAR_USERNAME = "user";
	public static final String SOAP_USERNAME = "soap";
	public static final String GROUP_ADMIN_USERNAME = "groupAdmin";
	public static final String USER_PASS = "Q!w2e3r4t5";
	public static final String GROUP = "smoketest";
	public static final String EMAIL = "junit@openmeetings.apache.org";

	public static void setOmHome() {
		String webappsDir = System.getProperty("om.home", ".");
		OmFileHelper.setOmHome(webappsDir);
		if (!OmFileHelper.getOmHome().exists() || !OmFileHelper.getOmHome().isDirectory()) {
			fail("Invalid directory is specified as OM HOME: " + webappsDir);
		}
	}

	public static String getTestCoordinates(TestInfo testInfo) {
		String meth = testInfo.getTestMethod().map(m -> m.getName()).orElse("method n/a");
		String res = testInfo.getTestClass().map(c -> c.getSimpleName()).orElse("class n/a") + ".";
		if (testInfo.getDisplayName().contains(meth)) {
			res += testInfo.getDisplayName();
		} else {
			res += meth + testInfo.getDisplayName();
		}
		return res;
	}

	public static void ensureSchema(UserDao userDao, ImportInitvalues importInitvalues) throws Exception {
		if (userDao.count() < 1) {
			makeDefaultScheme(importInitvalues);
			log.info("Default scheme created successfully");
		} else {
			log.info("Default scheme already created");
		}
		assertNotNull(getCryptClassName(), "Crypt class name should not be null");
	}

	private static void makeDefaultScheme(ImportInitvalues importInitvalues) throws Exception {
		InstallationConfig cfg = new InstallationConfig();
		cfg.setUsername(ADMIN_USERNAME);
		cfg.setPassword(USER_PASS);
		cfg.setEmail(EMAIL);
		cfg.setGroup(GROUP);
		cfg.setTimeZone(TIME_ZONE);
		importInitvalues.loadAll(cfg, false);
		// regular user
		importInitvalues.createSystemUser(EntityHelper.getUser(randomUUID().toString()), GROUP, REGULAR_USERNAME, USER_PASS, false, null);

		// soap user
		importInitvalues.createSystemUser(EntityHelper.getUser(randomUUID().toString()), GROUP, SOAP_USERNAME, USER_PASS, false, u -> {
			u.getRights().remove(User.Right.ROOM);
			u.getRights().remove(User.Right.DASHBOARD);
			u.getRights().add(User.Right.SOAP);
		});

		// group admin
		importInitvalues.createSystemUser(EntityHelper.getUser(randomUUID().toString()), GROUP, GROUP_ADMIN_USERNAME, USER_PASS, true, null);
	}
}
