package com.example.reports;

// Now depends on Report interface, works with proxies
public class ReportViewer {

    public void open(Report report, User user) {
        report.display(user);
    }
}