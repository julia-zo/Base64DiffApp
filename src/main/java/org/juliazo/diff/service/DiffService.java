package org.juliazo.diff.service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.juliazo.diff.commons.exception.CompromisedDataException;
import org.juliazo.diff.model.Base64Data;
import org.juliazo.diff.model.DiffBytes;
import org.juliazo.diff.model.DiffResult;
import org.juliazo.diff.model.ErrorPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Business logic of the REST Service. This class holds the implementation of all
 * REST operations accepted by Base64 Diff Application.
 *
 * In case the application gets too big, validation and auxiliary methods can be moved
 * to a separated class and package to improve readability.
 */
@Service
public class DiffService {

    /**
     * The constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DiffService.class);

    /**
     * Diff Storage - In Memory
     * Map containing all data received via API.
     * Although data base storage was not required for this assignment,
     * it would be simple to modify this class to include it.
     */
    private Map<String, Base64Data>  diffStorage = new HashMap<>();

    /**
     * Process POST Request for including data on the Left side of the diff.
     * Validates input and determines whether the data is new of if is being updated.
     * At this moment, there was no requirement to make update requests explicit,
     * but doing so would increase the reliability of this solution.
     *
     * @param id        the id: unique identifier of this data set
     * @param data      encoded base 64 data to be included on this data set
     * @return the response entity containing the request payload in case of success or an error message
     */
    public ResponseEntity inputLeft (String id, String data) {
        ResponseEntity errorResponse = validateInput(data, id);
        if (errorResponse == null) {
            Base64Data base64Data = findId(id);
            if (base64Data == null || base64Data.getLeftData().isEmpty()) {
                logger.debug("Creating new Left data on id: " + id);
                String rightData = base64Data == null ? "" : base64Data.getRightData();
                base64Data = new Base64Data(id, data, rightData);
                diffStorage.put(id, base64Data);
                return new ResponseEntity(base64Data, HttpStatus.CREATED);
            } else {
                logger.debug("Updating Left data on id: " + id);
                base64Data.setLeftData(data);
                return new ResponseEntity(base64Data, HttpStatus.OK);
            }
        }
        return errorResponse;
    }

    /**
     * Process POST Request for including data on the Right side of the diff.
     * Validates input and determines whether the data is new of if is being updated.
     * At this moment, there was no requirement to make update requests explicit,
     * but doing so would increase the reliability of this solution.
     *
     * @param id        the id: unique identifier of this data set
     * @param data      encoded base 64 data to be included on this data set
     * @return the response entity containing the request payload in case of success or an error message
     */
    public ResponseEntity inputRight (String id, String data) {
        ResponseEntity errorResponse = validateInput(data, id);
        if (errorResponse == null) {
            Base64Data base64Data = findId(id);
            if (base64Data == null || base64Data.getRightData().isEmpty()) {
                logger.debug("Creating new Right data on id: " + id);
                String leftData = base64Data == null ? "" : base64Data.getLeftData();
                base64Data = new Base64Data(id, leftData, data);
                diffStorage.put(id, base64Data);
                return new ResponseEntity(base64Data, HttpStatus.CREATED);
            } else {
                logger.debug("Updating Right data on id: " + id);
                base64Data.setRightData(data);
                return new ResponseEntity(base64Data, HttpStatus.OK);
            }
        }
        return errorResponse;
    }

    /**
     * Validates if the input data not empty and is a valid Base64 data.
     * This validation is based on the Base64 alphabet.
     * If the data has characters outside of this alphabet,
     * it will be considered invalid.
     *
     * @param data      input data
     * @return whether or not the input data is not empty and a valid Base64 data
     */
    private ResponseEntity validateInput (String data, String id) {
        if (data == null || data.isEmpty()) {
            logger.info("Empty data on id: " + id);
            ErrorPayload errorPayload = new ErrorPayload();
            errorPayload.setErrorCode(HttpStatus.BAD_REQUEST.toString());
            errorPayload.setMessage("Field data is required");
            return new ResponseEntity(errorPayload, HttpStatus.BAD_REQUEST);
        }
        if (!Base64.isBase64(data)) {
            logger.info("Invalid Base64 data on id: " + id);
            ErrorPayload errorPayload = new ErrorPayload();
            errorPayload.setErrorCode(HttpStatus.BAD_REQUEST.toString());
            errorPayload.setMessage("Input must use valid Base64 characters");
            return new ResponseEntity(errorPayload, HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    /**
     * Searches the data Storage for an occurrence of the provided id.
     * Checks for miss matches on the data storage index and the id recorded on the data set.
     * If no data set is found, returns null.
     *
     * @param id    the unique identifier of a data set
     * @return the {@link org.juliazo.diff.model.Base64Data} related to given id
     */
    private Base64Data findId (String id) {
        Base64Data base64Data = diffStorage.get(id);
        if (base64Data != null && !base64Data.getId().equals(id)) {
            String errorMessage = "FATAL ERROR: Provided id and stored id do not match: " + id + " - " + base64Data.getId();
            logger.error(errorMessage);
            throw new CompromisedDataException(new Exception(errorMessage));
        }
        return base64Data;
    }

    /**
     * Process a GET request on a given id, making a diff of the data stored on the
     * {@link org.juliazo.diff.model.Base64Data} linked to such id.
     *
     * Validates if there are both Left and Right data for the provided id, returns an error
     * message indicating which side is missing or returns not found if both sides are missing.
     *
     * Order of checks: Verify if size is equal; Verify if content is equal; Evaluate how many
     * and where are located the bytes that are not equal on both sides of the diff.
     *
     * The diff consists of the offset of the byte that is different on both sides of the diff
     * and the length of the difference, meaning how many bytes are different starting on the
     * one marked by the offset.
     *
     * @param id    the unique identifier of a data set
     * @return the {@link org.juliazo.diff.model.DiffResult} with the result of the diff operation
     */
    public ResponseEntity getDiffResult (String id) {

        Base64Data base64Data = findId(id);
        if (base64Data != null) {
            logger.debug("Data found, performing diff operation on id: " + id);

            String right = base64Data.getRightData();
            String left = base64Data.getLeftData();

            if (right.isEmpty()) {
                logger.debug("Right Data not found for id: " + id);
                ErrorPayload errorPayload = new ErrorPayload();
                errorPayload.setErrorCode(HttpStatus.BAD_REQUEST.toString());
                errorPayload.setMessage("Missing Right data");
                return new ResponseEntity(errorPayload, HttpStatus.BAD_REQUEST);
            }
            if (left.isEmpty()) {
                logger.debug("Left Data not found for id: " + id);
                ErrorPayload errorPayload = new ErrorPayload();
                errorPayload.setErrorCode(HttpStatus.BAD_REQUEST.toString());
                errorPayload.setMessage("Missing Left data");
                return new ResponseEntity(errorPayload, HttpStatus.BAD_REQUEST);
            }

            byte [] rightBytes = Base64.decodeBase64(right);
            byte [] leftBytes = Base64.decodeBase64(left);

            DiffResult diffResult = new DiffResult();
            diffResult.setId(id);

            //right side and left side can only be equal if they have the same size
            diffResult.setEqualSize(rightBytes.length == leftBytes.length);

            ResponseEntity response = new ResponseEntity(diffResult, HttpStatus.OK);

            if (!diffResult.isEqualSize()) {
                //Stop diff operation in case of different sizes to reduce response time.
                //The reduction on response time might be more noticeable when using large input data.
                logger.debug("Input data is not of the same size for id: " + id);
                return response;
            }

            diffResult.setEquals(Arrays.equals(rightBytes,leftBytes));

            if (diffResult.isEquals()) {
                //Stop diff operation in case of equal content to reduce response time.
                //The reduction on response time might be more noticeable when using large input data.
                logger.debug("Input data is equal for id: " + id);
                return response;
            }

            List<DiffBytes> differences = new ArrayList<>();

            int offset = -1;
            int length = 0;
            for (int i=0; i < rightBytes.length; i++) {

                if (rightBytes[i] != leftBytes[i]) {
                    //when the bytes are different
                    if (offset < 0) {
                        //starts a new sequence of different bytes
                        logger.debug("Found difference on index: " + i);
                        offset = i;
                    }
                    //increase the number of different bytes on this sequence
                    length++;
                } else {
                    if (offset < 0) {
                        //if the bytes are equal and no difference has been found yet
                        //continue to scan the array
                        continue;
                    }
                    //if the bytes are equal but there is an active sequence of different bytes,
                    //end this sequence and include it on the List of differences on diffResult.
                    logger.debug("Mounting DiffBytes object for offset: " + offset);
                    DiffBytes diffBytes = new DiffBytes();
                    diffBytes.setOffset(offset);
                    diffBytes.setLength(length);

                    //reset values to allow the start of a new sequence
                    offset = -1;
                    length = 0;

                    differences.add(diffBytes);
                }
            }
            //end the sequence when the last byte of the array is a different one.
            if (offset >= 0) {
                logger.debug("Mounting DiffBytes object for offset: " + offset);
                DiffBytes diffBytes = new DiffBytes();
                diffBytes.setOffset(offset);
                diffBytes.setLength(length);

                differences.add(diffBytes);
            }

            diffResult.setDifferences(differences);

            return response;
        }
        logger.info("Data not found for id: " + id);
        ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.setErrorCode(HttpStatus.NOT_FOUND.toString());
        errorPayload.setMessage("Data not Found");
        return  new ResponseEntity(errorPayload, HttpStatus.NOT_FOUND);
    }
}
