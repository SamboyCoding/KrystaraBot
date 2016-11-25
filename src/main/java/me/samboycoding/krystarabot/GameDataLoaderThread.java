/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.io.IOUtils;

/**
 * Thread to load game data from http://ashtender.com/gems/tools/world.php.
 *
 * @author Sam
 */
public class GameDataLoaderThread implements Runnable {

    @Override
    public void run() {
        try {
            main.logToBoth("[Game Data Loader]  Attempting to load data from remote location...");
            URL url = new URL("http://ashtender.com/gems/tools/world.php");
            main.logToBoth("[Game Data Loader]  Opening connection...");
            URLConnection con = url.openConnection();
            main.logToBoth("[Game Data Loader]  Reading data...");
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            main.logToBoth("[Game Data Loader]  Parsing body...");
            String body = IOUtils.toString(in, encoding);
            
            main.logToBoth("[Game Data Loader]  Passing to GameData class...");
            main.data.readJSONFromString(body);
        } catch (IOException ex) {
            main.logToBoth("Unable to load Game Data: " + ex.getMessage());
        }
    }
}
