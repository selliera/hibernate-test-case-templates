package org.hibernate.bugs;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.test.Department;
import org.hibernate.test.Employee;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM,
 * using the Java Persistence API.
 */
public class JPAUnitTestCase {

	static Logger logger = LoggerFactory.getLogger(ORMUnitTestCase.class);
	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		{
			entityManager.getTransaction().begin();
			Department department = new Department();
			department.setDepartmentId(1);
			entityManager.persist(department);
			entityManager.getTransaction().commit();
		}
		{
			entityManager.getTransaction().begin();
			Department dept = entityManager.find(Department.class, 1);
			Employee emp1 = new Employee();
			emp1.setEmployeeId(1);
			emp1.setName("empl1");
			dept.addEmployee(emp1);
			entityManager.merge(dept);
			entityManager.getTransaction().commit();
		}
		{
			entityManager.getTransaction().begin();
			Department loadDept = entityManager.find(Department.class, 1);
			loadDept.getEmployees().forEach(e -> {
				logger.info(e.getRank() + "-" + e.getName());
			});
			Employee emp = entityManager.find(Employee.class, 1);
			int generatedRank = emp.getRank();
			assertEquals("rank start at 0", 0, generatedRank);
			entityManager.getTransaction().commit();
		}
		entityManager.close();
	}
}
