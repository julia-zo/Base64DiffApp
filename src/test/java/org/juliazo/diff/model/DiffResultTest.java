package org.juliazo.diff.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for POJO {@link org.juliazo.diff.model.DiffResult}
 * Although testing a POJO class does not test any actual functionality,
 * I chose to include it so the coverage report will show when changes
 * were made to the POJO.
 */
public class DiffResultTest {

    /**
     * Creates a new element using the default constructor,
     * alters the data using the element's set methods and then
     * checks that data using the element's get methods
     */
    @Test
    public void testBase64DataEditFields () {
        String id = "1";

        DiffBytes diffBytes = new DiffBytes();
        diffBytes.setOffset(1);
        diffBytes.setLength(1);
        List<DiffBytes> differences = new ArrayList<>();
        differences.add(diffBytes);

        DiffResult actual = new DiffResult();

        actual.setId(id);
        actual.setEqualSize(true);
        actual.setEquals(Boolean.TRUE);
        actual.setDifferences(differences);

        assertEquals(id, actual.getId());
        assertTrue(actual.isEqualSize());
        assertTrue(actual.isEquals());
        assertEquals(differences, actual.getDifferences());
    }

}
