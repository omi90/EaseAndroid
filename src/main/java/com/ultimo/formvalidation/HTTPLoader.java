package com.ultimo.formvalidation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by vjprakash on 22/07/15.
 */
public class HTTPLoader {
    private static final String USER_AGENT = "Mozilla/5.0";

    public static String loadContentFromURL(String urlString,List<String[]> postVars){
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
                System.out.println("Input : " + inputLine);
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
    public static String loadContentFromURLGet(String urlString,List<String[]> postVars){
        try {
            String urlParameters = "";
            for (String[] var:postVars){
                urlParameters += var[0]+"="+var[1]+"&";
            }
            if (urlParameters.length()>1) {
                urlParameters = urlParameters.substring(0, urlParameters.length() - 1);
            }
            URL url = new URL(urlString+"?"+urlParameters);
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
        } catch (IOException e) {
            e.printStackTrace();
            return "{'error':'No Internet connection!'}";
        }
    }
}
