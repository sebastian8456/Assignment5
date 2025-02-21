package org.example.Assignments;

import org.example.Assignment.Database;
import org.example.Assignment.FilterInvoice;
import org.example.Assignment.Invoice;
import org.example.Assignment.QueryInvoicesDAO;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

public class FilterInvoiceTest {
    @Test
    void filterInvoiceTest() {
        // Create a real instance of QueryInvoicesDAO
        Database db = new Database();
        QueryInvoicesDAO dao = new QueryInvoicesDAO(db);

        // Create an instance of FilterInvoice with the real DAO
        FilterInvoice filterInvoice = new FilterInvoice(dao);

        // Call the method under test
        List<Invoice> result = filterInvoice.lowValueInvoices();

        // Verify the result (this assumes the database contains some data)
        assertNotNull(result, "The result should not be null");
        for (Invoice invoice : result) {
            assertTrue(invoice.getValue() < 100, "All invoices should have a value less than 100");
        }
    }

    @Test
    void filterInvoiceStubbedTest() {
        // Create a stub for QueryInvoicesDAO
        QueryInvoicesDAO stubDao = new QueryInvoicesDAO(null) {
            @Override
            public List<Invoice> all() {
                // Return a predefined list of invoices for testing
                return List.of(
                        new Invoice("Joe", 50),   // Low-value invoice
                        new Invoice("Jack", 150),
                        new Invoice("Jill", 99),   // Low-value invoice
                        new Invoice("Adam", 100)
                );
            }
        };

        // Create an instance of FilterInvoice with the stubbed DAO
        FilterInvoice filterInvoice = new FilterInvoice(stubDao);

        // Call the method under test
        List<Invoice> result = filterInvoice.lowValueInvoices();

        // Verify the result
        assertEquals(2, result.size(), "There should be 2 low-value invoices");

        // Verify the values of the filtered invoices
        assertEquals(50, result.get(0).getValue(), "First invoice should have a value of 50");
        assertEquals(99, result.get(1).getValue(), "Second invoice should have a value of 99");
    }
}
