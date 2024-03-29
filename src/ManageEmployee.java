import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ManageEmployee {
	private static SessionFactory factory; 

	public static void main(String[] args) { 
		try{ 
			factory = new Configuration().configure().buildSessionFactory();
		}catch (Throwable ex) { 
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

		ManageEmployee ME = new ManageEmployee(); 

		/* Let us have one address object */ 
		Address address1 = ME.addAddress("Kondapur","Hyderabad","AP","532"); 

		/* Add employee records in the database */ 
		Integer empID1 = ME.addEmployee("Manoj", "Kumar", 4000, address1);

		/* Let us have another address object */ 
		Address address2 = ME.addAddress("Saharanpur","Ambehta","UP","111");

		/* Add another employee record in the database */ 
		Integer empID2 = ME.addEmployee("Dilip", "Kumar", 3000, address2); 

		/* List down all the employees */ 
		ME.listEmployees(); 

		/* Update employee's salary records */ 
		ME.updateEmployee(empID1, 5000);

		/* Delete an employee from the database */
		//		ME.deleteEmployee(empID2);

		/* List down all the employees */
		ME.listEmployees();

		factory.close();
	}

	/* Method to add an address record in the database */
	public Address addAddress(String street, String city, String state, String zipcode) { 
		Session session = factory.openSession(); 
		Transaction tx = null;
		Integer addressID = null;
		Address address = null; 
		try{
			tx = session.beginTransaction();
			address = new Address(street, city, state, zipcode);
			addressID = (Integer) session.save(address); 
			tx.commit(); 
		}catch (HibernateException e) {
			if (tx!=null) 
				tx.rollback(); 
			e.printStackTrace();
		}finally {
			session.close(); 
		}
		return address; 
	} 

	/* Method to add an employee record in the database */ 
	public Integer addEmployee(String fname, String lname, int salary, Address address){
		Session session = factory.openSession();
		Transaction tx = null; 
		Integer employeeID = null;
		try{
			tx = session.beginTransaction(); 
			Employee employee = new Employee(fname, lname, salary, address);
			employeeID = (Integer) session.save(employee);
			tx.commit(); 
		}catch (HibernateException e) {
			if (tx!=null) 
				tx.rollback();
			e.printStackTrace(); 
		}finally { 
			session.close(); 
		}
		return employeeID; 
	}

	/* Method to list all the employees detail */
	@SuppressWarnings("unchecked")
	public void listEmployees( ){ 
		Session session = factory.openSession(); 
		Transaction tx = null;
		try{
			tx = session.beginTransaction(); 
			List<Employee> employees = session.createQuery("FROM Employee").list(); 
			for (Iterator<Employee> iterator = employees.iterator(); iterator.hasNext();){
				Employee employee = (Employee) iterator.next();
				System.out.println("------------------------------------------");
				System.out.print("First Name: " + employee.getFirstName()); 
				System.out.print(", Last Name: " + employee.getLastName()); 
				System.out.println(", Salary: " + employee.getSalary());
				Address addr = employee.getAddress(); 
				System.out.println("Address "); 
				System.out.println("\tStreet: " + addr.getStreet()); 
				System.out.println("\tCity: " + addr.getCity());
				System.out.println("\tState: " + addr.getState()); 
				System.out.println("\tZipcode: " + addr.getZipcode()); 
			} 
			tx.commit(); 
		}catch (HibernateException e) { 
			if (tx!=null) 
				tx.rollback();
			e.printStackTrace(); 
		}finally {
			session.close();
		} 
	}

	/* Method to update salary for an employee */ 
	public void updateEmployee(Integer employeeID, int salary ){
		Session session = factory.openSession(); 
		Transaction tx = null; 
		try{ 
			tx = session.beginTransaction(); 
			Employee employee = (Employee)session.get(Employee.class, employeeID);
			employee.setSalary( salary ); 
			session.update(employee); 
			tx.commit();
		}catch (HibernateException e) {
			if (tx!=null) tx.rollback();
			e.printStackTrace();
		}finally {
			session.close();
		} 
	} 

	/* Method to delete an employee from the records */
	public void deleteEmployee(Integer employeeID){ 
		Session session = factory.openSession();
		Transaction tx = null;
		try{ 
			tx = session.beginTransaction();
			Employee employee = (Employee)session.get(Employee.class, employeeID);
			session.delete(employee); 
			tx.commit(); 
		}catch (HibernateException e) {
			if (tx!=null) 
				tx.rollback(); 
			e.printStackTrace();
		}finally {
			session.close();
		} 
	} 
}