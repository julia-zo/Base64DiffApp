package org.juliazo.diff.controller;

import org.juliazo.diff.model.Base64DataPayload;
import org.juliazo.diff.service.DiffService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Base64 Diff Controller. Responsible for handling HTTP requests.
 * All endpoints mapped to the application must be defined here.
 */
@RestController
@RequestMapping("/v1/diff/{id}")
public class Base64DiffController {

    /**
     * The Diff Service. Implementation of each endpoint mapped here.
     */
    private final DiffService diffService;

    /**
     * The constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Base64DiffController.class);

    /**
     * Instantiates a new Base 64 diff controller.
     *
     * @param diffService the diff service
     */
    @Autowired
    public Base64DiffController(DiffService diffService) {
        this.diffService = diffService;
    }

    /**
     * Endpoint POST for inputting Left Data into the Base64 Diff Application
     *
     * @param id                unique identifier, will be used to find the diff afterwards
     * @param base64DataPayload the request payload containing the encoded data for the Left side of the diff
     * @return the response entity containing the request payload in case of success or an error message
     */
    @RequestMapping(method = RequestMethod.POST, value = "/left", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity inputLeft(@PathVariable String id, @RequestBody Base64DataPayload base64DataPayload) {
        logger.info("Receiving Left Data for id: " + id);
        return diffService.inputLeft(id, base64DataPayload.getData());
    }

    /**
     * Endpoint POST for inputting Right Data into the Base64 Diff Application
     *
     * @param id                unique identifier, will be used to find the diff afterwards
     * @param base64DataPayload the request payload containing the encoded data for the Right side of the diff
     * @return the response entity containing the request payload in case of success or an error message
     */
    @RequestMapping(method = RequestMethod.POST, value = "/right", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity inputRight(@PathVariable String id, @RequestBody Base64DataPayload base64DataPayload) {
        logger.info("Receiving Right Data for id: " + id);
        return diffService.inputRight(id, base64DataPayload.getData());

    }

    /**
     * Endpoint GET: returns the resulting diff of the Left and Right data provided on the POST endpoints.
     *
     * @param id    unique identifier, must be the same for each side of the diff data
     * @return      the response entity containing a set of information
     *              on the diff operation {@link org.juliazo.diff.model.DiffResult}
     *              in case of success or containing an error message.
     */
    @RequestMapping(method = RequestMethod.GET, produces  = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getDiff(@PathVariable String id) {
        logger.info("Performing Diff operation on id: " + id);
        return diffService.getDiffResult(id);


    }



}
