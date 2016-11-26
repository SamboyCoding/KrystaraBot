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
import java.util.Iterator;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

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
            //main.data.readJSONFromString(body);
            JSONObject rawData = new JSONObject(body);
            
            GameData.arrayClasses = rawData.getJSONArray("HeroClasses");
            GameData.arrayKingdoms = rawData.getJSONArray("Kingdoms");
            GameData.arrayTroops = rawData.getJSONArray("Troops");
            
            JSONArray traits = new JSONArray();
            
            //For each, calculate the new Health, Attack, Armor, and save any traits not yet known.
            for (Iterator<Object> it = GameData.arrayTroops.iterator(); it.hasNext();)
            {
                JSONObject troop = (JSONObject) it.next();
                JSONArray traitsForTroop = troop.getJSONArray("ParsedTraits");
                for (Iterator<Object> it2 = traitsForTroop.iterator(); it2.hasNext();)
                {
                    JSONObject trait = (JSONObject) it2.next();
                    JSONObject newTrait = new JSONObject();
                    
                    //Check if trait already known
                    boolean known = false;
                    for(Object knownTrait : traits)
                    {
                        JSONObject obj = (JSONObject) knownTrait;
                        if(obj.getString("Name").equals(trait.getString("Name")))
                        {
                            known = true;
                            break;
                        }
                    }
                    if(known)
                    {
                        continue;
                    }
                    
                    //Data slimming
                    newTrait.put("Name", trait.get("Name"));
                    newTrait.put("Description", trait.get("Description"));
                    
                    traits.put(newTrait);
                }
            }
            
            GameData.arrayTraits = traits;
            
        } catch (IOException ex) {
            main.logToBoth("Unable to load Game Data: " + ex.getMessage());
        }
    }
}
