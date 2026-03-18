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
package org.apache.openmeetings.web;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_CONTEXT_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWicketApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setWicketApplicationName;

import java.util.Date;
import java.util.Random;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.util.EntityHelper;
import org.apache.openmeetings.web.util.OmTestHelper;
import org.apache.tomcat.util.scan.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.fail;

@SpringJUnitWebConfig(locations={"classpath:applicationContext.xml"})
@RegularTest
public abstract class AbstractOmServerTest {
	private static final Logger log = LoggerFactory.getLogger(AbstractOmServerTest.class);
	public static final int ONE_HOUR = EntityHelper.ONE_HOUR;
	public static final String ADMIN_USERNAME = OmTestHelper.ADMIN_USERNAME;
	public static final String REGULAR_USERNAME = OmTestHelper.REGULAR_USERNAME;
	public static final String SOAP_USERNAME = OmTestHelper.SOAP_USERNAME;
	protected static final String GROUP_ADMIN_USERNAME = OmTestHelper.GROUP_ADMIN_USERNAME;
	public static final String USER_PASS = OmTestHelper.USER_PASS;
	public static final String GROUP = OmTestHelper.GROUP;
	public static final String EMAIL = OmTestHelper.EMAIL;
	public static final String UNIT_TEST_ARAB_EXT_TYPE = "النُّجُومُ الخَمْسَةِ";
	public static final Random RND = new Random();

	@Inject
	protected AppointmentDao appointmentDao;
	@Inject
	protected UserDao userDao;
	@Inject
	protected GroupDao groupDao;
	@Inject
	private ImportInitvalues importInitvalues;
	@Inject
	protected ConfigurationDao cfgDao;
	@Inject
	protected Application app;

	@BeforeAll
	public static void init() {
		setOmHome();
		System.setProperty(Constants.SKIP_JARS_PROPERTY, "*");
		LabelDao.initLanguageMap();
		if (LabelDao.getLanguages().isEmpty()) {
			fail("Failed to set languages");
		}
	}

	@BeforeEach
	public void serverSetup(TestInfo testInfo) throws Exception {
		if (app.getName() == null) {
			app.setName(DEFAULT_CONTEXT_NAME);
		}
		if (getWicketApplicationName() == null) {
			setWicketApplicationName(app.getName());
		}
		ensureApplication();
		ensureSchema(userDao, importInitvalues);
		log.info("Test started: {} ---", OmTestHelper.getTestCoordinates(testInfo));
	}

	@AfterEach
	void tearDown(TestInfo testInfo) {
		log.info(" --- test finished: {}", OmTestHelper.getTestCoordinates(testInfo));
	}

	public static void setOmHome() {
		OmTestHelper.setOmHome();
	}

	public static void ensureSchema(UserDao userDao, ImportInitvalues importInitvalues) throws Exception {
		OmTestHelper.ensureSchema(userDao, importInitvalues);
	}

	public static String getTestCoordinates(TestInfo testInfo) {
		return OmTestHelper.getTestCoordinates(testInfo);
	}

	public Appointment getAppointment() {
		Date start = new Date();
		Date end = new Date();
		end.setTime(start.getTime() + ONE_HOUR);
		return getAppointment(start, end);
	}

	public Appointment getAppointment(Date start, Date end) {
		return getAppointment(userDao.get(1L), start, end);
	}

	public Appointment getAppointment(User owner, Date start, Date end) {
		return getAppointment(owner, null, start, end);
	}

	public static Appointment getAppointment(User owner, Room r, Date start, Date end) {
		return EntityHelper.getAppointment(owner, r, start, end);
	}

	public static Appointment createAppointment(AppointmentDao appointmentDao, Appointment ap) {
		return EntityHelper.createAppointment(appointmentDao, ap);
	}

	public Appointment createAppointment(Appointment ap) {
		return createAppointment(appointmentDao, ap);
	}

	public User getUser() throws Exception {
		return getUser(randomUUID().toString());
	}

	protected static String getLogin(String uid) {
		return EntityHelper.getLogin(uid);
	}

	protected static String getEmail(String uid) {
		return EntityHelper.getEmail(uid);
	}

	public static String createPass() {
		return EntityHelper.createPass();
	}

	public static User getUser(String uuid) throws Exception {
		return EntityHelper.getUser(uuid);
	}

	public User createUser() throws Exception {
		return createUser(randomUUID().toString());
	}

	public User createUser(String uuid) throws Exception {
		return createUser(getUser(uuid));
	}

	public static User createUser(UserDao userDao, User u) {
		return EntityHelper.createUser(userDao, u);
	}

	public User createUser(User u) {
		return createUser(userDao, u);
	}

	public User getContact(String uuid, Long ownerId) {
		return EntityHelper.getContact(userDao, uuid, ownerId);
	}

	public User createUserContact(Long ownerId) {
		return createUserContact(getContact(randomUUID().toString(), ownerId), ownerId);
	}

	public User createUserContact(User user, Long ownerId) {
		return EntityHelper.createUserContact(userDao, user, ownerId);
	}
}
