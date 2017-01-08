/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.gemdb;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author julians
 */
public class AshClient
{
    public static <T> T query(String apiPathAndQuery, Class<T> c) throws IOException
    {
        URL url = new URL("http://ashtender.com/gems/api/" + apiPathAndQuery);
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        String encoding = con.getContentEncoding();
        encoding = encoding == null ? "UTF-8" : encoding;
        String body = IOUtils.toString(in, encoding);
        Gson gson = new Gson();
        return gson.fromJson(body, c);
    }
}
