package ap.exercises.finalproject_ap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Employee> employees;

    public EmployeeManager() {
        this.employees = new ArrayList<>();
    }

    public void registerEmployee(String name, String employeeId, String username, String password) {
        if (searchById(employeeId) != null) {
            System.out.println("An employee with this ID already exists.");
            return;
        }
        if (isUsernameTaken(username)) {
            System.out.println("This username already exists. Please choose a different username.");
            return;
        }
        Employee newEmployee = new Employee(name, employeeId, username, password);
        employees.add(newEmployee);
        System.out.println("Employee registration completed successfully.");
    }

    public Employee authenticateEmployee(String username, String password) {
        return employees.stream()
                .filter(e -> e.getUsername().equals(username) && e.checkPassword(password))
                .findFirst()
                .orElse(null);
    }

    public void employeePasswordChanging(Employee employee, String password) {
        if (employee == null || !employees.contains(employee)) {
            System.out.println("Employee not found. Password Changing Failed!");
            return;
        }
        employee.setPassword(password);
        System.out.println("Password Successfully Changed.");
    }

    public Employee searchById(String id) {
        return employees.stream()
                .filter(e -> e.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean isUsernameTaken(String username) {
        return employees.stream()
                .anyMatch(e -> e.getUsername().equals(username));
    }

    public int getEmployeesCount() {
        return employees.size();
    }
}
