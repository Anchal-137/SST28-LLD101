public class WhatsAppSender extends NotificationSender {

    public WhatsAppSender(AuditLog audit) {
        super(audit);
    }

    @Override
    protected void sendNotificaion(Notification n) {
        System.out.println("WA -> to=" + n.phone + " body=" + n.body);
    }

    @Override
    protected String getAuditMessage() {
        return "wa sent";
    }
}