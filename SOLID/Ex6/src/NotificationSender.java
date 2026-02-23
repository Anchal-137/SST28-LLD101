public abstract class NotificationSender {

    protected AuditLog audit;

    public NotificationSender(AuditLog audit) {
        this.audit = audit;
    }

    public final void send(Notification n) {
        validate(n);          
        sendNotificaion(n);            
        audit.add(getAuditMessage());
    }

    protected void validate(Notification n) {
        if (n == null) throw new IllegalArgumentException("Notification cannot be null");
        if (n.body == null) throw new IllegalArgumentException("Body cannot be null");
    }

    protected abstract void sendNotificaion(Notification n);

    protected abstract String getAuditMessage();
}