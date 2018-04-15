package org.juliazo.diff;

import org.juliazo.diff.controller.Base64DiffController;
import org.juliazo.diff.model.Base64Data;
import org.juliazo.diff.model.Base64DataPayload;
import org.juliazo.diff.model.DiffBytes;
import org.juliazo.diff.model.DiffResult;
import org.juliazo.diff.model.ErrorPayload;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for Base64 Diff Application
 * To run only integration tests use command line:
 *      mvn clean test -Dtest=Base64DiffAppIntTest
 *
 * To run all tests except this one (only unit tests)
 * use command line:
 *      mvn clean tests -Dtest=\!Base64DiffTestAppIntTest
 *
 * Coverage report will be in ./target/jacoco-coverage/index.html
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Base64DiffApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Base64DiffAppIntTest {

    @LocalServerPort
    private int port;

    /**
     * The Rest template.
     */
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    /**
     * The HTTP Headers to be sent on the REST call
     */
    private final HttpHeaders headers = new HttpHeaders();

    /**
     * Controller to whom SpringBoot will assign the context;
     */
    @Autowired
    private Base64DiffController base64DiffController;

    /**
     * Sanity check to see if the context is being created.
     *
     * @throws Exception the exception
     */
    @Test
    public void contextLoads() throws  Exception {
        assertThat(base64DiffController).isNotNull();
    }

    /**
     * Creates the service URL using localhost and a dynamic port provided by Springboot
     *
     * @param uri the query parameters and endpoint to be used
     * @return the compound URI
     */
    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/v1/diff/" + uri;
    }

    /**
     * POST Operation to add valid data to a given side of the diff
     *
     * @param endpoint  the endpoint, left or right
     * @param id        the id
     * @param inputData the input data as a {@link org.juliazo.diff.model.Base64DataPayload}
     * @return the response of the request as a {@link org.juliazo.diff.model.Base64Data}
     */
    private ResponseEntity<Base64Data> addValidData(String endpoint, String id, Base64DataPayload inputData) {
        HttpEntity<Base64DataPayload> entity = new HttpEntity<>(inputData, headers);

        return restTemplate.exchange(
                createURLWithPort(id + endpoint),
                HttpMethod.POST, entity, Base64Data.class);
    }

    /**
     * POST Operation to add invalid data to a given side of the diff
     *
     * @param endpoint  the endpoint, left or right
     * @param id        the id
     * @param inputData the input data as a {@link org.juliazo.diff.model.Base64DataPayload}
     * @return the response of the request as a {@link org.juliazo.diff.model.ErrorPayload}
     */
    private ResponseEntity<ErrorPayload> addInvalidData(String endpoint, String id, Base64DataPayload inputData) {
        HttpEntity<Base64DataPayload> entity = new HttpEntity<>(inputData, headers);

        return restTemplate.exchange(
                createURLWithPort(id + endpoint),
                HttpMethod.POST, entity, ErrorPayload.class);
    }

    /**
     * GET Operation to get error responses from /diff endpoint
     *
     * @param id        the id
     * @return the response of the request as a {@link org.juliazo.diff.model.ErrorPayload}
     */
    private ResponseEntity<ErrorPayload> getInvalidDiff(String id) {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        return restTemplate.exchange(
                createURLWithPort(id),
                HttpMethod.GET, entity, ErrorPayload.class);
    }

    /**
     * GET Operation to get valid responses from /diff endpoint
     *
     * @param id        the id
     * @return the response of the request as a {@link org.juliazo.diff.model.DiffResult}
     */
    private ResponseEntity<DiffResult> getValidDiff(String id) {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        return restTemplate.exchange(
                createURLWithPort(id),
                HttpMethod.GET, entity, DiffResult.class);
    }

    /**
     * POST Operation to add valid data to both left and right side of the diff
     *
     * @param id        the id
     * @param leftData  the left data as {@link java.lang.String}
     * @param rightData the right data as {@link java.lang.String}
     */
    private void addValidDataBothSides(String id, String leftData, String rightData) {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData(leftData);

        ResponseEntity<Base64Data> leftResponse = addValidData("/left", id, inputData);
        assertEquals(HttpStatus.CREATED, leftResponse.getStatusCode());

        inputData.setData(rightData);
        ResponseEntity<Base64Data> rightResponse = addValidData("/right", id, inputData);
        assertEquals(HttpStatus.CREATED, rightResponse.getStatusCode());

        Base64Data expectedData = new Base64Data(id, leftData, rightData);
        assertEquals(expectedData.getLeftData(), rightResponse.getBody().getLeftData());
        assertEquals(expectedData.getRightData(), rightResponse.getBody().getRightData());
    }

    @Test
    public void testAddValidLeftData() {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData("Ym9saW5oYQ==");

        String id = String.valueOf(nextInt());

        ResponseEntity<Base64Data> response = addValidData("/left", id, inputData);

        Base64Data expected = new Base64Data(id, inputData.getData(), "");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected.getId(), response.getBody().getId());
        assertEquals(expected.getLeftData(), response.getBody().getLeftData());
        assertEquals(expected.getRightData(), response.getBody().getRightData());
    }

    @Test
    public void testUpdateValidLeftData() {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData("Ym9saW5oYQ==");

        String id = String.valueOf(nextInt());

        ResponseEntity<Base64Data> response = addValidData("/left", id, inputData);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(inputData.getData(), response.getBody().getLeftData());

        inputData.setData("eHV4dQ==");
        response = addValidData("/left", id, inputData);

        Base64Data expected = new Base64Data(id, inputData.getData(), "");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected.getId(), response.getBody().getId());
        assertEquals(expected.getLeftData(), response.getBody().getLeftData());
        assertEquals(expected.getRightData(), response.getBody().getRightData());
    }

    @Test
    public void testAddEmptyLeftData() {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData("");

        String id = String.valueOf(nextInt());

        ResponseEntity<ErrorPayload> response = addInvalidData("/left", id, inputData);

        ErrorPayload expected = new ErrorPayload();
        expected.setErrorCode("400");
        expected.setMessage("Field data is required");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expected.getErrorCode(), response.getBody().getErrorCode());
        assertEquals(expected.getMessage(), response.getBody().getMessage());
    }

    @Test
    public void testAddInvalidLeftData() {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData("eHV4dQ\\==");

        String id = String.valueOf(nextInt());

        ResponseEntity<ErrorPayload> response = addInvalidData("/left", id, inputData);

        ErrorPayload expected = new ErrorPayload();
        expected.setErrorCode("400");
        expected.setMessage("Input must use valid Base64 characters");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expected.getErrorCode(), response.getBody().getErrorCode());
        assertEquals(expected.getMessage(), response.getBody().getMessage());
    }

    @Test
    public void testAddValidRightData() {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData("dGVsZXR1Ymll");

        String id = String.valueOf(nextInt());

        ResponseEntity<Base64Data> response = addValidData("/right", id, inputData);

        Base64Data expected = new Base64Data(id, "", inputData.getData());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected.getId(), response.getBody().getId());
        assertEquals(expected.getRightData(), response.getBody().getRightData());
        assertEquals(expected.getLeftData(), response.getBody().getLeftData());
    }

    @Test
    public void testUpdateValidRightData() {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData("Ym9saW5oYQ==");

        String id = String.valueOf(nextInt());

        ResponseEntity<Base64Data> response = addValidData("/right", id, inputData);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(inputData.getData(), response.getBody().getRightData());

        inputData.setData("eHV4dQ==");
        response = addValidData("/right", id, inputData);

        Base64Data expected = new Base64Data(id, "", inputData.getData());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected.getId(), response.getBody().getId());
        assertEquals(expected.getRightData(), response.getBody().getRightData());
        assertEquals(expected.getLeftData(), response.getBody().getLeftData());
    }

    @Test
    public void testAddEmptyRightData() {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData("");

        String id = String.valueOf(nextInt());

        ResponseEntity<ErrorPayload> response = addInvalidData("/right", id, inputData);

        ErrorPayload expected = new ErrorPayload();
        expected.setErrorCode("400");
        expected.setMessage("Field data is required");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expected.getErrorCode(), response.getBody().getErrorCode());
        assertEquals(expected.getMessage(), response.getBody().getMessage());
    }

    @Test
    public void testAddInvalidRightData() {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData("eHV4dQ\\==");

        String id = String.valueOf(nextInt());

        ResponseEntity<ErrorPayload> response = addInvalidData("/right", id, inputData);

        ErrorPayload expected = new ErrorPayload();
        expected.setErrorCode("400");
        expected.setMessage("Input must use valid Base64 characters");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expected.getErrorCode(), response.getBody().getErrorCode());
        assertEquals(expected.getMessage(), response.getBody().getMessage());
    }

    @Test
    public void testAddValidLeftAndRightData() {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData("dGVsZXR1Ymll");

        String id = String.valueOf(nextInt());

        ResponseEntity<Base64Data> response = addValidData("/left", id, inputData);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Base64Data expected = new Base64Data(id, inputData.getData(), "");

        assertEquals(expected.getLeftData(), response.getBody().getLeftData());
        assertTrue(response.getBody().getRightData().isEmpty());

        expected.setRightData(inputData.getData());

        response = addValidData("/right", id, inputData);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected.getId(), response.getBody().getId());
        assertEquals(expected.getRightData(), response.getBody().getRightData());
        assertEquals(expected.getLeftData(), response.getBody().getLeftData());
    }

    @Test
    public void testAddValidRightAndLeftData() {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData("dGVsZXR1Ymll");

        String id = String.valueOf(nextInt());

        ResponseEntity<Base64Data> response = addValidData("/right", id, inputData);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Base64Data expected = new Base64Data(id, "", inputData.getData());

        assertEquals(expected.getRightData(), response.getBody().getRightData());
        assertTrue(response.getBody().getLeftData().isEmpty());

        expected.setLeftData(inputData.getData());

        response = addValidData("/left", id, inputData);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected.getId(), response.getBody().getId());
        assertEquals(expected.getLeftData(), response.getBody().getLeftData());
        assertEquals(expected.getRightData(), response.getBody().getRightData());
    }

    @Test
    public void testGetDiffResultNonExistentId() {
        String id = String.valueOf(nextInt());
        ResponseEntity<ErrorPayload> response = getInvalidDiff(id);

        ErrorPayload expected = new ErrorPayload();
        expected.setErrorCode("404");
        expected.setMessage("Data not Found");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expected.getErrorCode(), response.getBody().getErrorCode());
        assertEquals(expected.getMessage(), response.getBody().getMessage());
    }

    @Test
    public void testGetDiffResultMissingLeftData() {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData("dGVsZXR1Ymll");

        String id = String.valueOf(nextInt());

        ResponseEntity<Base64Data> actual = addValidData("/right", id, inputData);
        assertEquals(HttpStatus.CREATED, actual.getStatusCode());

        Base64Data expectedData = new Base64Data(id, "", inputData.getData());
        assertEquals(expectedData.getRightData(), actual.getBody().getRightData());
        assertTrue(actual.getBody().getLeftData().isEmpty());

        ResponseEntity<ErrorPayload> response = getInvalidDiff(id);

        ErrorPayload expected = new ErrorPayload();
        expected.setErrorCode("400");
        expected.setMessage("Missing Left data");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expected.getErrorCode(), response.getBody().getErrorCode());
        assertEquals(expected.getMessage(), response.getBody().getMessage());
    }

    @Test
    public void testGetDiffResultMissingRightData() {
        Base64DataPayload inputData = new Base64DataPayload();
        inputData.setData("dGVsZXR1Ymll");

        String id = String.valueOf(nextInt());

        ResponseEntity<Base64Data> actual = addValidData("/left", id, inputData);
        assertEquals(HttpStatus.CREATED, actual.getStatusCode());

        Base64Data expectedData = new Base64Data(id, inputData.getData(), "");
        assertEquals(expectedData.getLeftData(), actual.getBody().getLeftData());
        assertTrue(actual.getBody().getRightData().isEmpty());

        ResponseEntity<ErrorPayload> response = getInvalidDiff(id);

        ErrorPayload expected = new ErrorPayload();
        expected.setErrorCode("400");
        expected.setMessage("Missing Right data");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expected.getErrorCode(), response.getBody().getErrorCode());
        assertEquals(expected.getMessage(), response.getBody().getMessage());
    }

    @Test
    public void testGetDiffResultDifferentSizeData() {
        String id = String.valueOf(nextInt());
        addValidDataBothSides(id, "dGVsZXR1Ymll", "dGVsZXR1");

        ResponseEntity<DiffResult> response = getValidDiff(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody().getId());
        assertFalse(response.getBody().isEqualSize());
        assertNull(response.getBody().isEquals());
        assertNull(response.getBody().getDifferences());
    }

    @Test
    public void testGetDiffResultEqualData() {
        String id = String.valueOf(nextInt());
        addValidDataBothSides(id, "dGVsZXR1Ymll", "dGVsZXR1Ymll");

        ResponseEntity<DiffResult> response = getValidDiff(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody().getId());
        assertTrue(response.getBody().isEqualSize());
        assertTrue(response.getBody().isEquals());
        assertNull(response.getBody().getDifferences());
    }

    @Test
    public void testGetDiffResultDiffFirstByte() {
        String id = String.valueOf(nextInt());
        addValidDataBothSides(id, "dGVsZXR1Ymll", "ZGVsZXR1Ymll");

        ResponseEntity<DiffResult> response = getValidDiff(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody().getId());
        assertTrue(response.getBody().isEqualSize());
        assertFalse(response.getBody().isEquals());
        assertEquals(1, response.getBody().getDifferences().size());
        assertEquals(0, response.getBody().getDifferences().get(0).getOffset());
        assertEquals(1, response.getBody().getDifferences().get(0).getLength());
    }

    @Test
    public void testGetDiffResultDiffLastByte() {
        String id = String.valueOf(nextInt());
        addValidDataBothSides(id, "dGVsZXR1Ymll", "dGVsZXR1Ymlv");

        ResponseEntity<DiffResult> response = getValidDiff(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody().getId());
        assertTrue(response.getBody().isEqualSize());
        assertFalse(response.getBody().isEquals());
        assertEquals(1, response.getBody().getDifferences().size());
        assertEquals(8, response.getBody().getDifferences().get(0).getOffset());
        assertEquals(1, response.getBody().getDifferences().get(0).getLength());
    }

    @Test
    public void testGetDiffResultDiffMiddleByte() {
        String id = String.valueOf(nextInt());
        addValidDataBothSides(id, "dGVsZXB1Ymll", "dGVsZXR1Ymll");

        ResponseEntity<DiffResult> response = getValidDiff(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody().getId());
        assertTrue(response.getBody().isEqualSize());
        assertFalse(response.getBody().isEquals());
        assertEquals(1, response.getBody().getDifferences().size());
        assertEquals(4, response.getBody().getDifferences().get(0).getOffset());
        assertEquals(1, response.getBody().getDifferences().get(0).getLength());
    }

    @Test
    public void testGetDiffResultDiffBigLength() {
        String id = String.valueOf(nextInt());
        addValidDataBothSides(id, "dGVsZXR1Ymll", "dGVsZWNvdGll");

        ResponseEntity<DiffResult> response = getValidDiff(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody().getId());
        assertTrue(response.getBody().isEqualSize());
        assertFalse(response.getBody().isEquals());
        assertEquals(1, response.getBody().getDifferences().size());
        assertEquals(4, response.getBody().getDifferences().get(0).getOffset());
        assertEquals(3, response.getBody().getDifferences().get(0).getLength());
    }

    @Test
    public void testGetDiffResultDiffMultipleOffsets() {
        String id = String.valueOf(nextInt());
        addValidDataBothSides(id, "dG9sZXRpYmV1", "dGVsZXR1Ymll");

        ResponseEntity<DiffResult> response = getValidDiff(id);

        DiffResult expected = new DiffResult();
        expected.setId(id);
        expected.setEqualSize(true);
        expected.setEquals(Boolean.FALSE);
        List<DiffBytes> diffBytesList = new ArrayList<>();

        DiffBytes diffBytes1 = new DiffBytes();
        diffBytes1.setOffset(1);
        diffBytes1.setLength(1);
        diffBytesList.add(diffBytes1);

        DiffBytes diffBytes2 = new DiffBytes();
        diffBytes2.setOffset(5);
        diffBytes2.setLength(1);
        diffBytesList.add(diffBytes2);

        DiffBytes diffBytes3 = new DiffBytes();
        diffBytes3.setOffset(7);
        diffBytes3.setLength(2);
        diffBytesList.add(diffBytes3);


        expected.setDifferences(diffBytesList);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody().getId());
        assertTrue(response.getBody().isEqualSize());
        assertFalse(response.getBody().isEquals());
        assertEquals(3, response.getBody().getDifferences().size());
        assertEquals(diffBytes1.getOffset(), response.getBody().getDifferences().get(0).getOffset());
        assertEquals(diffBytes1.getLength(), response.getBody().getDifferences().get(0).getLength());
        assertEquals(diffBytes2.getOffset(), response.getBody().getDifferences().get(1).getOffset());
        assertEquals(diffBytes2.getLength(), response.getBody().getDifferences().get(1).getLength());
        assertEquals(diffBytes3.getOffset(), response.getBody().getDifferences().get(2).getOffset());
        assertEquals(diffBytes3.getLength(), response.getBody().getDifferences().get(2).getLength());
    }
}
