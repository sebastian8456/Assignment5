package org.example.Assignment;

public class FailToSendSAPInvoiceException extends RuntimeException {
    public FailToSendSAPInvoiceException() {
        super("Failed to send invoice to SAP");
    }
}
