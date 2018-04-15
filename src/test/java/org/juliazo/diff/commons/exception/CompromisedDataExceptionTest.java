package org.juliazo.diff.commons.exception;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Test class for exception {@link org.juliazo.diff.commons.exception.CompromisedDataException}
 */
public class CompromisedDataExceptionTest {

    /**
     * Throws a new exception with a Throwable element,
     * catches this exception and checks the cause and the
     * message.
     */
    @Test
    public void testCompromisedDataException () {
        Throwable expected = new RuntimeException();
        String message = "FATAL ERROR: Data storage has been compromised!";
        try {
            throw new CompromisedDataException(expected);
        } catch (CompromisedDataException compromisedDataException) {
            assertEquals(message, compromisedDataException.getMessage());
            assertEquals(expected, compromisedDataException.getCause());
        }
    }
}
