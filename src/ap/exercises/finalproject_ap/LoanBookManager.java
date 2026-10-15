package ap.exercises.finalproject_ap;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanBookManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<LoanBook> loans;

    public LoanBookManager() {
        loans = new ArrayList<>();
    }

    public void addLoan(Employee giverEmployee, LoanRequest request) {
        LoanBook newLoanBook = new LoanBook(request, giverEmployee);
        loans.add(newLoanBook);
        System.out.println("Request successfully accepted. The book is ready for borrowing.");
    }

    public boolean isABookAvailable(Book targetBook) {
        long count = loans.stream()
                .filter(l -> l.getBook() == targetBook)
                .filter(l -> !(l.isReturned()))
                .count();
        return count == 0;
    }

    public void displayStudentStatistics(Student student) {
        int allLoansCount = 0;
        int notReturnedLoansCount = 0;
        int withDelayLoansCount = 0;
        for (LoanBook loanBook : loans) {
            if (loanBook.getStudent() == student) {
                allLoansCount++;
                if (!loanBook.isReturned()) {
                    notReturnedLoansCount++;
                }
                if (loanBook.isDelayedInReturning()) {
                    withDelayLoansCount++;
                }
            }
        }

        System.out.print("All Borrowed Books: " + allLoansCount + "\t");
        System.out.print("Unreturned Books: " + notReturnedLoansCount + "\t");
        System.out.println("Overdue Books: " + withDelayLoansCount);
        System.out.println();

    }

    public int getLoanHistoryCount() {
        return loans.size();
    }

    // ----- Database layer access (package-private) -----

    void restore(List<LoanBook> savedLoans) {
        this.loans = new ArrayList<>(savedLoans);
    }

    List<LoanBook> getLoans() {
        return loans;
    }

    public void returnBook(String bookId, Employee currentEmployee) {
        LoanBook loanBook = loans.stream()
                .filter(l -> l.getBook().getBookId().equals(bookId))
                .filter(l -> !(l.isReturned()))
                .findFirst()
                .orElse(null);
        if (loanBook == null) {
            System.out.println("There's not such a book on loan.");
            return;
        }
        loanBook.setGetterEmployee(currentEmployee);
        loanBook.setReturnDate(LocalDate.now());
        System.out.println("Book returned successfully.");
    }

    public Integer bookGivenByEmployeeCount(String employeeId) {
        return (int) loans.stream()
                .filter(l -> l.getGiverEmployee().getUserId().equals(employeeId))
                .count();
    }

    public Integer bookGottenByEmployeeCount(String employeeId) {
        return (int) loans.stream()
                .filter(l -> l.getGetterEmployee().getUserId().equals(employeeId))
                .count();
    }

    public int allLoansForBook(String bookId) {
        return (int) loans.stream()
                .filter(l -> l.getBook().getBookId().equals(bookId))
                .count();
    }

    public double averageBorrowingDuration(String bookId) {

        return loans.stream()
                .filter(l -> l.getBook().getBookId().equals(bookId) && l.isReturned())
                .mapToInt(l -> l.loanDuration())
                .average()
                .orElse(0);
    }

    public int allLoansCount() {
        return loans.size();
    }

    public int allUnreturnedLoansCount() {
        return (int) loans.stream()
                .filter(l -> !(l.isReturned()))
                .count();
    }

    public int allOverdueLoansCount() {
        return (int) loans.stream()
                .filter(l -> l.isDelayedInReturning())
                .count();
    }

    public void displayTenStudentsWithMostDelay() {
        List<Student> studentsWithDelay = new ArrayList<>();
        List<Integer> delayDuration = new ArrayList<>();
        int index;

        for (LoanBook loan : loans) {
            if (loan.isDelayedInReturning() && (loan.delayDuration()) > 0) {
                if ((index = studentsWithDelay.indexOf(loan.getStudent())) == -1) {
                    studentsWithDelay.add(loan.getStudent());
                    delayDuration.add(loan.delayDuration());
                } else {
                    delayDuration.set(index, delayDuration.get(index) + loan.delayDuration());
                }
            }
        }
        if (studentsWithDelay.size() == 0) {
            System.out.println("No students with delay.");
            return;
        }

        for (int i = 0; i < Math.min(10, studentsWithDelay.size()); i++) {
            int maxDelayedIndex = 0;
            int maxDelay = delayDuration.get(0);
            for (int j = 1; j < studentsWithDelay.size(); j++) {
                if (delayDuration.get(j) > maxDelay) {
                    maxDelay = delayDuration.get(j);
                    maxDelayedIndex = j;
                }
            }
            System.out.println((i + 1) + ". " + studentsWithDelay.get(maxDelayedIndex) + " | Delay Duration: " + maxDelay);
            studentsWithDelay.remove(maxDelayedIndex);
            delayDuration.remove(maxDelayedIndex);

        }
    }
}
