package dev.spoocy.utils.common.exceptions;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class FileReadOnlyException extends RuntimeException {

    public FileReadOnlyException(String file) {
        super(String.format("File %s is read-only!", file));
    }

}
