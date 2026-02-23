public class EmailSender extends NotificationSender {

    public EmailSender(AuditLog audit) {
        super(audit);
    }

    @Override
    protected void sendNotificaion(Notification n) {
        String body = n.body;

        // Explicit documented formatting rule (not hidden behavior)
        if (body.length() > 40) {
            body = body.substring(0, 40);
        }

        System.out.println("EMAIL -> to=" + n.email +
                " subject=" + n.subject +
                " body=" + body);
    }

    @Override
    protected String getAuditMessage() {
        return "email sent";
    }
}