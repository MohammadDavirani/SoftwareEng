package ap.exercises.finalproject_ap;

public class LibraryHeadmaster extends PersonParent {

    public LibraryHeadmaster(String name, String userId, String username, String password) {
        super(name, userId, username, password);
    }

    public boolean headmasterLogin(String username, String password) {
        return (super.getUsername().equals(username) && super.checkPassword(password));
    }
}
