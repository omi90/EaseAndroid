package com.ultimo.formvalidation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vjprakash on 12/08/15.
 */
public class Validator {
    private Context context;
    public Validator(Context ctx){
        this.context = ctx;
    }
    public ValidateInterface getmValidateInterface() {
        return mValidateInterface;
    }

    public void setmValidateInterface(ValidateInterface mValidateInterface) {
        this.mValidateInterface = mValidateInterface;
    }

    private ValidateInterface mValidateInterface;

    public void validateAndSubmit(final ViewGroup viewGroup, final String urlString){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (validateAllFields(viewGroup)){
                    Log.i("Vijay", "Form Validated");
                    String response = HTTPLoader.loadContentFromURL(urlString, getURLPostVars());
                    mValidateInterface.isSubmitted(response);
                }else {
                    mValidateInterface.isValidated(false);
                }
            }
        });
        t.start();
    }
    private SparseArray<View> array = new SparseArray<View>();
    public boolean validateAllFields(ViewGroup viewGroup){
        findAllEdittexts(viewGroup);

        boolean formValidated = true;
        for (int i=0;i<array.size();i++){
            int key = array.keyAt(i);
            // get the object by the key.
            Object obj = array.get(key);
            View view = (View)obj;
            if (view instanceof EditText){
                final EditText editText = (EditText)view;
                String tag = editText.getTag().toString();
                String value = editText.getEditableText().toString();
                try {
                    String validatePattern = tag.substring(tag.indexOf(Constants.VALIDATE_START) + Constants.VALIDATE_START.length(), tag.indexOf(Constants.VALIDATE_END));
                    final String retMsg = ValidationLogic.checkValidation(value, validatePattern);
                    if (retMsg!=null) {
                        if (formValidated){
                            editText.post(new Runnable() {
                                @Override
                                public void run() {
                                    editText.requestFocus();
                                }
                            });
                        }
                        formValidated = false;
                        editText.post(new Runnable() {
                            @Override
                            public void run() {
                                editText.setError(retMsg);
                            }
                        });
                    }else {
                        //editText.setError("");
                    }
                } catch (ValidationTypeNotSupported validationTypeNotSupported) {
                    validationTypeNotSupported.printStackTrace();
                }
            }
            //System.out.println(editText.getTag());
            //System.out.println((editText.getEditableText().toString()));
        }
        return formValidated;
    }
    private void findAllEdittexts(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup)
                findAllEdittexts((ViewGroup) view);
            else if (view instanceof Button){

            }
            else {
                array.put(view.getId(), view);
            }
        }
    }
    private List<String[]> getURLPostVars() {
        List<String[]> listOfParams = new ArrayList<String[]>();
        for (int i = 0; i < array.size(); i++) {
            int key = array.keyAt(i);
            // get the object by the key.
            Object obj = array.get(key);
            View view = (View) obj;
            if (view instanceof EditText) {
                final EditText editText = (EditText) view;
                String tag = editText.getTag().toString();
                String value = editText.getEditableText().toString();
                try {
                    String httpparam = tag.substring(tag.indexOf(Constants.HTTP_STR_START) + Constants.HTTP_STR_START.length(), tag.indexOf(Constants.HTTP_STR_END));
                    listOfParams.add(new String[]{httpparam,value});

                }catch (IndexOutOfBoundsException idxe){

                }
            }
        }
        return listOfParams;
    }
    private void storeInSharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < array.size(); i++) {
            int key = array.keyAt(i);
            // get the object by the key.
            Object obj = array.get(key);
            View view = (View) obj;
            if (view instanceof EditText) {
                final EditText editText = (EditText) view;
                String tag = editText.getTag().toString();
                String value = editText.getEditableText().toString();
                try {
                    String httpparam = tag.substring(tag.indexOf(Constants.HTTP_STR_START) + Constants.HTTP_STR_START.length(), tag.indexOf(Constants.HTTP_STR_END));
                    editor.putString(httpparam, value);

                }catch (IndexOutOfBoundsException idxe){

                }
            }
        }
        editor.commit();
    }
    private void loadFromSharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        for (int i = 0; i < array.size(); i++) {
            int key = array.keyAt(i);
            // get the object by the key.
            Object obj = array.get(key);
            View view = (View) obj;
            if (view instanceof EditText) {
                final EditText editText = (EditText) view;
                String tag = editText.getTag().toString();
                String value = editText.getEditableText().toString();
                try {
                    String httpparam = tag.substring(tag.indexOf(Constants.HTTP_STR_START) + Constants.HTTP_STR_START.length(), tag.indexOf(Constants.HTTP_STR_END));
                    editText.setText(sharedPreferences.getString(httpparam,""));
                }catch (IndexOutOfBoundsException idxe){

                }
            }
        }
    }
}
