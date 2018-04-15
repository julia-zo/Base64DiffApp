package org.juliazo.diff.model;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Test class for POJO {@link org.juliazo.diff.model.Base64Data}
 * Although testing a POJO class does not test any actual functionality,
 * I chose to include it so the coverage report will show when changes
 * were made to the POJO.
 */
public class Base64DataTest {

    /**
     * Creates a new element passing data on the constructor,
     * checks that data using the element's get methods
     */
    @Test
    public void testBase64Data () {
        String id = "1";
        String left = "left";
        String right = "right";

        Base64Data actual = new Base64Data(id, left, right);
        assertEquals(id, actual.getId());
        assertEquals(left, actual.getLeftData());
        assertEquals(right, actual.getRightData());
    }

    /**
     * Creates a new element with the default constructor,
     * sets data using the element's set methods
     */
    @Test
    public void testBase64DataEmptyConstructor () {
        String id = "1";
        String left = "left";
        String right = "right";

        Base64Data actual = new Base64Data();

        actual.setId(id);
        actual.setLeftData(left);
        actual.setRightData(right);

        assertEquals(id, actual.getId());
        assertEquals(left, actual.getLeftData());
        assertEquals(right, actual.getRightData());
    }

    /**
     * Creates a new element passing empty data on the constructor,
     * alters the data using the element's set methods and then
     * checks that data using the element's get methods
     */
    @Test
    public void testBase64DataEditFields () {
        String id = "1";
        String left = "left";
        String right = "right";

        Base64Data actual = new Base64Data("", "", "");
        assertEquals("", actual.getId());
        assertEquals("", actual.getLeftData());
        assertEquals("", actual.getRightData());

        actual.setId(id);
        actual.setLeftData(left);
        actual.setRightData(right);

        assertEquals(id, actual.getId());
        assertEquals(left, actual.getLeftData());
        assertEquals(right, actual.getRightData());
    }
}
