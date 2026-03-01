public class Main {
    public static void main(String[] args) {
        System.out.println("=== Evaluation Pipeline ===");
        Submission sub = new Submission("23BCS1007", "public class A{}", "A.java");

        // wire up concretes and inject into pipeline
        PlagiarismCheckable checker = new PlagiarismChecker();
        Gradable grader = new CodeGrader();
        ReportWritable writer = new ReportWriter();

        new EvaluationPipeline(checker, grader, writer).evaluate(sub);
    }
}
