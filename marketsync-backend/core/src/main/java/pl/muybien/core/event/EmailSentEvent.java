package pl.muybien.core.event;

public class EmailSentEvent {
    private String customerEmail;
    private String subject;
    private String body;

    public EmailSentEvent() {
    }

    public EmailSentEvent(String customerEmail, String subject, String body) {
        this.customerEmail = customerEmail;
        this.subject = subject;
        this.body = body;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
