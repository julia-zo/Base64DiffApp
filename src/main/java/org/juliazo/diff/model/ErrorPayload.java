package org.juliazo.diff.model;

/**
 * POJO class for errors found during regular execution of the code.
 */
public class ErrorPayload {

    /**
     * HTTP Status code for the error found
     */
    private String errorCode;

    /**
     * Error message explaining what went wrong
     */
    private String message;

    /**
     * Gets errorCode.
     *
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets errorCode.
     *
     * @param errorCode the errorCode
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
