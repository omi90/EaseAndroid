package com.ultimo.formvalidation;

/**
 * Created by vjprakash on 12/08/15.
 */
public class ValidationTypeNotSupported extends Exception {
    public ValidationTypeNotSupported(){
        super("Validation type not supported");
    }
    public ValidationTypeNotSupported(String detailMessage) {
        super(detailMessage);
    }
}
