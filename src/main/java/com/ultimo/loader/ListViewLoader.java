package com.ultimo.loader;

import android.content.Context;
import android.os.Looper;
import android.widget.ListView;

import com.ultimo.formvalidation.HTTPLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vjprakash on 02/09/15.
 */
public class ListViewLoader {
    private boolean hasMoreToLoad = true;
    private Context context;
    private boolean isPaginate;
    private String pageParamName;
    private String urlString;
    private ListView listView;
    private Class rIdClass;
    private String imageBaseURL;
    private int curPage = 1;
    private List<String[]> postParams;
    private String jsonArrayName;
    public void setArrayName(String jsonArrayName){
        this.jsonArrayName = jsonArrayName;
    }
    private CustomListViewAdapter listViewAdapter;
    public ListViewLoader(Context ctx){
        this.context = ctx;
    }
    public void loadFromURL(String urlString,String imageBaseURL,List<String[]> postParams,ListView listView,int itemLayout,
                                    Class rIdClass,boolean isPaginate,String pageParamName) {
        this.listView = listView;
        this.imageBaseURL = imageBaseURL;
        this.urlString = urlString;
        this.pageParamName = pageParamName;
        this.isPaginate = isPaginate;
        this.rIdClass = rIdClass;
        this.postParams = postParams;
        List list = new ArrayList();
        this.listViewAdapter = new CustomListViewAdapter(this.context,list,itemLayout,this.rIdClass,this.imageBaseURL,ListViewLoader.this);
        this.listView.setAdapter(listViewAdapter);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    loadNextPage();
                    Looper.loop();
                }catch (JSONException jsx){
                    jsx.printStackTrace();
                }
            }
        });
        t.start();
    }
    public void loadNextPage() throws JSONException {
        if (curPage == 1 && !this.isPaginate){
            if (postParams==null)
                postParams = new ArrayList<String[]>();
            String response = HTTPLoader.loadContentFromURLGet(this.urlString, postParams,context);
            List list = new ArrayList();
            JSONArray jsonArray = new JSONArray(response);
            for (int i =0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Iterator<String> itr = jsonObject.keys();
                HashMap<String,String> mapObj = new HashMap<>();
                while (itr.hasNext()){
                    String key = itr.next();
                    mapObj.put(key,jsonObject.getString(key));
                }
                list.add(mapObj);
            }
            this.listViewAdapter.addList(list);
            curPage++;
        }
        else if (this.isPaginate && hasMoreToLoad){
            if (postParams==null)
                postParams = new ArrayList<String[]>();
            postParams.add(new String[]{pageParamName, ""+this.curPage});
            String response = HTTPLoader.loadContentFromURLGet(this.urlString, postParams,context);
            List list = new ArrayList();
            JSONArray jsonArray = null;
            if (jsonArrayName == null || jsonArrayName.equalsIgnoreCase("")){
                jsonArray = new JSONArray(response);
            }else {
                JSONObject jsonObject = new JSONObject(response);
                jsonArray = jsonObject.getJSONArray(jsonArrayName);
            }
            if(jsonArray.length()==0){
                hasMoreToLoad=false;
            }else {
                hasMoreToLoad = true;
            }
            for (int i =0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Iterator<String> itr = jsonObject.keys();
                HashMap<String,String> mapObj = new HashMap<>();
                while (itr.hasNext()){
                    String key = itr.next();
                    mapObj.put(key,jsonObject.getString(key));
                }
                list.add(mapObj);
            }
            this.listViewAdapter.addList(list);
            curPage++;
        }
    }
}
