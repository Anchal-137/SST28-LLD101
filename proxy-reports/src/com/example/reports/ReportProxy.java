package com.example.reports;

// Proxy: access control + lazy loading + caching of real report
public class ReportProxy implements Report {

    private final String reportId;
    private final String title;
    private final String classification;
    private final AccessControl accessControl = new AccessControl();

    private RealReport cached; // lazy loaded, reused on next call

    public ReportProxy(String reportId, String title, String classification) {
        this.reportId = reportId;
        this.title = title;
        this.classification = classification;
    }

    @Override
    public void display(User user) {
        if (!accessControl.canAccess(user, classification)) {
            System.out.println("ACCESS DENIED: " + user.getName()
                    + " (" + user.getRole() + ") cannot view " + classification + " report " + reportId);
            return;
        }

        if (cached == null) {
            cached = new RealReport(reportId, title, classification);
        }
        cached.display(user);
    }
}