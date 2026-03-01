// Abstraction so the pipeline doesn't depend on the concrete ReportWriter
public interface ReportWritable {
    String write(Submission s, int plag, int code);
}
