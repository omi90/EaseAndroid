package com.ultimo.formvalidation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vjprakash on 22/07/15.
 */
public class HTTPLoader {
    private static String lineEnd = "\r\n";
    private static String twoHyphens = "--";
    private static String boundary = "*****";
    private static String Tag = "fSnd";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static boolean IS_COOKIE_STORE_LOADED = false;
    public static final String COOKIES_HEADER = "Set-Cookie";
    //private static CookieManager cookieManager = new CookieManager();
    public static void loadCookieStore(Context context){
        android.webkit.CookieSyncManager.createInstance(context);
        android.webkit.CookieManager.getInstance().setAcceptCookie(true);

        WebkitCookieManagerProxy cookieManager = new WebkitCookieManagerProxy(null, java.net.CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        IS_COOKIE_STORE_LOADED = true;
    }
    public static String loadContentFromURL(String urlString,List<String[]> postVars,Context context){
        if(!isNetworkConnected(context)){
            return "{'error':'No Internet connection!'}";
        }
        if (!IS_COOKIE_STORE_LOADED){
            loadCookieStore(context);
        }
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            String urlParameters = "";
            for (String[] var:postVars){
                urlParameters += var[0]+"="+var[1]+"&";
            }
            if (urlParameters.length()>1) {
                urlParameters = urlParameters.substring(0, urlParameters.length() - 1);
            }
            //String urlParameters = "latest_entry_id="+values[0]+"&updt_dt="+values[1];
            // Send post request
            con.setDoInput(true);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                //System.out.println("Input : " + inputLine);
            }
            in.close();
            //print result
            System.out.println("Response : " + response);
            return new String(response);
        } catch (IOException e) {
            e.printStackTrace();
            return "{'error':'No Internet connection!'}";
        }
    }
    public static String loadContentFromURLGet(String urlString,List<String[]> postVars,Context context){
        if(!isNetworkConnected(context)){
            return "{'error':'No Internet connection!'}";
        }
        if (!IS_COOKIE_STORE_LOADED){
            loadCookieStore(context);
        }
        try {
            String urlParameters = "";
            for (String[] var:postVars){
                urlParameters += var[0]+"="+var[1]+"&";
            }
            if (urlParameters.length()>1) {
                urlParameters = urlParameters.substring(0, urlParameters.length() - 1);
            }
            URL url;
            if (urlParameters.equalsIgnoreCase(""))
                url = new URL(urlString);
            else
                url = new URL(urlString+"?"+urlParameters);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //add reuqest header
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setDoInput(true);
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                System.out.println("Input : " + inputLine);
            }
            in.close();
            //print result
            System.out.println("Response : " + response);
            return new String(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "{'error':'No Internet connection!'}";
        }
    }

    public static String loadContentMultipart(String urlString,List<String[]> postVars,Context context) {
        if(!isNetworkConnected(context)){
            return "{'error':'No Internet connection!'}";
        }
        try{
            URL connectURL = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dos, "UTF-8"));

            for(String[] vars :postVars) {
                writer.write(twoHyphens + boundary + lineEnd);
                writer.write("Content-Disposition: form-data; name=\"" + vars[0] + "\"" + lineEnd);
                writer.write(lineEnd);
                writer.write(vars[1]);
                writer.write(lineEnd);
                writer.write(twoHyphens + boundary + lineEnd);
            }
            writer.flush();
            Log.i(Tag, "Sent, Response: " + String.valueOf(conn.getResponseCode()));
            InputStream is = conn.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            String s = b.toString();
            Log.i("Response", s);
            dos.close();
            return s;
        }catch (MalformedURLException ex) {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
            return "{'error':'No Internet connection!'}";
        }
        catch (IOException ioe) {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
            return "{'error':'No Internet connection!'}";
        }
    }
    public static boolean isNetworkConnected(Context context){
        ConnectivityManager connectivityManager =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        if (isConnected) {
            Log.i("NET", "connected" + isConnected);
            return true;
        }
        else {
            Log.i("NET", "not connected" + isConnected);
            return false;
        }
    }
}
