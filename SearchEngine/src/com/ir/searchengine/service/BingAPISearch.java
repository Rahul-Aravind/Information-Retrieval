package com.ir.searchengine.service;
import java.io.IOException;
import java.util.Base64;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

public class BingAPISearch {
	
	public static void main(String[] args) throws IOException, JSONException {
		
		HttpClient httpclient = new DefaultHttpClient();
		
		try {
			String accountKey = "eery2Aa3nSbP4BzjT15aV4XRKqUgKQjkRvACa+4tKq0";
            HttpGet httpget = new HttpGet("https://api.datamarket.azure.com/Data.ashx/Bing/Search/Web?Query=%27Pablo%27&$top=10&$format=Json");
            httpget.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((accountKey + ":" + accountKey).getBytes()));

            System.out.println("executing request " + httpget.getURI());

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println("----------------------------------------");

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
	}

}
