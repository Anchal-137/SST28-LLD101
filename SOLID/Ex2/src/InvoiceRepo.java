public interface InvoiceRepo {
    void save(String name, String content);
    int countLines(String name);
}