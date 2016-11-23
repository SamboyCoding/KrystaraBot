package me.samboycoding.krystarabot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

/**
 * Handles the Code JSON file
 *
 * @author Sam
 */
public class CodesHandler
{

    File codes = new File("codes.json");
    JSONObject codesJSON;

    public void loadJSON()
    {
        try
        {
            main.logToBoth("Attempting to load codes.json...");
            if (codes.exists())
            {
                main.logToBoth("Existing codes found, loading...");
                loadFromJSON();
            } else
            {
                main.logToBoth("No codes file found, creating a new one...");
                codes.createNewFile();
                FileUtils.writeStringToFile(codes, "{}", Charset.defaultCharset());
            }
            main.logToBoth("Success!");
        } catch (IOException e)
        {
            main.logToBoth("Error loading/creating codes file! Codes section will be broken!");
            e.printStackTrace();
        }
    }

    private void loadFromJSON()
    {
        try
        {
            String jsonRaw = FileUtils.readFileToString(codes, Charset.defaultCharset());
            codesJSON = new JSONObject(jsonRaw);
            main.logToBoth("Succesfully loaded " + codesJSON.length() + " codes from file!");
        } catch (IOException ex)
        {
            main.logToBoth("Error reading codes file!");
            ex.printStackTrace();
        }
    }
    
    public Boolean isCodePresent(String code)
    {
        return codesJSON.has(code.toUpperCase());
    }
    
    public ArrayList<String> getLiveCodes()
    {
        ArrayList<String> res = new ArrayList<>();
        
        for(String code : codesJSON.keySet())
        {
            if(!codesJSON.getBoolean(code))
            {
                res.add(code);
            }
        }
        
        return res;
    }
    
    public ArrayList<String> getDeadCodes()
    {
        ArrayList<String> res = new ArrayList<>();
        
        for(String code : codesJSON.keySet())
        {
            if(codesJSON.getBoolean(code))
            {
                res.add(code);
            }
        }
        
        return res;
    }
    
    public Boolean isCodeAlive(String code)
    {
        return getLiveCodes().contains(code.toUpperCase());
    }
    
    public Boolean isCodeDead(String code)
    {
        return getDeadCodes().contains(code.toUpperCase());
    }
    
    public void addCode(String code) throws IOException
    {
        codesJSON.put(code.toUpperCase(), false);
        FileUtils.writeStringToFile(codes, codesJSON.toString(4), Charset.defaultCharset());
    }
    
    public void makeCodeDead(String code) throws IOException
    {
        if(!isCodePresent(code))
        {
            throw new IllegalArgumentException("Tried to make a code dead that wasn't present!");
        }
        
        if(isCodeDead(code))
        {
            return;
        }
        
        codesJSON.remove(code.toUpperCase());
        codesJSON.put(code.toUpperCase(), true);
        FileUtils.writeStringToFile(codes, codesJSON.toString(4), Charset.defaultCharset());
    }
}
