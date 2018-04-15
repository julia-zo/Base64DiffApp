package org.juliazo.diff.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for POJO {@link org.juliazo.diff.model.DiffBytes}
 * Although testing a POJO class does not test any actual functionality,
 * I chose to include it so the coverage report will show when changes
 * were made to the POJO.
 */
public class DiffBytesTest {


    /**
     * Creates a new element using the default constructor,
     * alters the data using the element's set methods and then
     * checks that data using the element's get methods
     */
    @Test
    public void testDiffBytes () {
        int offset = 1;
        int length = 2;

        DiffBytes actual = new DiffBytes();
        actual.setOffset(offset);
        actual.setLength(length);

        assertEquals(offset, actual.getOffset());
        assertEquals(length, actual.getLength());

    }
}
