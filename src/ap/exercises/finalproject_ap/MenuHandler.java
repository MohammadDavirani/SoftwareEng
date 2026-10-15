package ap.exercises.finalproject_ap;


// MenuHandler.java

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MenuHandler implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String HEADMASTER_NAME = "Jalal";
    private static final String HEADMASTER_ID = "mHg505p";
    private static final String HEADMASTER_USERNAME = "Saba1404";
    private static final String HEADMASTER_PASSWORD = "s14B04";

    private transient Scanner scanner;
    private LibrarySystem librarySystem;
    private Student currentUser;
    private Employee currentEmployee;
    private LibraryHeadmaster headmaster;
    final private int MAX_EMPLOYEES = 3;

    public MenuHandler(LibrarySystem librarySystem) {
        this.scanner = new Scanner(System.in);
        this.librarySystem = librarySystem;
        this.currentUser = null;
        this.headmaster = new LibraryHeadmaster(HEADMASTER_NAME, HEADMASTER_ID, HEADMASTER_USERNAME, HEADMASTER_PASSWORD);
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMainMenu() {
        while (true) {
            System.out.println("\n=== University Library Management System ===");
            System.out.println("1. Student Registration");
            System.out.println("2. Student Login");
            System.out.println("3. Guest User");
            System.out.println("4. Employee Login");
            System.out.println("5. Headmaster Login");
            System.out.println("6. Exit");
            System.out.print("Please enter your choice: ");

            int choice = getIntInput(1, 6);

            try {
                switch (choice) {
                    case 1:
                        handleStudentRegistration();
                        break;
                    case 2:
                        handleStudentLogin();
                        break;
                    case 3:
                        displayGuestMenu();
                        break;
                    case 4:
                        handleEmployeeLogin();
                        break;
                    case 5:
                        handleHeadmasterLogin();
                        break;
                    case 6:
                        librarySystem.saveExit();
                        System.out.println("Exiting system. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e + " Returning to the main menu.");
            }
            System.out.println("___________________________");
        }
    }

    private void handleStudentRegistration() {
        System.out.println("\n--- New Student Registration ---");

        System.out.print("Student name: ");
        String name = scanner.nextLine();

        System.out.print("Student ID: ");
        String studentId = scanner.nextLine();

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        librarySystem.registerStudent(name, studentId, username, password);
    }

    private void handleStudentLogin() {
        System.out.println("\n--- Student Login ---");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = librarySystem.authenticateStudent(username, password);

        if (currentUser != null) {
            System.out.println("Login successful! Welcome, " + currentUser.getName());
            displayLoggedInStudentMenu();
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    private void handleEmployeeLogin() {
        System.out.println("\n--- Agent of Library Login ---");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentEmployee = librarySystem.authenticateEmployee(username, password);
        if (currentEmployee == null) {
            System.out.println("Invalid username or password. Please try again.");
        } else {
            System.out.println("Login successful! Welcome, " + currentEmployee.getName());
            displayLoggedInEmployeeMenu();
        }
    }

    private void handleHeadmasterLogin() {
        System.out.println("\n--- Headmaster of Library Login ---");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (headmaster.headmasterLogin(username, password)) {
            System.out.println("Login successful! Welcome, dear " + headmaster.getName());
            displayHeadmasterMenu();
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    private void displayLoggedInStudentMenu() {
        while (currentUser != null) {
            System.out.println("\n=== Student Dashboard ===");
            System.out.println("1. View My Information");
            System.out.println("2. Edit My Information");
            System.out.println("3. View Available Books");
            System.out.println("4. Search A Book");
            System.out.println("5. Submit A Book Loaning Request");
            System.out.println("6. Logout");
            System.out.print("Please enter your choice: ");

            int choice = getIntInput(1, 6);

            try {
                switch (choice) {
                    case 1:
                        System.out.println("\n--- My Information ---");
                        System.out.println(currentUser);
                        break;
                    case 2:
                        handleStudentInfoEditing();
                        break;
                    case 3:
                        librarySystem.displayAvailableBooks();
                        break;
                    case 4:
                        handleBookSearching();
                        break;
                    case 5:
                        handleLoanRequest();
                        break;
                    case 6:
                        currentUser = null;
                        System.out.println("Logged out successfully.");
                        return;
                    default:
                        System.out.println("Invalid option! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e + " Returning to the student menu.");
            }
        }
    }

    private void handleStudentInfoEditing() {
        System.out.println("\n--- Student Information Editing ---");
        System.out.println("Please fill in the fields below:");

        System.out.print("New Username: ");
        String newUsername = scanner.nextLine();

        System.out.print("Password: ");
        String newPassword = scanner.nextLine();

        librarySystem.editStudentInformation(currentUser, newUsername, newPassword);
    }

    private void handleBookSearching() {
        System.out.println("\n=== Searching a Book ===");
        System.out.println("Please fill in the fields below: (you can pass a filed by Enter key if it's not necessary)");

        System.out.print("Book Title:");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            title = null;
        }

        System.out.print("Author:");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            author = null;
        }

        System.out.print("Publish Year:");
        String publishYear = scanner.nextLine().trim();
        if (publishYear.isEmpty()) {
            publishYear = null;
        }

        if (title == null && author == null && publishYear == null) {
            System.out.println("No information declared. Searching Failed!");
        } else {
            librarySystem.searchBook(title, author, publishYear);
        }

    }

    private void handleLoanRequest() {
        System.out.println("\n--- Submitting Loan Request ---");
        if (librarySystem.isBlocked(currentUser)) {
            System.out.println("Sorry, You are Blocked by library agents.");
            return;
        }
        System.out.println("Please fill in the fields below: (To Find a Book and it's Id, Search Before.)");

        System.out.print("The BookId: ");
        String bookId = scanner.nextLine().trim();

        System.out.print("Loan Start Date: (Year-Mon-Day)");
        LocalDate startDate = parseDateInput(scanner.nextLine());
        if (startDate == null) {
            System.out.println("Loan request cancelled.");
            return;
        }

        System.out.print("Loan End Date: (Year-Mon-Day)");
        LocalDate endDate = parseDateInput(scanner.nextLine());
        if (endDate == null) {
            System.out.println("Loan request cancelled.");
            return;
        }

        LocalDate today = LocalDate.now();
        if (!startDate.isBefore(today) && endDate.isAfter(startDate)) {
            librarySystem.loanBookRequest(bookId, currentUser, startDate, endDate);
        } else {
            System.out.println("Entered dates are not valid!");
        }
    }

    private LocalDate parseDateInput(String input) {
        try {
            return LocalDate.parse(input.trim());
        } catch (Exception e) {
            System.out.println("Invalid date input. Expected format: Year-Mon-Day (e.g. 2026-10-16).");
            return null;
        }
    }

    private void displayGuestMenu() {
        while (true) {
            System.out.println("\n--- Guest User Menu ---");
            System.out.println("1. View Registered Student Count");
            System.out.println("2. Search By Book's Title");
            System.out.println("3. Show Library Statistics ");
            System.out.println("4. Exit And Return to Previous Page");
            System.out.print("Please enter your choice: ");


            int choice = getIntInput(1, 4);

            try {
                switch (choice) {
                    case 1:
                        displayStudentCount();
                        break;
                    case 2:
                        handleBookSearchingByTitle();
                        break;
                    case 3:
                        displayLibraryStatistics();
                        break;
                    case 4:
                        System.out.println("Returning to Previous Page");
                        return;
                    default:
                        System.out.println("Invalid option! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e + " Returning to the guest menu.");
            }
        }
    }

    private void displayStudentCount() {
        int studentCount = librarySystem.getStudentCount();
        System.out.println("\nTotal registered students: " + studentCount);
    }

    private void handleBookSearchingByTitle() {
        System.out.println("\n--- Searching By Title ---");

        System.out.print("Book Title: ");
        String title = scanner.nextLine();

        librarySystem.searchBookByTitleForGuest(title);
    }

    private void displayLibraryStatistics() {
        System.out.println("\n--- Library General Statistics ---");

        System.out.print("Registered Students Count: ");
        System.out.println(librarySystem.getStudentCount());

        System.out.print("Library All Books Count: ");
        System.out.println(librarySystem.getBookCount());

        System.out.print("Book Loaning Histories Count: ");
        System.out.println(librarySystem.getLoanHistoryCount());

        System.out.print("On Loan Books Count: ");
        System.out.println(librarySystem.onLoanBookCount());

    }

    private void displayLoggedInEmployeeMenu() {
        while (true) {
            System.out.println("\n--- Agent of Library Menu ---");
            System.out.println("1. Change Password");
            System.out.println("2. Add Book");
            System.out.println("3. Edit Book Information");
            System.out.println("4. Check and Accept Recent Requests");
            System.out.println("5. Student Personal Statistics");
            System.out.println("6. Student Accessibility");
            System.out.println("7. Receive Book");
            System.out.println("8. Logout");
            System.out.print("Please enter your choice: ");

            int choice = getIntInput(1, 8);

            try {
                switch (choice) {
                    case 1:
                        handleEmployeePasswordChange();
                        break;
                    case 2:
                        handleAddNewBooK();
                        break;
                    case 3:
                        handleEditingBook();
                        break;
                    case 4:
                        handleAcceptingRequests();
                        break;
                    case 5:
                        handleStudentStatistics();
                        break;
                    case 6:
                        handleStudentAccessibility();
                        break;
                    case 7:
                        handleReceivingBook();
                        break;
                    case 8:
                        currentEmployee = null;
                        System.out.println("Logged out successfully.");
                        return;
                    default:
                        System.out.println("Invalid option! Please try again.");

                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e + " Returning to the employee menu.");
            }
        }
    }

    private void handleEmployeePasswordChange() {
        System.out.println("\n--- Employee Password Changing ---");

        System.out.print("New Password: ");
        String password = scanner.nextLine();

        librarySystem.employeePasswordChanging(currentEmployee, password);
    }

    private void handleAddNewBooK() {
        System.out.println("\n--- Adding Book To Library ---");

        System.out.print("Book Id:");
        String bookId = scanner.nextLine().trim();

        System.out.print("Book Title:");
        String title = scanner.nextLine().trim();

        System.out.print("Author:");
        String author = scanner.nextLine().trim();

        System.out.print("Publish Year:");
        int publishYear = getIntInput(0, 2100);

        librarySystem.addNewBook(currentEmployee, bookId, title, author, publishYear);
    }

    private void handleEditingBook() {
        System.out.println("\n--- Editing Book Information ---");
        System.out.println("Description: First, search the book and keep it's Id-Number");
        System.out.println("Then, Enter the Id-Number and new information.");
        handleBookSearching();
        System.out.println("\n-----------------------------\n");
        System.out.println("Enter new information to be changed. (If a field will not be changed pass Enter key.)");

        System.out.print("Id-Number of Target Book :");
        String targetBookId = scanner.nextLine().trim();
        if (targetBookId.isEmpty()) {
            System.out.println("Changing book information failed!");
            return;
        }

        System.out.print("Book Id:");
        String bookId = scanner.nextLine().trim();
        if (bookId.isEmpty()) {
            bookId = null;
        }

        System.out.print("Book Title:");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            title = null;
        }

        System.out.print("Author:");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            author = null;
        }

        System.out.print("Publish Year:");
        String publishYear = scanner.nextLine().trim();
        if (publishYear.isEmpty()) {
            publishYear = null;
        }

        if (bookId == null && title == null && author == null && publishYear == null) {
            System.out.println("No information declared. Changing Information Failed!");
        } else {
            librarySystem.changeBookInfo(targetBookId, bookId, title, author, publishYear);
        }

    }

    public void handleAcceptingRequests() {
        System.out.println("\n--- Accepting Requests ---");
        while (true) {
            System.out.println("Recent Requests: ");
            List<LoanRequest> recentRequests = librarySystem.listOfRecentRequests();

            if (recentRequests.size() == 0) {
                System.out.println("No recent requests.");
                return;
            }

            for (int i = 0; i < recentRequests.size(); i++) {
                System.out.println((i + 1) + ". " + recentRequests.get(i));
            }

            System.out.print("Please select the request by its number, or enter 0 to exit:");
            int input = getIntInput(0, recentRequests.size());

            if (input == 0) {
                return;
            }
            librarySystem.convertRequestToAcceptedLoan(currentEmployee, recentRequests.get(input - 1));
        }
    }

    public void handleStudentStatistics() {
        System.out.println("\n--- Student Statistics ---");
        System.out.print("Student Id:");
        String id = scanner.nextLine();
        librarySystem.displayStudentStatistics(id);
    }

    public void handleStudentAccessibility() {
        System.out.println("\n--- Student Accessibility ---");
        System.out.println("1. BlocKing");
        System.out.println("2. Unblocking");
        System.out.print("Please enter your choice: ");

        int choice = getIntInput(1, 2);

        System.out.print("Student Id: ");
        String id = scanner.nextLine();

        switch (choice) {
            case 1:
                librarySystem.blockStudent(id);
                break;
            case 2:
                librarySystem.unblockStudent(id);
                break;
            default:
                System.out.println("Invalid Choice.");
        }
    }

    public void handleReceivingBook() {
        System.out.println("-- Receiving Book --");
        System.out.print("Enter the Id written on the book: ");
        String bookId = scanner.nextLine();
        librarySystem.returnLoaningBook(bookId, currentEmployee);
    }

    public void displayHeadmasterMenu() {
        while (true) {
            System.out.println("\n--- Dear " + headmaster.getName() + "'s Menu ---");
            System.out.println("1. Determine an Employee");
            System.out.println("2. Display an Employee's Performance");
            System.out.println("3. Display Statistics for a Book");
            System.out.println("4. Display Statistics for All Students");
            System.out.println("5. Display Ten Students With Most Delay");
            System.out.println("6. Logout");
            System.out.print("Please enter your choice: ");

            int choice = getIntInput(1, 6);

            try {
                switch (choice) {
                    case 1:
                        handleEmployeeAddition();
                        break;
                    case 2:
                        handleEmployeePerformance();
                        break;
                    case 3:
                        handleBookStatistics();
                        break;
                    case 4:
                        handleGeneralStudentsStatistics();
                        break;
                    case 5:
                        librarySystem.displayTenStudentsWithMostDelay();
                        break;
                    case 6:
                        System.out.println("Logged out successfully.");
                        return;
                    default:
                        System.out.println("Invalid option! Please try again.");

                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e + " Returning to the headmaster menu.");
            }
        }
    }

    public void handleEmployeeAddition() {
        System.out.println("\n--- Employee Registration ---");
        if (MAX_EMPLOYEES <= librarySystem.getEmployeeCount()) {
            System.out.println("The limitation hit! There's already " + MAX_EMPLOYEES + " employees signed in system.");
            return;
        }

        System.out.print("Employee's Name: ");
        String name = scanner.nextLine();

        System.out.print("Employee's ID: ");
        String employeeId = scanner.nextLine();

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        librarySystem.registerEmployee(name, employeeId, username, password);
    }

    public void handleEmployeePerformance() {
        System.out.println("\n--- Employee's Performance Details ---");
        System.out.print("Employee's ID: ");
        String employeeId = scanner.nextLine();

        List<Integer> details = librarySystem.employeePerformanceDetails(employeeId);
        System.out.println("1. Books signed by this employee count: " + details.get(0));
        System.out.println("2. Books given by this employee count: " + details.get(1));
        System.out.println("3. Books gotten by this employee count: " + details.get(2));
        System.out.println(" ---------------------------------------------- ");
    }

    public void handleBookStatistics() {
        System.out.println("\n--- Book Statistics ---");
        System.out.print("Book ID: ");
        String bookId = scanner.nextLine();

        List<Double> details = librarySystem.bookStatistics(bookId);
        System.out.println("1. Current Requests Signed for This Book : " + details.get(0).intValue());
        System.out.println("2. Borrowing History Count for This Book  : " + details.get(1).intValue());
        System.out.println("3. Average Days per Loan : " + details.get(2));
        System.out.println(" ------------------------------------------------");
    }

    public void handleGeneralStudentsStatistics() {
        System.out.println("\n--- Students Statistics ---");

        List<Integer> details = librarySystem.studentsGeneralStatistics();
        System.out.print(" All Borrowed Books: " + details.get(0) + "\t");
        System.out.println(" All Unreturned Books: " + details.get(1));
        System.out.print(" All Overdue Books: " + details.get(2) + "\t");
        System.out.println(" All Current Requests: " + details.get(3));
        System.out.println("\n Statistics Per Person: \n");
        librarySystem.displayAllStudentStatistics();


    }

    private int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            } catch (NoSuchElementException e) {
                System.out.println("\nInput stream closed. Saving and exiting system.");
                librarySystem.saveExit();
                System.exit(0);
                return -1;
            }
        }
    }
}