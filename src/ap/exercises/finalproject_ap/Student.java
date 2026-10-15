package ap.exercises.finalproject_ap;

public class Student extends PersonParent {

    private static final long serialVersionUID = 1L;

    public Student(String name, String studentId, String username, String password) {
        super(name, studentId, username, password);
    }

    @Override
    public String toString() {
        return "Student Name: " + super.getName() +
                " | Student ID : " + super.getUserId() +
                " | Username: " + super.getUsername();
    }
}
