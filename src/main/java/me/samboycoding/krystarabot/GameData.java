package me.samboycoding.krystarabot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.charset.Charset.defaultCharset;

/**
 * Class that collects and provides data from JSON-Source File
 *
 * @author MrSnake
 */
public class GameData
{

    private final File sourceJSON = new File("Data.json"); //Source File
    public static JSONArray arrayTroops, arrayTraits, arraySpells, arrayClasses, arrayKingdoms; //JSON arrays for original data
    public static boolean dataLoaded = false; //If the data has been loaded

    /**
     * Entry point for data importing by other classes.
     * @throws IOException If the file could not be read.
     * @deprecated Should be handled by {@link me.samboycoding.krystarabot.GameDataLoaderThread} instead.
     */
    @Deprecated
    public void importData() throws IOException
    {
        if (sourceJSON.exists())
        {
            main.logToBoth("Source-File 'Data.json' is available - starting data import.");
            readJSON();
            main.logToBoth("Data import done.");
        } else
        {
            main.logToBoth("Error: Source-File 'Data.json' is NOT available.");
        }
    }

    public void readJSONFromString(String raw) throws IOException
    {
        try
        {
            JSONObject jsonFull = new JSONObject(raw);
            arrayTroops = jsonFull.getJSONArray("Troops");
            arrayTraits = jsonFull.getJSONArray("Traits");
            arraySpells = jsonFull.getJSONArray("Spells");
            arrayClasses = jsonFull.getJSONArray("HeroClasses");
            arrayKingdoms = jsonFull.getJSONArray("Kingdoms");
        } catch (JSONException ex)
        {
            main.logToBoth("JSON file is corrupt! The GameData class will be broken for the entire session. Details: " + ex.getMessage());
        }
    }

    /**
     * Gets the value for whatever property passed at level 20, using the original value specified, plus the increases + any acensions
     * @param original The original value for the property (at level 1)
     * @param increases The increases array for the property
     * @param ascensions The ascensions array for the property
     * @return The value for the property at level 20.
     */
    public int getLevel20ForProperty(int original, JSONArray increases, JSONArray ascensions)
    {
        int res = 0;
        int j = 0;

        for (int i = 0; i < increases.length(); i++)
        {
            if (i == 0)
            {
                //Set original value
                res += original + increases.getInt(i);
            } else if (i < 15)
            {
                res += increases.getInt(i);
            } else if (ascensions != null)
            {
                res += increases.getInt(i) + ascensions.getInt(j);
                j++;
            } else
            {
                res += increases.getInt(i);
            }

        }
        return res;
    }
    
    /**
     * Gets the value for whatever property passed at level 10, using the original value specified, plus the increases up to level 10
     * @param original The original value for the property (at level 1)
     * @param increases The increases array for the property
     * @return The value for the property at level 20.
     */
    public int getLevel10ForProperty(int original, JSONArray increases)
    {
        int res = 0;
        int j = 0;

        for (int i = 0; i < 10; i++)
        {
            if (i == 0)
            {
                //Set original value
                res += original + increases.getInt(i);
            } else
            {
                res += increases.getInt(i);
            }

        }
        return res;
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
            main.logToBoth("Error: " + e.getMessage());
        } catch (JSONException e2)
        {
            main.logToBoth("JSON file is corrupt! The GameData class will be broken for the entire session. Details: " + e2.getMessage());
        }
    }

    public JSONObject getTroopByName(String troopName)
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
    
    public JSONObject getTroopById(int id)
    {
        JSONObject troop = null;
        for (Iterator<Object> it = arrayTroops.iterator(); it.hasNext();)
        {
            JSONObject checkTroop = (JSONObject) it.next();
            if (checkTroop.getInt("Id") == id)
            {
                return checkTroop;
            }
        }
        return troop;
    }

    public JSONObject getTraitByName(String traitName)
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
            if(checkKingdom.isNull("Name"))
            {
                main.logToBoth("[Warning] Ignoring kingdom with null name; it's reference name is " + checkKingdom.getString("ReferenceName"));
                continue;
            }
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
            if (name.toLowerCase().contains(searchTerm.toLowerCase()))
            {
                res.add(name);
            }
        }

        return res;
    }
    
    public JSONObject getKingdomFromBanner(String searchTerm)
    {
        for (Iterator<Object> it = arrayKingdoms.iterator(); it.hasNext();)
        {
            JSONObject kingdom = (JSONObject) it.next();
            String bannerName = kingdom.getString("BannerName");
            
            if (bannerName.toLowerCase().equals(searchTerm.toLowerCase()))
            {
                return kingdom;
            }
        }
        
        return null;
    }

    public ArrayList<String> searchForTrait(String searchTerm)
    {
        ArrayList<String> res = new ArrayList<>();

        for (Iterator<Object> it = arrayTraits.iterator(); it.hasNext();)
        {
            String name = ((JSONObject) it.next()).getString("Name");
            if (name.toLowerCase().contains(searchTerm.toLowerCase()))
            {
                res.add(name);
            }
        }

        return res;
    }
    
    /**
     * Searches for a banner, EITHER BY BANNER NAME OR KINGDOM NAME.
     * @param searchTerm The banner/kingdom name to search for.
     * @return An arraylist containing all results.
     */
    public ArrayList<String> searchForBanner(String searchTerm)
    {
        ArrayList<String> res = new ArrayList<>();
        
        for (Iterator<Object> it = arrayKingdoms.iterator(); it.hasNext();)
        {
            JSONObject kingdom = (JSONObject) it.next();
            if(kingdom.isNull("BannerName") || kingdom.isNull("Name"))
            {
                main.logToBoth("[Warning] Ignoring kingdom with null name; it's reference name is " + kingdom.getString("ReferenceName"));
                continue;
            }
            
            String bannerName = kingdom.getString("BannerName");
            String kingdomName = kingdom.getString("Name");
            if(bannerName.toLowerCase().contains(searchTerm.toLowerCase()) || kingdomName.toLowerCase().replace("'", "").contains(searchTerm.toLowerCase().replace("'", "")))
            {
                res.add(bannerName);
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
            if (name.toLowerCase().contains(searchTerm.toLowerCase()))
            {
                res.add(name);
            }
        }

        return res;
    }

    public ArrayList<String> searchForClass(String searchTerm)
    {
        ArrayList<String> res = new ArrayList<>();

        for (Iterator<Object> it = arrayClasses.iterator(); it.hasNext();)
        {
            String name = ((JSONObject) it.next()).getString("Name");
            if (name.toLowerCase().contains(searchTerm.toLowerCase()))
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
            JSONObject obj = (JSONObject) it.next();
            if(obj.isNull("Name"))
            {
                main.logToBoth("[Warning] Ignoring kingdom with null name; it's reference name is " + obj.getString("ReferenceName"));
                continue;
            }
            String name = obj.getString("Name");
            if (name.toLowerCase().contains(searchTerm.toLowerCase()))
            {
                res.add(name);
            }
        }

        return res;
    }
}
