package org.juliazo.diff.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for POJO {@link org.juliazo.diff.model.ErrorPayload}
 * Although testing a POJO class does not test any actual functionality,
 * I chose to include it so the coverage report will show when changes
 * were made to the POJO.
 */
public class ErrorPayloadTest {

    /**
     * Creates a new element using the default constructor,
     * alters the data using the element's set methods and then
     * checks that data using the element's get methods
     */
    @Test
    public void testBase64DataEditFields () {
        String errorCode = "400";
        String message = "error";

        ErrorPayload actual = new ErrorPayload();

        actual.setErrorCode(errorCode);
        actual.setMessage(message);

        assertEquals(errorCode, actual.getErrorCode());
        assertEquals(message, actual.getMessage());
    }
}
