package com.blz.employeepayrollmultithreading;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public void addEmployeeToPayrollWithThreads(List<EmployeePayrollData> asList) {
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<>();
		employeePayrollList.forEach(employeePayrollData -> {
			// creating task using runnable to execute the thread
			Runnable task = () -> {
				//employee payroll object id is set to false because get is not added
				employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
				log.info("Employee being added : " + Thread.currentThread().getName());
				try {
					this.addEmployeeToPayrollDB(employeePayrollData.emp_id, employeePayrollData.name,
							employeePayrollData.gender, employeePayrollData.salary, employeePayrollData.startDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
				employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
				log.info("Employee added : " + Thread.currentThread().getName());
			};
			//creating a thread and assigning to start the task
			Thread thread = new Thread(task, employeePayrollData.name);
			thread.start();
		});
		// keeping Main thread to wait
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
