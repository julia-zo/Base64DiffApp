package org.juliazo.diff.controller;


import org.juliazo.diff.model.Base64Data;
import org.juliazo.diff.model.Base64DataPayload;
import org.juliazo.diff.model.DiffBytes;
import org.juliazo.diff.model.DiffResult;
import org.juliazo.diff.service.DiffService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Test class for the entry point of the rest application
 */
@RunWith(MockitoJUnitRunner.class)
public class Base64DiffControllerTest {

    /**
     * The controller responsible for handling REST requests
     */
    @InjectMocks
    private Base64DiffController base64DiffController;

    /**
     * The service that holds the entire functionality of the application
     */
    @Mock
    private DiffService diffService;

    /**
     * Test: Adds data to a new ID on /left endpoint
     */
    @Test
    public void testLeftInput () {
        Base64DataPayload leftPayload = new Base64DataPayload();
        leftPayload.setData("dGVsZXR1Ymll");
        String id = "1";

        ResponseEntity expected = new ResponseEntity (new Base64Data(id, leftPayload.getData(), ""), HttpStatus.CREATED);

        when(diffService.inputLeft(eq(id), any())).thenReturn(expected);

        ResponseEntity  actual = base64DiffController.inputLeft(id, leftPayload);

        assertEquals(expected, actual);
    }

    /**
     * Test: Adds data to a new ID on /right endpoint
     */
    @Test
    public void testRightInput () {
        Base64DataPayload rightPayload = new Base64DataPayload();
        rightPayload.setData("dGVsZXR1Ymll");
        String id = "1";

        ResponseEntity expected = new ResponseEntity (new Base64Data(id, "", rightPayload.getData()), HttpStatus.CREATED);

        when(diffService.inputRight(eq(id), any())).thenReturn(expected);

        ResponseEntity actual = base64DiffController.inputRight(id, rightPayload);

        assertEquals(expected, actual);
    }

    /**
     * Test: Gets a diff of the data from id X on /id endpoint
     */
    @Test
    public void testGetDiff () {
        String id = "1";
        DiffResult diffResult = new DiffResult();
        diffResult.setId(id);
        diffResult.setEquals(Boolean.FALSE);
        diffResult.setEqualSize(true);
        DiffBytes diffBytes = new DiffBytes();
        diffBytes.setOffset(0);
        diffBytes.setLength(1);
        List<DiffBytes> differences = new ArrayList<>();
        differences.add(diffBytes);
        diffResult.setDifferences(differences);

        ResponseEntity expected = new ResponseEntity (diffResult, HttpStatus.OK);

        when(diffService.getDiffResult(eq(id))).thenReturn(expected);

        ResponseEntity actual = base64DiffController.getDiff(id);

        assertEquals(expected, actual);
    }
}
