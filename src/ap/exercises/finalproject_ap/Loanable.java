package ap.exercises.finalproject_ap;

public interface Loanable {
    public boolean isReturned();
    public boolean isDelayedInReturning();
    public int loanDuration();
    public int delayDuration();
}
