// Abstraction so the pipeline doesn't depend on the concrete CodeGrader
public interface Gradable {
    int grade(Submission s, Rubric r);
}
