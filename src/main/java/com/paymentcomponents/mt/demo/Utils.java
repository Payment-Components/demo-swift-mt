package com.paymentcomponents.mt.demo;

import gr.datamation.mt.validator.SwiftValidObj;

public class Utils {
    public static void printErrors(SwiftValidObj swiftValidObj) {
        swiftValidObj.getValidationErrorList().stream().forEach(error -> {
            System.err.println(
                    "Error Code: " + error.getErrorCode() + "\n" +
                            "Error Description: " + error.getDescription() + "\n" +
                            "Tag in error: " + error.getTagName() + "\n" +
                            "Line number in error inside the tag: " + error.getLine() + "\n" +
                            "Ocurrency: " + error.getOccurs() //In case the tag is repeated in the message, the occurs property contains the occurency in error
            );
        });
    }

}
