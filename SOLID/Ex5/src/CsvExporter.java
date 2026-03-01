import java.nio.charset.StandardCharsets;

public class CsvExporter extends Exporter {

    @Override
    protected ExportResult doExport(ExportRequest req) {
        // Fixed: properly quote fields so commas and newlines are preserved, no
        // silent data corruption anymore. RFC-4180 style quoting.
        String body = req.body == null ? "" : req.body;
        String title = req.title == null ? "" : req.title;

        String csv = "title,body\n" + csvEscape(title) + "," + csvEscape(body) + "\n";
        return new ExportResult("text/csv", csv.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected ExportResult emptyResult() {
        return new ExportResult("text/csv", new byte[0]);
    }

    private String csvEscape(String value) {
        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
