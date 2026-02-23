public class SmsSender extends NotificationSender {

    public SmsSender(AuditLog audit) {
        super(audit);
    }

    @Override
    protected void sendNotificaion(Notification n) {
        System.out.println("SMS -> to=" + n.phone + " body=" + n.body);
    }

    @Override
    protected String getAuditMessage() {
        return "sms sent";
    }
}