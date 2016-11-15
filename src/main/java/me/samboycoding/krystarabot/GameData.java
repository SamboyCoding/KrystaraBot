package me.samboycoding.krystarabot;

import java.io.File;
import java.io.FileNotFoundException;
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
    
    public JSONObject getTroopInfo(String troopName)
    {
        JSONObject troop = null;
        for(int i=0; i<arrayTroops.length(); i++) {
            JSONObject checkTroop = arrayTroops.getJSONObject(i);
            if(checkTroop.get("Name") == troopName)
            {
                return checkTroop;
            }
        }
        return troop;
    }
    
    public JSONObject getTraitInfo(String traitName)
    {
        JSONObject trait = null;
        for(int i=0; i<arrayTraits.length(); i++) {
            JSONObject checkTrait = arrayTraits.getJSONObject(i);
            if(checkTrait.get("Name") == traitName)
            {
                return checkTrait;
            }
        }
        return trait;
    }
    
    public JSONObject getSpellInfo(String spellName)
    {
        JSONObject spell = null;
        for(int i=0; i<arraySpells.length(); i++) {
            JSONObject checkSpell = arraySpells.getJSONObject(i);
            if(checkSpell.get("Name") == spellName)
            {
                return checkSpell;
            }
        }
        return spell;
    }
}
