package com.ultimo.listviewloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.ultimo.formvalidation.R;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by vjprakash on 02/09/15.
 */
public class CustomListViewAdapter extends BaseAdapter{
    private Context context;
    private List list;
    private Class aClass;
    private int errResId=0;
    private Loader loader;
    private String imageBaseURL;
    private int baseLayoutId=0;
    public CustomListViewAdapter(Context context,List list,int baseLayoutId,Class rIdClass,String imageBaseURL,Loader loader){
        this.context = context;
        this.list = list;
        this.baseLayoutId = baseLayoutId;
        this.aClass = rIdClass;
        this.imageBaseURL = imageBaseURL;
        this.loader = loader;
    }
    public CustomListViewAdapter(Context context,List list,int baseLayoutId,Class rIdClass,String imageBaseURL,Loader loader,int errorImageRes){
        this.context = context;
        this.list = list;
        this.baseLayoutId = baseLayoutId;
        this.aClass = rIdClass;
        this.imageBaseURL = imageBaseURL;
        this.loader = loader;
        this.errResId = errorImageRes;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(this.baseLayoutId, null);
        HashMap<String,String> map = (HashMap)list.get(position);
        Iterator<String> itr = map.keySet().iterator();
        while (itr.hasNext()){
            String key = itr.next();
            try {
                Field field = aClass.getField(key);
                int idValue = field.getInt(aClass);
                View view = convertView.findViewById(idValue);
                if (view==null){
                }
                else if (view instanceof TextView){
                    ((TextView) view).setText(map.get(key));
                }else if (view instanceof ImageView){
                    final ImageView imageView = ((ImageView) view);
                    String fullURL = map.get(key);
                    if (fullURL!=null && !fullURL.equalsIgnoreCase("")) {
                        if (this.imageBaseURL != null) {
                                fullURL = imageBaseURL + fullURL.replaceAll(" ","%20");
                                Log.i(CustomListViewAdapter.class.getCanonicalName(),fullURL);
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
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (isReachedEnd(position))
            try {
                this.loader.loadListViewNextPage();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return convertView;
    }
    public void addList(List list){
        this.list.addAll(list);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                CustomListViewAdapter.this.notifyDataSetChanged();
            }
        });
    }
    private boolean isReachedEnd(int position){
        if (this.list.size()-1 == position){
            return true;
        }
        return false;
    }
}
