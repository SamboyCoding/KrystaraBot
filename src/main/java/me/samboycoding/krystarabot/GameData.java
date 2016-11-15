package me.samboycoding.krystarabot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.nio.charset.Charset.defaultCharset;
import java.util.Iterator;
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
            main.log("Source-File 'Data.json' is available - starting data import.");
            readJSON();
            main.log("Data import done.");
        } else
        {
            main.log("Error: Source-File 'Data.json' is NOT available.");
        }
    }

    private void readJSON() throws IOException
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
        } catch (JSONException e2)
        {
            main.log("JSON file is corrupt! The GameData class will be broken for the entire session. Details: " + e2.getMessage());
        }
    }

    public JSONObject getTroopInfo(String troopName)
    {
        JSONObject troop = null;
        for (Iterator<Object> it = arrayTroops.iterator(); it.hasNext();)
        {
            JSONObject checkTroop = (JSONObject) it.next();
            if (checkTroop.get("Name").equals(troopName))
            {
                return checkTroop;
            }
        }
        return troop;
    }

    public JSONObject getTraitInfo(String traitName)
    {
        JSONObject trait = null;
        for (Iterator<Object> it = arrayTraits.iterator(); it.hasNext();)
        {
            JSONObject checkTrait = (JSONObject) it.next();
            if (checkTrait.get("Name").equals(traitName))
            {
                return checkTrait;
            }
        }
        return trait;
    }

    public JSONObject getSpellInfo(String spellName)
    {
        JSONObject spell = null;
        for (Iterator<Object> it = arraySpells.iterator(); it.hasNext();)
        {
            JSONObject checkSpell = (JSONObject) it.next();
            if (checkSpell.get("Name").equals(spellName))
            {
                return checkSpell;
            }
        }
        return spell;
    }
}
