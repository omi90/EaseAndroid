package com.ultimo.formvalidation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
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
    private boolean toStoreInSharedPred = false;

    public Validator(Context ctx){
        this.context = ctx;
    }
    public ValidateInterface getmValidateInterface() {
        return mValidateInterface;
    }

    public boolean isToStoreInSharedPred() {
        return toStoreInSharedPred;
    }

    public void setToStoreInSharedPred(boolean toStoreInSharedPred) {
        this.toStoreInSharedPred = toStoreInSharedPred;
    }
    public void setmValidateInterface(ValidateInterface mValidateInterface) {
        this.mValidateInterface = mValidateInterface;
    }
    class HolderClass{
        private String url;
        private List<String[]> params;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<String[]> getParams() {
            return params;
        }

        public void setParams(List<String[]> params) {
            this.params = params;
        }
    }
    class BackgroundTask extends AsyncTask<HolderClass,Void,String>{

        @Override
        protected String doInBackground(HolderClass... params) {
            String response = HTTPLoader.loadContentFromURL(params[0].getUrl(), params[0].getParams(),context);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            mValidateInterface.isSubmitted(response);
        }
    }
    private ValidateInterface mValidateInterface;

    public void validateAndSubmit(final ViewGroup viewGroup, final String urlString, final List<String[]> customPostVars){
        if (validateAllFields(viewGroup)){
            if (toStoreInSharedPred){
                storeInSharedPref();
            }
            //Log.i("Vijay", "Form Validated");
            mValidateInterface.isValidated(true);
            HolderClass holderClass = new HolderClass();
            holderClass.setUrl(urlString);
            holderClass.setParams(getURLPostVars(customPostVars));
            new BackgroundTask().execute(holderClass);
        }else {
            mValidateInterface.isValidated(false);
        }
    }
    public void submit(final String urlString, final List<String[]> customPostVars){
        if (toStoreInSharedPred){
            storeInSharedPref();
        }
        HolderClass holderClass = new HolderClass();
        holderClass.setUrl(urlString);
        holderClass.setParams(customPostVars);
        new BackgroundTask().execute(holderClass);
    }
    private SparseArray<View> array = new SparseArray<View>();
    public boolean validateAllFields(ViewGroup viewGroup){
        array = new SparseArray<View>();
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
    public void clearForm(ViewGroup viewGroup){
        array = new SparseArray<View>();
        findAllEdittexts(viewGroup);
        for (int i=0;i<array.size();i++) {
            int key = array.keyAt(i);
            // get the object by the key.
            Object obj = array.get(key);
            View view = (View) obj;
            if (view instanceof EditText) {
                final EditText editText = (EditText) view;
                editText.setText("");
            }
        }
    }
    private List<String[]> getURLPostVars(List<String[]> custom) {
        List<String[]> listOfParams = new ArrayList<String[]>();
        if (custom!=null && custom.size()>0){
            listOfParams.addAll(custom);
        }
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
                    listOfParams.add(new String[]{httpparam, value});

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
    private void loadFromSharedPref(ViewGroup viewGroup){
        findAllEdittexts(viewGroup);

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
