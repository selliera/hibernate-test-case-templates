/*
 * Copyright 2014 JBoss Inc
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
package org.hibernate.bugs;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.test.Department;
import org.hibernate.test.Employee;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM,
 * using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage
 * of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing
 * your reproducer using this method
 * simplifies the process.
 *
 * What's even better? Fork hibernate-orm itself, add your test case directly to
 * a module's unit tests, then
 * submit it as a PR!
 */
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

	static Logger logger = LoggerFactory.getLogger(ORMUnitTestCase.class);

	// Add your entities here.
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				Department.class,
				Employee.class
		};
	}

	// If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
	@Override
	protected String[] getMappings() {
		return new String[] {
				// "Foo.hbm.xml",
				// "Bar.hbm.xml"
		};
	}

	// If those mappings reside somewhere other than resources/org/hibernate/test,
	// change this.
	@Override
	protected String getBaseForMappings() {
		return "org/hibernate/test/";
	}

	// Add in any settings that are specific to your test. See
	// resources/hibernate.properties for the defaults.
	@Override
	protected void configure(Configuration configuration) {
		super.configure(configuration);

		configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString());
		configuration.setProperty(AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString());
		// configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
	}

	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {

		// First save the department
		Session s = openSession();
		Transaction tx = s.beginTransaction();

		Department department = new Department();
		department.setDepartmentId(1);

		session.save(department);

		tx.commit();
		s.close();

		// Then fetch it from the DB to add an employee

		Session employeeSession = openSession();
		Transaction emplTx = employeeSession.beginTransaction();

		Department savedDept = employeeSession
				.createQuery("from Department", Department.class)
				.list()
				.get(0);

		Employee emp1 = new Employee();
		emp1.setEmployeeId(1);
		emp1.setName("empl1");
		// Employee emp2 = new Employee();
		// emp2.setName("empl2");
		savedDept.addEmployee(emp1);

		session.save(savedDept);

		emplTx.commit();
		employeeSession.close();

		// And last, check the employee from DB

		Session secondSession = openSession();
		Transaction tx2 = secondSession.beginTransaction();
		List<Employee> employees = secondSession.createQuery("from Employee", Employee.class).list();
		employees.forEach(e -> {
			logger.info(e.getRank() + "-" + e.getName());
		});

		tx2.commit();
		secondSession.close();
		int generatedRank = employees.get(0).getRank();
		assertEquals("Rank must start with 0", 0, generatedRank);
	}

}
