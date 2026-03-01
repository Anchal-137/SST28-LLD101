import java.nio.charset.StandardCharsets;

public class JsonExporter extends Exporter {

    @Override
    protected ExportResult doExport(ExportRequest req) {
        // null handling now lives in the base class (emptyResult), so here
        // we just focus on building the JSON output.
        String json = "{\"title\":\"" + escape(req.title) + "\",\"body\":\"" + escape(req.body) + "\"}";
        return new ExportResult("application/json", json.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected ExportResult emptyResult() {
        return new ExportResult("application/json", new byte[0]);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }
}
