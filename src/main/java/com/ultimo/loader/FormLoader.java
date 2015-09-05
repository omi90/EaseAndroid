package com.ultimo.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.ultimo.formvalidation.HTTPLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vjprakash on 05/09/15.
 */
public class FormLoader {
    private String urlString;
    private String imageBaseURL;
    private int errResId=0;
    private Context context;

    public int getSpinnerResItem() {
        return spinnerResItem;
    }

    public void setSpinnerResItem(int spinnerResItem) {
        this.spinnerResItem = spinnerResItem;
    }

    private int spinnerResItem;
    public FormLoader(Context context){
        this.context = context;
    }
    public void loadFromURL(ViewGroup containerFormView,String urlString,String imageBaseURL,List<String[]> postParams,Class idClass,int errorImageRes) throws JSONException {
        this.urlString = urlString;
        this.imageBaseURL= imageBaseURL;
        this.errResId = errorImageRes;
        String response = HTTPLoader.loadContentFromURLGet(this.urlString, postParams);
        JSONObject jsonObject = new JSONObject(response);
        Iterator<String> itr = jsonObject.keys();
        while (itr.hasNext()){
            String key = itr.next();
            try {
                Field field = idClass.getField(key);
                int idValue= field.getInt(idClass);
                View view = containerFormView.findViewById(idValue);
                if (view==null){

                }else if (view instanceof EditText){
                    EditText editText = (EditText)view;
                    editText.setText(jsonObject.getString(key));
                }else if (view instanceof ImageView){
                    final ImageView imageView = ((ImageView) view);
                    String fullURL = jsonObject.getString(key);
                    if (fullURL!=null && !fullURL.equalsIgnoreCase("")) {
                        if (this.imageBaseURL != null) {
                            fullURL = imageBaseURL + fullURL.replaceAll(" ","%20");
                            Log.i(CustomListViewAdapter.class.getCanonicalName(), fullURL);
                        }
                        ImageRequest request = new ImageRequest(fullURL, new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                imageView.setImageBitmap(response);
                            }
                        }, 0, 0, null,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (errResId != 0) {
                                            imageView.setImageResource(errResId);
                                        }
                                    }
                                });
                        VolleySingleton.getInstance(context).addToRequestQueue(request);
                    }else {

                    }
                }else if (view instanceof Spinner){
                    Spinner spinner = (Spinner)view;
                    SpinnerAdapter spinnerAdapter = spinner.getAdapter();
                    JSONArray jsonArray = jsonObject.getJSONArray(key);
                    List<String> list = new ArrayList<>();
                    for (int i=0;i<jsonArray.length();i++){
                        list.add(jsonArray.getString(i));
                    }
                    ArrayAdapter adapter;
                    if (this.spinnerResItem==0)
                        adapter = new ArrayAdapter<String>(this.context,android.R.layout.simple_spinner_dropdown_item,list);
                    else
                        adapter = new ArrayAdapter<String>(this.context,this.spinnerResItem,list);
                    spinner.setAdapter(adapter);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        }
    }
}
