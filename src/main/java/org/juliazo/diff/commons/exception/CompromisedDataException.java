package org.juliazo.diff.commons.exception;

/**
 * Compromised Data Exception: Occurs when the id used as key on the data storage
 * does not match the id inside the data structure {@link org.juliazo.diff.model.Base64Data}.
 *
 * This indicates that the data might have been tempered with and the execution must not proceed.
 */
public class CompromisedDataException extends RuntimeException{


    /**
     * Instantiates a new Compromised data exception.
     *
     * @param throwable the throwable
     */
    public CompromisedDataException(final Throwable throwable) {
        super("FATAL ERROR: Data storage has been compromised!", throwable);
    }
}
