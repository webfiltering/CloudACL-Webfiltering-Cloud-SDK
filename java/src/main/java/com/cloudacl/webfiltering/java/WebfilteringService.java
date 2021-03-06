package com.cloudacl.webfiltering.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WebfilteringService {
    static private final String webfilteringBaseUrl =
            "https://api.cloudacl.com/webapi/getcategory";
    static WebfilteringService INSTANCE = null;
    static public WebfilteringService getInstance() {
        if (INSTANCE == null) {
            return new WebfilteringService();
        }
        return INSTANCE;
    }
    
    public static class UrlCategory {
        private int errorcode;
        private String id;
        private String url;
        private String desc;
        public UrlCategory(int errorcode, String id, String url, String desc) {
            this.errorcode = errorcode;
            this.id = id;
            this.url = url;
            this.desc = desc;
        }
        public int getErrorCode() {
            return errorcode;
        }
        public String getId() {
            return id;
        }
        public String getUrl() {
            return url;
        }
        public String getDesc() {
            return desc;
        }
    }
    
    public UrlCategory getCategoryByUrl(String key, String url) throws IOException, ParseException {
        HttpClient httpClient = new DefaultHttpClient();
        String requestUrl = String.format("%s?url=%s&key=%s", webfilteringBaseUrl, url, key);
        HttpGet httpGet = new HttpGet(requestUrl);
        HttpResponse response = httpClient.execute(httpGet);
        
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            throw new IOException("Fail to get result from request");
        }
        InputStream instream = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            sb.append(line + "\n");
        }
        JSONParser parser=new JSONParser();
        JSONObject json = (JSONObject)parser.parse(sb.toString());
        long r_errorCode = (Long)json.get("errorcode");
        String r_id = json.get("id").toString();
        String r_url = (String)json.get("url");
        String r_desc = (String)json.get("desc");        
        return new UrlCategory((int)r_errorCode, r_id, r_url, r_desc);
    }
}
