package com.ultimo.formvalidation;

/**
 * Created by vjprakash on 12/08/15.
 */
class Constants{
    public static final String VALIDATE_START = "vs_";
    public static final String VALIDATE_END = "_ve_";
    public static final String HTTP_STR_START = "hts_";
    public static final String HTTP_STR_END = "_hte_";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String LENGTH = "length";
    public static final String NUMBER = "number";
    public static final String REGEX = "regex";

    public static final String EMAIL_ERROR="Please enter a valid email";
    public static final String PHONE_ERROR="Please enter a valid phone no";
    public static final String LENGTH1_ERROR="Please enter value greater than %n";
    public static final String LENGTH2_ERROR="Please enter value greater than %n and less than %n";
    public static final String NUMBER_ERROR="Please enter a number";
    public static final String REGEX_ERROR="Please enter a correct pattern value";
}
public class ValidationLogic {
    private static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static String phonePattern = "[0-9]{10}";
    public static String checkValidation(String stringToValidate,String validationPattern) throws ValidationTypeNotSupported {
        int nxtPos = validationPattern.indexOf("_");
        String retMsg="";
        boolean isValidated=false;
        String validationType = validationPattern;
        if (nxtPos > -1) {
            validationType = validationPattern.substring(0, nxtPos);
        }
        switch (validationType){
            case Constants.EMAIL:
                retMsg = Constants.EMAIL_ERROR;
                isValidated = stringToValidate.matches(emailPattern);
                break;
            case Constants.PHONE:
                retMsg = Constants.PHONE_ERROR;
                isValidated = stringToValidate.matches(phonePattern);
                break;
            case Constants.LENGTH:
                String[] patvals = validationPattern.split("_");
                if (patvals.length<2){
                    throw new ValidationTypeNotSupported();
                }else if (patvals.length<3){
                    retMsg = String.format(Constants.LENGTH1_ERROR,Integer.parseInt(patvals[1]));
                    if (stringToValidate.length()>=Integer.parseInt(patvals[1])){
                        isValidated = true;
                    }else {
                        isValidated = false;
                    }
                }else if (patvals.length<4){
                    retMsg = String.format(Constants.LENGTH2_ERROR,Integer.parseInt(patvals[1]),Integer.parseInt(patvals[2]));
                    if (stringToValidate.length()>=Integer.parseInt(patvals[1]) && stringToValidate.length()<=Integer.parseInt(patvals[2]) ){
                        isValidated = true;
                    }else {
                        isValidated = false;
                    }
                }
                break;
            case Constants.NUMBER:
                retMsg = Constants.NUMBER_ERROR;
                try {
                    Long.parseLong(stringToValidate);
                    isValidated = true;
                }catch (NumberFormatException nex){
                    isValidated = false;
                }
                break;
            case Constants.REGEX:
                retMsg = Constants.REGEX_ERROR;
                isValidated = stringToValidate.matches(validationPattern);
                break;
            default:
                throw new ValidationTypeNotSupported(validationType+"<>"+validationPattern);
        }
        if (isValidated){
            return null;
        }else {
            return retMsg;
        }
    }
}
