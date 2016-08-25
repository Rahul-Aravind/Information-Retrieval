package com.ir.searchengine.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ir.searchengine.vos.Result;

public class BingSearch {
    /**
     * @param args
     */
    public List<QueryResultStats> getBingResults(String searchText) {

    	
        searchText = searchText.replaceAll(" ", "%20");
        List<QueryResultStats> results = new ArrayList<QueryResultStats>();
        String accountKey="NkGfR6GNm1J7wTMoO0q94vTpIFbZQKd/Zy1JQ9kxJV8";
        byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
        String accountKeyEnc = new String(accountKeyBytes);
        URL url;
        try {
            url = new URL(  
                    "https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Web?Query=%27" + searchText + "%27&$top=10&$format=json");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        char[] buffer = new char[4096];
        while ((output = br.readLine()) != null) {
            sb.append(output);
        } 

        conn.disconnect(); 
        
        System.out.println(sb.toString());
        
        JSONObject obj = new JSONObject(sb.toString());

        JSONArray arr = obj.getJSONObject("d").getJSONArray("results");
        for (int i = 0; i < arr.length(); i++)
        {
            String displayURL = arr.getJSONObject(i).getString("DisplayUrl");
            String title = arr.getJSONObject(i).getString("Title");
            String content = arr.getJSONObject(i).getString("Description");		
            QueryResultStats result = new QueryResultStats(displayURL, title, content);
        	results.add(result);
        }
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return results;
    }        
}