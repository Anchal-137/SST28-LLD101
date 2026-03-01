/**
 * Base class for all exporters.
 *
 * Contract (postconditions every subclass must honour):
 *  - export() never throws for non-null ExportRequest whose title and body are non-null strings
 *  - If the request is null, return an empty result (0-byte array with the appropriate mime type)
 *  - Returned bytes faithfully represent the body; no silent data loss
 *  - Content-type matches the format
 */
public abstract class Exporter {

    public final ExportResult export(ExportRequest req) {
        if (req == null) {
            return emptyResult();
        }
        return doExport(req);
    }

    protected abstract ExportResult doExport(ExportRequest req);

    protected abstract ExportResult emptyResult();
}
