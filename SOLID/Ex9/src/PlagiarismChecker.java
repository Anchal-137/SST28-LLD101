public class PlagiarismChecker implements PlagiarismCheckable {
    @Override
    public int check(Submission s) {
        // fake score: lower is "better", but pipeline adds it anyway
        return (s.code.contains("class") ? 12 : 40);
    }
}
