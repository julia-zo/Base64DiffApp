package org.juliazo.diff.service;

import org.juliazo.diff.commons.exception.CompromisedDataException;
import org.juliazo.diff.model.Base64Data;
import org.juliazo.diff.model.DiffBytes;
import org.juliazo.diff.model.DiffResult;
import org.juliazo.diff.model.ErrorPayload;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test Class for the core functionality of the Base64 Diff App
 */
public class DiffServiceTest {

    /**
     * Service Class, holds all functionality
     */
    private final DiffService diffService = new DiffService();

    /**
     * Test: Add data to a non existent id on /left endpoint
     */
    @Test
    public void testAddLeftData () {
        String id = "1";
        String data = "dGVsZXR1Ymll";

        ResponseEntity actual = diffService.inputLeft(id, data);
        Base64Data base64Data = (Base64Data) actual.getBody();

        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(id, base64Data.getId());
        assertEquals(data, base64Data.getLeftData());
        assertEquals("", base64Data.getRightData());
    }

    /**
     * Test: Add data to an already existent id on /left endpoint,
     * thus replacing the existent data
     */
    @Test
    public void testUpdateLeftData () {
        String id = "2";
        String data = "dGVsZXR1Ymll";

        ResponseEntity responseEntity = diffService.inputLeft(id, data);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        ResponseEntity actual = diffService.inputLeft(id, data + "z");
        assertEquals(HttpStatus.OK, actual.getStatusCode());

        Base64Data base64Data = (Base64Data) actual.getBody();

        assertEquals(id, base64Data.getId());
        assertEquals(data + "z", base64Data.getLeftData());
        assertEquals("", base64Data.getRightData());
    }

    /**
     * Test: Add invalid Base64 data to a non existent id on /left endpoint
     */
    @Test
    public void testInvalidLeftData () {
        String id = "3";
        String data = "dGVsZXR1Ymll\\";

        ResponseEntity actual = diffService.inputLeft(id, data);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

        ErrorPayload errorPayload = (ErrorPayload) actual.getBody();
        assertEquals("Input must use valid Base64 characters", errorPayload.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), errorPayload.getErrorCode());
    }

    /**
     * Test: Add an empty string to a non existent id on /left endpoint
     */
    @Test
    public void testEmptyLeftData () {
        String id = "4";
        String data = "";

        ResponseEntity actual = diffService.inputLeft(id, data);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

        ErrorPayload errorPayload = (ErrorPayload) actual.getBody();
        assertEquals("Field data is required", errorPayload.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), errorPayload.getErrorCode());
    }

    /**
     * Test: Add null object to a non existent id on /left endpoint
     */
    @Test
    public void testNullLeftData () {
        String id = "5";
        String data = null;

        ResponseEntity actual = diffService.inputLeft(id, data);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

        ErrorPayload errorPayload = (ErrorPayload) actual.getBody();
        assertEquals("Field data is required", errorPayload.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), errorPayload.getErrorCode());
    }

    /**
     * Test: Add data to a non existent id on /right endpoint
     */
    @Test
    public void testAddRightData () {
        String id = "6";
        String data = "dGVsZXR1Ymll";

        ResponseEntity actual = diffService.inputRight(id, data);
        Base64Data base64Data = (Base64Data) actual.getBody();

        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(id, base64Data.getId());
        assertEquals(data, base64Data.getRightData());
        assertEquals("", base64Data.getLeftData());
    }

    /**
     * Test: Add data to an already existent id on /right endpoint,
     * thus replacing the existent data
     */
    @Test
    public void testUpdateRightData () {
        String id = "7";
        String data = "dGVsZXR1Ymll";

        ResponseEntity responseEntity = diffService.inputRight(id, data);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        ResponseEntity actual = diffService.inputRight(id, data + "z");
        assertEquals(HttpStatus.OK, actual.getStatusCode());

        Base64Data base64Data = (Base64Data) actual.getBody();

        assertEquals(id, base64Data.getId());
        assertEquals(data + "z", base64Data.getRightData());
        assertEquals("", base64Data.getLeftData());
    }

    /**
     * Test: Add invalid Base64 data to a non existent id on /right endpoint
     */
    @Test
    public void testInvalidRightData () {
        String id = "8";
        String data = "dGVsZXR1Ymll\\";

        ResponseEntity actual = diffService.inputRight(id, data);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

        ErrorPayload errorPayload = (ErrorPayload) actual.getBody();
        assertEquals("Input must use valid Base64 characters", errorPayload.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), errorPayload.getErrorCode());
    }

    /**
     * Test: Add an empty string to a non existent id on /right endpoint
     */
    @Test
    public void testEmptyRightData () {
        String id = "9";
        String data = "";

        ResponseEntity actual = diffService.inputRight(id, data);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

        ErrorPayload errorPayload = (ErrorPayload) actual.getBody();
        assertEquals("Field data is required", errorPayload.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), errorPayload.getErrorCode());
    }

    /**
     * Test: Add null object to a non existent id on /right endpoint
     */
    @Test
    public void testNullRightData () {
        String id = "10";
        String data = null;

        ResponseEntity actual = diffService.inputRight(id, data);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

        ErrorPayload errorPayload = (ErrorPayload) actual.getBody();
        assertEquals("Field data is required", errorPayload.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), errorPayload.getErrorCode());
    }

    /**
     * Test: Perform a diff of id X when the data storage has been tampered with.
     *
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Test (expected = CompromisedDataException.class)
    public void testCompromisedData () throws NoSuchFieldException, IllegalAccessException {
        String id = "11";
        Base64Data compromisedData = new Base64Data("10", "dGVsZXR1Ymll", "dGVsZXR1Ymll");

        Map<String, Base64Data> alteredDiffStorage = new HashMap<>();
        alteredDiffStorage.put(id, compromisedData);

        DiffService alteredDiffService = new DiffService();

        Field field = DiffService.class.getDeclaredField("diffStorage");
        field.setAccessible(true);
        field.set(alteredDiffService, alteredDiffStorage);

        try {
            alteredDiffService.getDiffResult(id);
        } catch (CompromisedDataException compromisedException) {
            assertEquals("FATAL ERROR: Data storage has been compromised!",
                    compromisedException.getMessage());
            assertEquals("FATAL ERROR: Provided id and stored id do not match: 11 - 10",
                    compromisedException.getCause().getMessage());
            throw compromisedException;
        }
    }

    /**
     * Test: Perform a diff operation on a non-existent id
     */
    @Test
    public void testGetDiffNotFound () {
        String id = "12";
        ResponseEntity actual = diffService.getDiffResult(id);
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ErrorPayload errorPayload = (ErrorPayload) actual.getBody();
        assertEquals("Data not Found", errorPayload.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.toString(), errorPayload.getErrorCode());
    }

    /**
     * Test: Perform a diff operation on an id that has data only for the right side
     */
    @Test
    public void testGetDiffMissingLeft () {
        String id = "13";
        String data = "dGVsZXR1Ymll";

        ResponseEntity rightData = diffService.inputRight(id, data);
        Base64Data base64Data = (Base64Data) rightData.getBody();

        assertEquals(HttpStatus.CREATED, rightData.getStatusCode());
        assertEquals("", base64Data.getLeftData());

        ResponseEntity actual = diffService.getDiffResult(id);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

        ErrorPayload errorPayload = (ErrorPayload) actual.getBody();
        assertEquals("Missing Left data", errorPayload.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), errorPayload.getErrorCode());
    }

    /**
     * Test: Perform a diff operation on an id that has data only for the left side
     */
    @Test
    public void testGetDiffMissingRight () {
        String id = "14";
        String data = "dGVsZXR1Ymll";

        ResponseEntity leftData = diffService.inputLeft(id, data);
        Base64Data base64Data = (Base64Data) leftData.getBody();

        assertEquals(HttpStatus.CREATED, leftData.getStatusCode());
        assertEquals("", base64Data.getRightData());

        ResponseEntity actual = diffService.getDiffResult(id);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

        ErrorPayload errorPayload = (ErrorPayload) actual.getBody();
        assertEquals("Missing Right data", errorPayload.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), errorPayload.getErrorCode());
    }

    /**
     * Test: Perform a diff operation with data with different sizes
     * Values for Equals and Differences are omitted, since the data on both sides
     * cannot be equal if they have different sizes
     */
    @Test
    public void testGetDiffDifferentSize () {
        String id = "15";
        String dataRight = "dGVsZXR1Ymll";
        String dataLeft = "dGVsZXR1Ymllcw==";

        inputRightLeft(id, dataRight, dataLeft);

        ResponseEntity actual = diffService.getDiffResult(id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());

        DiffResult diffResult = (DiffResult) actual.getBody();
        assertEquals(id, diffResult.getId());
        assertFalse(diffResult.isEqualSize());
        assertNull(diffResult.isEquals());
        assertNull(diffResult.getDifferences());
    }

    /**
     * Test: Perform a diff operation with equal data on both sides
     * Value for Differences is omitted because there are no differences
     */
    @Test
    public void testGetDiffEquals () {
        String id = "16";
        String dataRight = "dGVsZXR1Ymll";
        String dataLeft = "dGVsZXR1Ymll";

        //Create left side first to test combination {leftData not empty, rightData empty}
        ResponseEntity leftResponse = diffService.inputLeft(id, dataLeft);
        assertEquals(HttpStatus.CREATED, leftResponse.getStatusCode());

        ResponseEntity rightResponse = diffService.inputRight(id, dataRight);
        assertEquals(HttpStatus.CREATED, rightResponse.getStatusCode());

        Base64Data base64Data = (Base64Data) rightResponse.getBody();
        assertEquals(id, base64Data.getId());
        assertEquals(dataRight, base64Data.getRightData());
        assertEquals(dataLeft, base64Data.getLeftData());

        ResponseEntity actual = diffService.getDiffResult(id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());

        DiffResult diffResult = (DiffResult) actual.getBody();
        assertEquals(id, diffResult.getId());
        assertTrue(diffResult.isEqualSize());
        assertTrue(diffResult.isEquals());
        assertNull(diffResult.getDifferences());

    }

    /**
     * Test: Perform a diff operation where the difference is on the first
     * byte of the data
     */
    @Test
    public void testGetDiffOnFirstByte () {
        String id = "17";
        String dataRight = "dGVsZXR1Ymll";
        String dataLeft = "ZGVsZXR1Ymll";

        inputRightLeft(id, dataRight, dataLeft);

        ResponseEntity actual = diffService.getDiffResult(id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());

        DiffResult diffResult = (DiffResult) actual.getBody();
        assertEquals(id, diffResult.getId());
        assertTrue(diffResult.isEqualSize());
        assertFalse(diffResult.isEquals());
        assertNotNull(diffResult.getDifferences());
        assertEquals(1, diffResult.getDifferences().size());

        DiffBytes diffBytes = diffResult.getDifferences().get(0);
        assertEquals(0, diffBytes.getOffset());
        assertEquals(1, diffBytes.getLength());
    }

    /**
     * Test: Perform a diff operation where the difference is on the last
     * byte of the data
     */
    @Test
    public void testGetDiffOnLastByte () {
        String id = "18";
        String dataRight = "dGVsZXR1Ymll";
        String dataLeft = "dGVsZXR1Ymlh";

        inputRightLeft(id, dataRight, dataLeft);

        ResponseEntity actual = diffService.getDiffResult(id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());

        DiffResult diffResult = (DiffResult) actual.getBody();
        assertEquals(id, diffResult.getId());
        assertTrue(diffResult.isEqualSize());
        assertFalse(diffResult.isEquals());
        assertNotNull(diffResult.getDifferences());
        assertEquals(1, diffResult.getDifferences().size());

        DiffBytes diffBytes = diffResult.getDifferences().get(0);
        assertEquals(8, diffBytes.getOffset());
        assertEquals(1, diffBytes.getLength());
    }

    /**
     * Test: Perform a diff operation where the difference is on the
     * middle of the data
     */
    @Test
    public void testGetDiffOnMiddleByte () {
        String id = "19";
        String dataRight = "dGVsZXRvYmll";
        String dataLeft = "dGVsZXR1Ymll";

        inputRightLeft(id, dataRight, dataLeft);

        ResponseEntity actual = diffService.getDiffResult(id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());

        DiffResult diffResult = (DiffResult) actual.getBody();
        assertEquals(id, diffResult.getId());
        assertTrue(diffResult.isEqualSize());
        assertFalse(diffResult.isEquals());
        assertNotNull(diffResult.getDifferences());
        assertEquals(1, diffResult.getDifferences().size());

        DiffBytes diffBytes = diffResult.getDifferences().get(0);
        assertEquals(5, diffBytes.getOffset());
        assertEquals(1, diffBytes.getLength());
    }

    /**
     * Test: Perform a diff operation where the difference extends for more
     * than on consecutive byte
     */
    @Test
    public void testGetDiffLongSequence () {
        String id = "20";
        String dataRight = "dGVsaWNvYmll";
        String dataLeft = "dGVsZXR1Ymll";

        inputRightLeft(id, dataRight, dataLeft);

        ResponseEntity actual = diffService.getDiffResult(id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());

        DiffResult diffResult = (DiffResult) actual.getBody();
        assertEquals(id, diffResult.getId());
        assertTrue(diffResult.isEqualSize());
        assertFalse(diffResult.isEquals());
        assertNotNull(diffResult.getDifferences());
        assertEquals(1, diffResult.getDifferences().size());

        DiffBytes diffBytes = diffResult.getDifferences().get(0);
        assertEquals(3, diffBytes.getOffset());
        assertEquals(3, diffBytes.getLength());
    }

    /**
     * Test: Perform a diff operation where there are differences in
     * multiple sectors of the data
     */
    @Test
    public void testGetDiffMultipleOffsets () {
        String id = "21";
        String dataRight = "dGV2ZXRvYmlh";
        String dataLeft = "dGVsZXR1Ymll";

        inputRightLeft(id, dataRight, dataLeft);

        ResponseEntity actual = diffService.getDiffResult(id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());

        DiffResult diffResult = (DiffResult) actual.getBody();
        assertEquals(id, diffResult.getId());
        assertTrue(diffResult.isEqualSize());
        assertFalse(diffResult.isEquals());
        assertNotNull(diffResult.getDifferences());
        assertEquals(3, diffResult.getDifferences().size());

        DiffBytes diffBytes = diffResult.getDifferences().get(0);
        assertEquals(2, diffBytes.getOffset());
        assertEquals(1, diffBytes.getLength());
        diffBytes = diffResult.getDifferences().get(1);
        assertEquals(5, diffBytes.getOffset());
        assertEquals(1, diffBytes.getLength());
        diffBytes = diffResult.getDifferences().get(2);
        assertEquals(8, diffBytes.getOffset());
        assertEquals(1, diffBytes.getLength());
    }

    /**
     * Auxiliary method to input valid data on both sides
     * of the diff while checking for the correct
     * response for each data inclusion
     *
     * @param id            the unique identifier of this set of data
     * @param dataRight     encoded base64 data for the right side of the diff
     * @param dataLeft      encoded base64 data for the left side of the diff
     */
    private void inputRightLeft(String id, String dataRight, String dataLeft) {
        //Create right side first to test combination {leftData empty, rightData not empty}
        ResponseEntity rightResponse = diffService.inputRight(id, dataRight);
        assertEquals(HttpStatus.CREATED, rightResponse.getStatusCode());

        ResponseEntity leftResponse = diffService.inputLeft(id, dataLeft);
        assertEquals(HttpStatus.CREATED, leftResponse.getStatusCode());

        Base64Data base64Data = (Base64Data) leftResponse.getBody();
        assertEquals(id, base64Data.getId());
        assertEquals(dataRight, base64Data.getRightData());
        assertEquals(dataLeft, base64Data.getLeftData());
    }
}
