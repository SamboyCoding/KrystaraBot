package me.samboycoding.krystarabot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.nio.charset.Charset.defaultCharset;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that collects and provides data from JSON-Source File
 *
 * @author MrSnake
 */
public class GameData
{

    private final File sourceJSON = new File("Data.json"); //Source File
    public static JSONArray arrayTroops, arrayTraits, arraySpells; //JSON arrays for original data

    public void importData() throws IOException
    {
        if (sourceJSON.exists())
        {
            main.log("Source-File 'Data.json' is available - start data import.");
            readJSON();
        } else
        {
            main.log("Error: Source-File 'Data.json' is NOT available.");
        }
    }

    public void readJSON() throws IOException
    {
        try
        {
            JSONObject jsonFull = new JSONObject(FileUtils.readFileToString(sourceJSON, defaultCharset()));
            arrayTroops = jsonFull.getJSONArray("Troops");
            arrayTraits = jsonFull.getJSONArray("Traits");
            arraySpells = jsonFull.getJSONArray("Spells");
        } catch (FileNotFoundException e)
        {
            main.log("Error: " + e.getMessage());
        } catch(JSONException e2)
        {
            main.log("JSON file is corrupt! The GameData class will be broken for the entire session. Details: " + e2.getMessage());
        }
    }
}
