package ap.exercises.finalproject_ap;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Testing GitHub connection
public class LibrarySystem implements Serializable {

    private static final long serialVersionUID = 1L;

    private MenuHandler menuHandler;
    private StudentManager studentManager;
    private LoanBookManager loanBookManager;
    private BookManager bookManager;
    private LoanRequestManager loanRequestManager;
    private final EmployeeManager employeeManager;
    private final Persistence persistence;

    public LibrarySystem() {
        this.studentManager = new StudentManager();
        this.menuHandler = new MenuHandler(this);
        this.loanBookManager = new LoanBookManager();
        this.bookManager = new BookManager(this.loanBookManager);
        this.loanRequestManager = new LoanRequestManager();
        this.employeeManager = new EmployeeManager();
        this.persistence = new Persistence();

    }

    public int getStudentCount() {
        return this.studentManager.getStudentCount();
    }

    public int getBookCount() {
        return this.bookManager.getBookCount();
    }

    public int getLoanHistoryCount() {
        return this.loanBookManager.getLoanHistoryCount();
    }

    public int onLoanBookCount() {
        return this.bookManager.onLoanBookCount();
    }

    public void registerStudent(String name, String studentId, String username, String password) {
        if (employeeManager.isUsernameTaken(username)) {
            System.out.println("This username already exists (taken by an employee). Please choose a different username.");
            return;
        }
        studentManager.registerStudent(name, studentId, username, password);
    }

    public void registerEmployee(String name, String employeeId, String username, String password) {
        if (studentManager.isUsernameTaken(username)) {
            System.out.println("This username already exists (taken by a student). Please choose a different username.");
            return;
        }
        employeeManager.registerEmployee(name, employeeId, username, password);
    }

    public Student authenticateStudent(String username, String password) {
        return studentManager.authenticateStudent(username, password);
    }

    public Employee authenticateEmployee(String username, String password) {
        return employeeManager.authenticateEmployee(username, password);
    }

    public void employeePasswordChanging(Employee employee, String password) {
        employeeManager.employeePasswordChanging(employee, password);
    }

    public void editStudentInformation(Student student, String username, String password) {
        studentManager.editStudentInformation(student, username, password);
    }

    public void addNewBook(Employee employee, String bookId, String title, String author, int publishYear) {
        bookManager.addNewBook(employee, title, author, publishYear, bookId);
    }

    public void displayAvailableBooks() {
        bookManager.displayAvailableBooks();
    }

    public void searchBookByTitleForGuest(String title) {
        bookManager.searchBYTitleForGuest(title);
    }

    public void searchBook(String title, String author, String publishYear) {
        bookManager.searchBook(title, author, publishYear);
    }

    public void changeBookInfo(String targetBookId, String bookId, String title, String author, String publishYear) {
        bookManager.changeBookInfo(targetBookId, bookId, title, author, publishYear);
    }

    public void loanBookRequest(String bookId, Student student, LocalDate startDate, LocalDate endDate) {
        Book targetBook = bookManager.searchBookById(bookId);
        if (targetBook == null) {
            System.out.println("Request failed. A book with ID : " + bookId + " does not exist.");
            return;
        }
        if (!loanBookManager.isABookAvailable(targetBook)) {
            System.out.println("Your request failed. The Book is on loan.");
            return;
        }
        if (loanRequestManager.hasPendingRequestFor(targetBook, student)) {
            System.out.println("You already have a pending request for this book.");
            return;
        }
        loanRequestManager.addLoanRequest(targetBook, student, startDate, endDate);
    }

    public List<LoanRequest> listOfRecentRequests() {
        return loanRequestManager.listOfRecentRequests();
    }

    public void convertRequestToAcceptedLoan(Employee giverEmployee, LoanRequest request) {
        if (!loanBookManager.isABookAvailable(request.getBook())) {
            System.out.println("Request can't be accepted. The book is already on loan to someone else.");
            return;
        }
        loanBookManager.addLoan(giverEmployee, request);
        loanRequestManager.deleteRequest(request);
    }

    public void displayStudentStatistics(String id) {
        Student student = studentManager.searchById(id);
        if (student == null) {
            System.out.println("There isn't any student with this Id.");
            return;
        }
        loanBookManager.displayStudentStatistics(student);
    }

    public static void main(String[] args) {
        LibrarySystem system = new LibrarySystem();
        system = system.loadOrCreate();
        system.start();
    }

    public void blockStudent(String id) {
        studentManager.addStudentToBlackList(id);
    }

    public void unblockStudent(String id) {
        studentManager.removeStudentFromBlackList(id);
    }

    public boolean isBlocked(Student student) {
        return studentManager.isBlocked(student);
    }

    public void returnLoaningBook(String bookId, Employee currentEmployee) {
        loanBookManager.returnBook(bookId, currentEmployee);
    }

    public int getEmployeeCount() {
        return employeeManager.getEmployeesCount();
    }

    public List<Integer> employeePerformanceDetails(String employeeId) {
        List<Integer> results = new ArrayList<>();
        results.add(bookManager.bookSignedByEmployeeCount(employeeId));
        results.add(loanBookManager.bookGivenByEmployeeCount(employeeId));
        results.add(loanBookManager.bookGottenByEmployeeCount(employeeId));
        return results;
    }

    public void displayAllStudentStatistics() {
        studentManager.displayStudentStatistics(loanBookManager);
    }

    public void displayTenStudentsWithMostDelay() {
        loanBookManager.displayTenStudentsWithMostDelay();
    }

    public List<Double> bookStatistics(String bookId) {
        List<Double> results = new ArrayList<>();
        results.add((double) loanRequestManager.requestCountForBook(bookId));
        results.add((double) loanBookManager.allLoansForBook(bookId));
        results.add(loanBookManager.averageBorrowingDuration(bookId));
        return results;

    }

    public List<Integer> studentsGeneralStatistics() {
        List<Integer> results = new ArrayList<>();
        results.add(loanBookManager.allLoansCount());
        results.add(loanBookManager.allUnreturnedLoansCount());
        results.add(loanBookManager.allOverdueLoansCount());
        results.add(loanRequestManager.allCurrenRequestsCount());
        return results;
    }

    public LibrarySystem loadOrCreate() {
        LibrarySystem librarySystem = persistence.load();
        if (librarySystem == null) {
            librarySystem = new LibrarySystem();
        }
        librarySystem.menuHandler.setScanner(new Scanner(System.in));
        return librarySystem;
    }

    public void start() {
        menuHandler.displayMainMenu();
    }

    public void saveExit() {
        persistence.save(this);
    }
}
