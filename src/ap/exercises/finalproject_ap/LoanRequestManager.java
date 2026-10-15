package ap.exercises.finalproject_ap;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LoanRequestManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<LoanRequest> requests;

    public LoanRequestManager() {
        this.requests = new ArrayList<>();
    }

    public void addLoanRequest(Book book, Student student, LocalDate startDate, LocalDate endDate) {
        LoanRequest request = new LoanRequest(student, book, startDate, endDate);
        requests.add(request);
        System.out.println("The request for a book signed in system.");
        System.out.println("--The Request Information: ");
        System.out.println(request);
    }

    public List<LoanRequest> listOfRecentRequests() {
        renewList();
        return new ArrayList<>(requests);
    }

    public boolean hasPendingRequestFor(Book book, Student student) {
        renewList();
        return requests.stream()
                .anyMatch(r -> r.getBook() == book && r.getStudent() == student);
    }

    public int requestCountForBook(String bookId) {
        renewList();
        return (int) requests.stream()
                .filter(r -> r.getBook().getBookId().equals(bookId))
                .count();
    }

    public void deleteRequest(LoanRequest request) {
        requests.remove(request);
    }

    private void renewList() {
        //Delete old requests from list of them.

        LocalDate yesterday = LocalDate.now().minusDays(1);
        requests = requests.stream()
                .filter(r -> !(r.getStartDate().isBefore(yesterday)))
                .collect(Collectors.toList());
    }

    public int allCurrenRequestsCount() {
        renewList();
        return requests.size();
    }

    // ----- Database layer access (package-private) -----

    void restore(List<LoanRequest> savedRequests) {
        this.requests = new ArrayList<>(savedRequests);
    }

    List<LoanRequest> getRequests() {
        return requests;
    }
}
