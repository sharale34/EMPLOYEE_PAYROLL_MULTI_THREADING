package com.blz.employeepayrollmultithreading;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class EmployeePayrollService {
	private static Logger log = Logger.getLogger(EmployeePayrollService.class.getName());

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO;
	}

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readData();
		return this.employeePayrollList;
	}

	public void addEmployeeToPayroll(List<EmployeePayrollData> employeePayrollList) {
		employeePayrollList.forEach(employeePayrollData -> {
			log.info("Employee being added : " + employeePayrollData.name);
			try {
				this.addEmployeeToPayrollDB(employeePayrollData.emp_id, employeePayrollData.name,
						employeePayrollData.gender, employeePayrollData.salary, employeePayrollData.startDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info("Employee added : " + employeePayrollData.name);
		});
		log.info("" + this.employeePayrollList);
	}

	private void addEmployeeToPayrollDB(int emp_id, String name, String gender, double salary, LocalDate startDate) {
		employeePayrollList
				.add(employeePayrollDBService.addEmployeeToPayrollDB(emp_id, name, gender, salary, startDate));
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollList.size();
		return 0;
	}
}
