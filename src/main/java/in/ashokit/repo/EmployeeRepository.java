package in.ashokit.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import in.ashokit.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String>{

}
