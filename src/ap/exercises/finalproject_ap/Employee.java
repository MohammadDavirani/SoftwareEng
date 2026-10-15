package ap.exercises.finalproject_ap;

public class Employee extends PersonParent {

    private static final long serialVersionUID = 1L;

    public Employee(String name, String userId, String username, String password) {
        super(name, userId, username, password);
    }

    @Override
    public String toString() {
        return "Employee Name: " + super.getName() +
                " | Employee ID: " + super.getUserId() +
                " | Username: " + super.getUsername();
    }
}
