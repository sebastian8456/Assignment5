package org.example.Assignments;

import org.example.Assignment.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class SAP_BasedInvoiceSenderTest {
    @Test
    void testWhenLowInvoicesSent() {
        // Create mock objects for dependencies
        FilterInvoice mockFilter = mock(FilterInvoice.class);
        SAP mockSap = mock(SAP.class);

        // Create an instance of SAP_BasedInvoiceSender with the mock dependencies
        SAP_BasedInvoiceSender sapSender = new SAP_BasedInvoiceSender(mockFilter, mockSap);

        // Stub the return value of filter.lowValueInvoices() to return some low-value invoices
        when(mockFilter.lowValueInvoices()).thenReturn(List.of(
                new Invoice("Joe", 50),   // Low-value invoice
                new Invoice("Jill", 99)    // Low-value invoice
        ));

        // Call the method under test
        sapSender.sendLowValuedInvoices();

        // Verify that sap.send() was called for each low-value invoice
        verify(mockSap, times(1)).send(new Invoice("Joe", 50));
        verify(mockSap, times(1)).send(new Invoice("Jill", 99));
    }

    @Test
    void testWhenNoInvoices() {
        // Create mock objects for dependencies
        FilterInvoice mockFilter = mock(FilterInvoice.class);
        SAP mockSap = mock(SAP.class);  // Mock the SAP interface

        // Create an instance of SAP_BasedInvoiceSender with the mock dependencies
        SAP_BasedInvoiceSender sapSender = new SAP_BasedInvoiceSender(mockFilter, mockSap);

        // Stub the return value of filter.lowValueInvoices() to return an empty list
        when(mockFilter.lowValueInvoices()).thenReturn(Collections.emptyList());

        // Call the method under test
        sapSender.sendLowValuedInvoices();

        // Verify that sap.send() was never called
        verify(mockSap, never()).send(any(Invoice.class));
    }

    @Test
    void testThrowExceptionWhenBadInvoice() {
        // Create mock objects for dependencies
        FilterInvoice mockFilter = mock(FilterInvoice.class);
        SAP mockSap = mock(SAP.class);

        // Create an instance of SAP_BasedInvoiceSender with the mock dependencies
        SAP_BasedInvoiceSender sapSender = new SAP_BasedInvoiceSender(mockFilter, mockSap);

        // Create some invoices
        Invoice goodInvoice = new Invoice("Joe", 50);
        Invoice badInvoice = new Invoice("Jill", 99);

        // Stub the return value of filter.lowValueInvoices() to return the invoices
        when(mockFilter.lowValueInvoices()).thenReturn(List.of(goodInvoice, badInvoice));

        // Stub the sap.send() method to throw an exception for the bad invoice
        doThrow(new FailToSendSAPInvoiceException()).when(mockSap).send(badInvoice);

        // Call the method under test
        List<Invoice> failedInvoices = sapSender.sendLowValuedInvoices();

        // Verify that sap.send() was called for both invoices
        verify(mockSap, times(1)).send(goodInvoice);
        verify(mockSap, times(1)).send(badInvoice);

        // Verify that the failed invoice is in the returned list
        assertEquals(1, failedInvoices.size(), "There should be 1 failed invoice");
        assertEquals(badInvoice, failedInvoices.get(0), "The failed invoice should match the bad invoice");
    }
}
