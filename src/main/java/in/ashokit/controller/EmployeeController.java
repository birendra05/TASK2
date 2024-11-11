package in.ashokit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.ashokit.model.Employee;

import in.ashokit.service.EmployeeService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    
    @PostMapping
    public ResponseEntity<?> createEmployee(@Valid @RequestBody in.ashokit.model.Employee employee) {
        try {
        	Employee savedEmployee = employeeService.saveEmployee(employee);
            return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{employeeId}/tax-deductions")
    public ResponseEntity<Object> getEmployeeTaxDeductions(@PathVariable String employeeId) {
        return employeeService.calculateTaxDeductions(employeeId)
                .map(taxResponse -> new ResponseEntity<Object>(taxResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND));
    }

}
