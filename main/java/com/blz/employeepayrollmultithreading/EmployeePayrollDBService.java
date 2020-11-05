package com.blz.employeepayrollmultithreading;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EmployeePayrollDBService {
	private static EmployeePayrollDBService employeePayrollDBService;
	private static Logger log = Logger.getLogger(EmployeePayrollDBService.class.getName());
	private int connectionCounter = 0;

	// creating the object of Signature and getting instance
	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}

	public synchronized Connection getConnection() throws SQLException {
		connectionCounter++;
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service";
		String userName = "root";
		String password = "Sourabhharale@143";
		Connection connection = null;
		// the DriverManager class will attempt to load available JDBC drivers
		log.info("Processing Thread : " + Thread.currentThread().getName() + "Connecting to database : " + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		//which thread is actually going to execute my connection
		log.info("Processing Thread : " + Thread.currentThread().getName() + " ID : " + connectionCounter
				+ " Connection is successful! " + connection);
		return connection;
	}

	public List<EmployeePayrollData> readData() {
		String sql = "SELECT * FROM employee_payroll;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			// A SQL statement is precompiled and stored in a PreparedStatement object. 
			//This object can then be used to efficiently execute this statement multiple times. 
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				int emp_id = resultSet.getInt("emp_id");
				String name = resultSet.getString("name");
				String gender = resultSet.getString("Gender");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(emp_id, name, gender, salary, startDate));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	public EmployeePayrollData addEmployeeToPayrollDB(int emp_id, String name, String gender, double salary,
			LocalDate startDate) {
		int employeeId = -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format(
				"INSERT INTO employee_payroll (name,gender,salary,start) VALUES ('%s','%s','%s','%s')", name, gender,
				salary, Date.valueOf(startDate));
		try (Connection connection = this.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			//generated keys should be made available for retrieval
			int rowAffected = preparedStatement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(employeeId, name, gender, salary, startDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeePayrollData;
	}
}
