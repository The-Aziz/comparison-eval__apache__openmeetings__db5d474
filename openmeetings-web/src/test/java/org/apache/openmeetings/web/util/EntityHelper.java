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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.User;

public class EntityHelper {
	public static final int ONE_HOUR = 60 * 60 * 1000;

	public static Appointment getAppointment(User owner, Room r, Date start, Date end) {
		// add new appointment
		Appointment ap = new Appointment();

		ap.setTitle("appointmentName");
		ap.setLocation("appointmentLocation");

		ap.setStart(start);
		ap.setEnd(end);
		ap.setDescription("appointmentDescription");
		ap.setInserted(new Date());
		ap.setDeleted(false);
		ap.setIsDaily(false);
		ap.setIsWeekly(false);
		ap.setIsMonthly(false);
		ap.setIsYearly(false);
		ap.setPasswordProtected(false);

		ap.setOwner(owner);
		ap.setConnectedEvent(false);

		if (ap.getReminder() == null) {
			ap.setReminder(Appointment.Reminder.NONE);
		}

		if (r == null) {
			r = new Room();
			r.setType(Room.Type.CONFERENCE);
			r.setAppointment(true);
		}
		ap.setRoom(r);
		return ap;
	}

	public static Appointment createAppointment(AppointmentDao appointmentDao, Appointment ap) {
		assertNotNull(appointmentDao, "Can't access to appointment dao implimentation");
		// add new appointment
		ap = appointmentDao.update(ap, null, false);
		assertNotNull(ap.getId(), "Can't add appointment");
		return ap;
	}

	public static String getLogin(String uid) {
		return "login" + uid;
	}

	public static String getEmail(String uid) {
		return String.format("email%s@local", uid);
	}

	public static String createPass() {
		return "pass1_!@#$%_A";
	}

	public static User getUser(String uuid) throws Exception {
		User u = new User();
		// add user
		u.setFirstname("firstname" + uuid);
		u.setLastname("lastname" + uuid);
		u.setLogin(getLogin(uuid));
		u.setAddress(new Address());
		u.getAddress().setEmail(getEmail(uuid));
		u.setRights(UserDao.getDefaultRights());
		u.setTimeZoneId("Asia/Bangkok");
		u.updatePassword(createPass());
		u.setLanguageId(1L);
		return u;
	}

	public static User createUser(UserDao userDao, User u) {
		u = userDao.update(u, null);
		assertNotNull(u, "Can't add user");
		return u;
	}

	public static User getContact(UserDao userDao, String uuid, Long ownerId) {
		return userDao.getContact("email" + uuid, "firstname" + uuid, "lastname" + uuid, ownerId);
	}

	public static User createUserContact(UserDao userDao, User user, Long ownerId) {
		user = userDao.update(user, ownerId);
		assertNotNull(user, "Cann't add user");
		return user;
	}
}
