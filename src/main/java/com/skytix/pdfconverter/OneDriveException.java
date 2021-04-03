package com.skytix.pdfconverter;

public class OneDriveException extends Exception {
    public OneDriveException() {
    }

    public OneDriveException(String message) {
        super(message);
    }

    public OneDriveException(String message, Throwable cause) {
        super(message, cause);
    }

    public OneDriveException(Throwable cause) {
        super(cause);
    }

    public OneDriveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
