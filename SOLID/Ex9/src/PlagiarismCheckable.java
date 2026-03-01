// Abstraction so the pipeline doesn't depend on concrete PlagiarismChecker
public interface PlagiarismCheckable {
    int check(Submission s);
}
