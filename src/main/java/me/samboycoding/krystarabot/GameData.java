package me.samboycoding.krystarabot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.nio.charset.Charset.defaultCharset;
import java.util.ArrayList;
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
    public static JSONArray arrayTroops, arrayTraits, arraySpells, arrayClasses, arrayKingdoms; //JSON arrays for original data

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
            arrayClasses = jsonFull.getJSONArray("HeroClasses");
            arrayKingdoms = jsonFull.getJSONArray("Kingdoms");
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
            if (checkTroop.getString("Name").toLowerCase().replace("'", "").equals(troopName.replace("'", "").toLowerCase()))
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
            if (checkTrait.getString("Name").toLowerCase().equals(traitName.toLowerCase()))
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
            if (checkSpell.getString("Name").toLowerCase().equals(spellName.toLowerCase()))
            {
                return checkSpell;
            }
        }
        return spell;
    }
    
    public JSONObject getClassInfo(String className)
    {
        JSONObject hClass = null;
        for (Iterator<Object> it = arrayClasses.iterator(); it.hasNext();)
        {
            JSONObject checkClass = (JSONObject) it.next();
            if (checkClass.getString("Name").toLowerCase().equals(className.toLowerCase()))
            {
                return checkClass;
            }
        }
        return hClass;
    }
    
    public JSONObject getKingdomInfo(String kingdomName)
    {
        JSONObject kingdom = null;
        for (Iterator<Object> it = arrayKingdoms.iterator(); it.hasNext();)
        {
            JSONObject checkKingdom = (JSONObject) it.next();
            if (checkKingdom.getString("Name").toLowerCase().replace("'", "").equals(kingdomName.replace("'", "").toLowerCase()))
            {
                return checkKingdom;
            }
        }
        return kingdom;
    }
    
    public ArrayList<String> searchForTroop(String searchTerm)
    {
        ArrayList<String> res = new ArrayList<>();
        
        for (Iterator<Object> it = arrayTroops.iterator(); it.hasNext();)
        {
            String name = ((JSONObject) it.next()).getString("Name");
            if(name.contains(searchTerm))
            {
                res.add(name);
            }
        }
        
        return res;
    }
    
    public ArrayList<String> searchForTrait(String searchTerm)
    {
        ArrayList<String> res = new ArrayList<>();
        
        for (Iterator<Object> it = arrayTraits.iterator(); it.hasNext();)
        {
            String name = ((JSONObject) it.next()).getString("Name");
            if(name.contains(searchTerm))
            {
                res.add(name);
            }
        }
        
        return res;
    }
    
    public ArrayList<String> searchForSpell(String searchTerm)
    {
        ArrayList<String> res = new ArrayList<>();
        
        for (Iterator<Object> it = arraySpells.iterator(); it.hasNext();)
        {
            String name = ((JSONObject) it.next()).getString("Name");
            if(name.contains(searchTerm))
            {
                res.add(name);
            }
        }
        
        return res;
    }
    
    public ArrayList<String> searchForKingdom(String searchTerm)
    {
        ArrayList<String> res = new ArrayList<>();
        
        for (Iterator<Object> it = arrayKingdoms.iterator(); it.hasNext();)
        {
            String name = ((JSONObject) it.next()).getString("Name");
            if(name.contains(searchTerm))
            {
                res.add(name);
            }
        }
        
        return res;
    }
}
