package in.ashokit.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.ashokit.model.Employee;
import in.ashokit.model.EmployeeTaxResponse;
import in.ashokit.repo.EmployeeRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Optional<EmployeeTaxResponse> calculateTaxDeductions(String employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);

        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            double monthlySalary = employee.getSalary();
            LocalDate doj = LocalDate.parse(employee.getDoj());
            LocalDate financialYearStart = LocalDate.of(LocalDate.now().getYear(), 4, 1);
            LocalDate currentDate = LocalDate.now();

            // Determine months worked in the financial year
            long monthsWorked = doj.isAfter(financialYearStart) 
                    ? ChronoUnit.MONTHS.between(doj.withDayOfMonth(1), currentDate.withDayOfMonth(1)) + 1 
                    : ChronoUnit.MONTHS.between(financialYearStart.withDayOfMonth(1), currentDate.withDayOfMonth(1)) + 1;

            // Calculate yearly salary
            double yearlySalary = monthlySalary * monthsWorked;

            // Calculate tax and cess based on slabs
            double taxAmount = calculateTax(yearlySalary);
            double cessAmount = (yearlySalary > 2500000) ? (yearlySalary - 2500000) * 0.02 : 0;

            // Prepare response
            return Optional.of(new EmployeeTaxResponse(
                    employeeId,
                    employee.getFirstName(),
                    employee.getLastName(),
                    yearlySalary,
                    taxAmount,
                    cessAmount
            ));
        }
        return Optional.empty();
    }

    private double calculateTax(double yearlySalary) {
        double taxAmount = 0;

        if (yearlySalary > 250000 && yearlySalary <= 500000) {
            taxAmount = (yearlySalary - 250000) * 0.05;
        } else if (yearlySalary > 500000 && yearlySalary <= 1000000) {
            taxAmount = 250000 * 0.05 + (yearlySalary - 500000) * 0.1;
        } else if (yearlySalary > 1000000) {
            taxAmount = 250000 * 0.05 + 500000 * 0.1 + (yearlySalary - 1000000) * 0.2;
        }

        return taxAmount;
    }
}
