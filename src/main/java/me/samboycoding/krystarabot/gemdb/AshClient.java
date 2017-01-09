/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.gemdb;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.stream.Stream;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.apache.commons.io.IOUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

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
    
    public static <T extends Nameable> T getSingleResult(IChannel chnl, List<T> resultList, String typeString, String searchName, Class<T> type) 
        throws MissingPermissionsException, RateLimitException, DiscordException
    {
        String searchNameLower = searchName.toLowerCase();
        
        int prefixMatchCount = 0;
        T prefixMatch = null;
        
        if (resultList.isEmpty())
        {
            String message = "No " + typeString + " \"" + searchName + "\" found.";
            chnl.sendMessage(message);
            return null;
        }

        // Search for an exact match in the result list
        for (T result : resultList)
        {
            String resultNameLower = result.getName().toLowerCase();
            
            if (resultNameLower.equals(searchNameLower))
            {
                return result;
            }
            
            // Count prefix matches
            if (resultNameLower.startsWith(searchNameLower))
            {
                prefixMatchCount++;
                prefixMatch = result;
            }
        }
        
        // No exact match; was there only one prefix match?
        if (prefixMatchCount == 1)
        {
            // Assume it's correct
            return prefixMatch;
        }
        
        // No single prefix match; was there only one result?
        if (resultList.size() == 1)
        {
            T result = resultList.get(0);
            
            // Only one (fuzzy) match; assume it's correct but show a warning
            String message = "No " + typeString + " \"" + searchName + "\" found. Assuming '" + result.getName() + "'...";
            chnl.sendMessage(message);

            return result;
        }
        
        // Ambiguity
        Stream<String> str = resultList.stream().map(t -> t.getName());
        Utilities.sendDisambiguationMessage(chnl, "Search term \"" + searchName + "\" is ambiguous.", str::iterator);

        return null;
    }
}