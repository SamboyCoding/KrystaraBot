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
public class GameDataLoaderThread implements Runnable
{

    @Override
    public void run()
    {
        try
        {
            GameData.dataLoaded = false;
            long startConnectionTime = System.currentTimeMillis();

            //Initial setup
            main.logToBoth("[Game Data Loader]  Attempting to load data from remote location...");
            URL url = new URL("http://ashtender.com/gems/tools/world.php");

            //Make request
            main.logToBoth("[Game Data Loader]  Opening connection...");
            URLConnection con = url.openConnection();

            //Open stream to read data
            main.logToBoth("[Game Data Loader] Opening stream...");
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;

            //Read data -> string
            main.logToBoth("[Game Data Loader] Retreiving body...");
            String body = IOUtils.toString(in, encoding);

            //Parse data
            long finishDownloadTime = System.currentTimeMillis();
            main.logToBoth("[Game Data Loader] Body retrieved (in " + (finishDownloadTime - startConnectionTime) + " milliseconds). Contains " + body.length() + " characters. Parsing -> JSONObject...");
            JSONObject rawData = new JSONObject(body);

            //Just copy kingdoms array over as-is.
            main.logToBoth("[Game Data Loader] Loading Kingdoms...");
            GameData.arrayKingdoms = rawData.getJSONArray("Kingdoms");
            main.logToBoth("[Game Data Loader] Loaded " + GameData.arrayKingdoms.length() + " Kingdoms.");

            //Copy classes over
            main.logToBoth("[Game Data Loader] Loading classes...");
            GameData.arrayClasses = rawData.getJSONArray("HeroClasses");

            JSONArray newClasses = new JSONArray();

            //Modify all hero classes, and add the kingdom name object.
            for (Object c : GameData.arrayClasses)
            {
                JSONObject hClass = (JSONObject) c;
                int kingdomId = hClass.getInt("KingdomId");
                boolean found = false;

                //Loop through all kingdoms until we find one with the correct id.
                for (Object k : GameData.arrayKingdoms)
                {
                    JSONObject kingdom = (JSONObject) k;
                    int id = kingdom.getInt("Id");
                    if (kingdomId == id)
                    {
                        found = true;
                        hClass.put("Kingdom", kingdom.get("Name").toString());
                        break;
                    }
                }

                if (!found)
                {
                    main.logToBoth("[Game Data Loader] Could not load kingdom info for Hero Class \"" + hClass.getString("Name") + "\"! It will not be available!");
                    continue;
                }

                newClasses.put(hClass);
            }

            //Save modified class data.
            GameData.arrayClasses = newClasses;
            main.logToBoth("[Game Data Loader] Loaded " + GameData.arrayClasses.length() + " Hero Classes.");

            //Copy troop array over.
            main.logToBoth("[Game Data Loader] Loading Troops");
            GameData.arrayTroops = rawData.getJSONArray("Troops");

            JSONArray modifiedTroops = new JSONArray();

            //Fill in kingdom of origin for each troop
            for (Object trp : GameData.arrayTroops)
            {
                JSONObject trp2 = (JSONObject) trp;
                int id = trp2.getInt("Id");
                boolean found = false;

                //Loop through all kingdoms until we find the one containing our troop.
                for (Object kng : GameData.arrayKingdoms)
                {
                    JSONObject kngdom = (JSONObject) kng;

                    //Loop through all troops - check if the one we want is in there.
                    for (Iterator<Object> it = kngdom.getJSONArray("TroopIds").iterator(); it.hasNext();)
                    {
                        int troopId = (int) it.next();
                        if (troopId == id)
                        {
                            found = true;
                            trp2.put("Kingdom", kngdom.getString("Name"));
                            break;
                        }
                    }

                    if (found)
                    {
                        break; //We've found the troop - don't continue to loop through the kingdoms
                    }
                }

                if (!trp2.has("Kingdom"))
                {
                    main.logToBoth("[Game Data Loader] ERROR! Could not find kingdom for troop \"" + trp2.getString("Name") + "\" (with reference name \"" + trp2.getString("ReferenceName") + "\")! It will not be available!");
                    continue; //Do not add
                }

                modifiedTroops.put(trp2); //Add to new troops array
            }

            //Save modified troops
            GameData.arrayTroops = modifiedTroops;
            main.logToBoth("[Game Data Loader] Loaded " + GameData.arrayTroops.length() + " Troops.");

            //Copy traits over
            main.logToBoth("[Game Data Loader] Loading Traits...");
            JSONArray traits = new JSONArray();

            //For each troop, save any traits not yet known.
            //Loop through all troops
            for (Iterator<Object> it = GameData.arrayTroops.iterator(); it.hasNext();)
            {
                JSONObject troop = (JSONObject) it.next();
                JSONArray traitsForTroop = troop.getJSONArray("ParsedTraits");

                //Loop through its traits
                for (Iterator<Object> it2 = traitsForTroop.iterator(); it2.hasNext();)
                {
                    JSONObject trait = (JSONObject) it2.next();
                    JSONObject newTrait = new JSONObject();

                    //Check if trait already known
                    boolean known = false;

                    //Loop through all known traits
                    for (Object knownTrait : traits)
                    {
                        JSONObject obj = (JSONObject) knownTrait;
                        if (obj.getString("Name").equals(trait.getString("Name")))
                        {
                            known = true;
                            break;
                        }
                    }

                    //Move on if known
                    if (known)
                    {
                        continue;
                    }

                    //Data slimming
                    newTrait.put("Name", trait.get("Name"));
                    newTrait.put("Description", trait.get("Description"));

                    //Add to known traits
                    traits.put(newTrait);
                }
            }

            //Do the same again, for the hero class traits.
            for (Iterator<Object> it = GameData.arrayClasses.iterator(); it.hasNext();)
            {
                JSONObject hClass = (JSONObject) it.next();
                JSONArray traitsForClass = hClass.getJSONArray("ParsedTraits");

                //Loop through all class traits
                for (Iterator<Object> it2 = traitsForClass.iterator(); it2.hasNext();)
                {
                    JSONObject trait = (JSONObject) it2.next();
                    JSONObject newTrait = new JSONObject();

                    //Check if trait already known
                    boolean known = false;

                    //Loop through all known traits
                    for (Object knownTrait : traits)
                    {
                        JSONObject obj = (JSONObject) knownTrait;
                        if (obj.getString("Name").equals(trait.getString("Name")))
                        {
                            known = true;
                            break;
                        }
                    }

                    //Move on if known
                    if (known)
                    {
                        continue;
                    }

                    //Data slimming
                    newTrait.put("Name", trait.get("Name"));
                    newTrait.put("Description", trait.get("Description"));

                    //Add to known traits
                    traits.put(newTrait);
                }
            }

            //Save modified traits
            GameData.arrayTraits = traits;
            main.logToBoth("[Game Data Loader] Loaded " + GameData.arrayTraits.length() + " traits.");

            //Create empty spells array
            main.logToBoth("[Game Data Loader] Loading spells...");
            JSONArray newSpells = new JSONArray();

            //Go through all troops, and add spells.
            for (Object t : GameData.arrayTroops)
            {
                //Gather raw data
                JSONObject trp = (JSONObject) t;
                JSONObject spellInfo = trp.getJSONObject("Spell");
                JSONObject spell = new JSONObject();

                //Format spell description
                String desc = spellInfo.getString("Description");
                JSONObject firstSpellStep = spellInfo.getJSONArray("SpellSteps").getJSONObject(0);
                desc = getMagicValue(trp, 1, desc);

                //Only copy over what's needed
                spell.put("Name", spellInfo.getString("Name"));
                spell.put("Description", desc);
                spell.put("Cost", spellInfo.getInt("Cost"));

                //Add to new spells array.
                newSpells.put(spell);
            }

            //Copy over new array
            GameData.arraySpells = newSpells;
            main.logToBoth("[Game Data Loader] Loaded " + GameData.arraySpells.length() + " spells");

            //Logging.
            long finishParseTime = System.currentTimeMillis();
            main.logToBoth("[Game Data Loader] Finished loading game data (in " + (finishParseTime - startConnectionTime) + " milliseconds - " + (finishParseTime - finishDownloadTime) + " milliseconds since download finished).");
            GameData.dataLoaded = true;
        } catch (IOException ex)
        {
            main.logToBoth("Unable to load Game Data: " + ex.getMessage());
        }
    }

    /**
     * Gets the filled-in string for the {1} placeholder in spell descriptions.
     *
     * @param firstStep The first item in the Troop.Spell.SpellSteps array for
     * the troop.
     * @return A formatted string containing the text the {1} placeholder should
     * hold.
     */
    /*private String getMagicValue(JSONObject firstStep)
    {
        //get the magic value for spell descriptions
        String magicDesc = "Magic";
        if (firstStep.getBoolean("Primarypower"))
        {
            if (firstStep.getInt("SpellPowerMultiplier") == 0)
            {
                return "" + firstStep.getInt("Amount");
            }
            if (firstStep.getInt("Amount") != 0)
            {
                magicDesc = magicDesc + "+" + firstStep.getInt("Amount");
            }
            if (firstStep.getInt("SpellPowerMultiplier") != 1)
            {
                if (!magicDesc.equals("Magic"))
                {
                    magicDesc = "(" + magicDesc + ")";
                }
                if (firstStep.getInt("SpellPowerMultiplier") < 1)
                {
                    magicDesc = magicDesc + " / " + (1 / firstStep.getInt("SpellPowerMultiplier"));
                } else
                {
                    magicDesc = magicDesc + " x " + firstStep.getInt("SpellPowerMultiplier");
                }
            }
        }
        magicDesc = "[" + magicDesc + "]";
        return magicDesc;
    }*/
    public String getMagicValue(JSONObject troop, int magicAmount, String magicText)
    {
        String spellDesc = troop.getJSONObject("Spell").getString("Description");
        String boostDesc = "";
        String magicDesc = "";
        JSONObject spell = troop.getJSONObject("Spell");

        if (spell.has("SpellSteps"))
        {
            for (Object s : spell.getJSONArray("SpellSteps"))
            {
                JSONObject spellStep = (JSONObject) s;
                if (spellStep.getString("Type").equals("Count"))
                {
                    switch (spellStep.getInt("Amount"))
                    {
                        case 20:
                            boostDesc = " [5:1]";
                            break;
                        case 25:
                            boostDesc = " [4:1]";
                            break;
                        case 34:
                            boostDesc = " [3:1]";
                            break;
                        case 50:
                            boostDesc = " [2:1]";
                            break;
                        case 100:
                            boostDesc = " [1:1]";
                            break;
                        default:
                            if (spellStep.getInt("Amount") > 100 && spellStep.getInt("Amount") < 10000)
                            {
                                boostDesc = " [" + (spellStep.getInt("Amount") / 100) + "x]";
                            }
                            break;
                    }
                }

                if (spellStep.getBoolean("Primarypower"))
                {
                    if (spellStep.getInt("SpellPowerMultiplier") == 0)
                    {
                        magicDesc = "" + spellStep.getInt("Amount");
                    } else
                    {
                        magicDesc = magicText;
                        if (spellStep.getInt("SpellPowerMultiplier") != 1)
                        {
                            if (!magicDesc.equals(magicText))
                            {
                                magicDesc = "(" + magicDesc + ")";
                            }
                            if (spellStep.getInt("SpellPowerMultiplier") < 1)
                            {
                                magicDesc = magicDesc + " / " + (1 / spellStep.getInt("SpellPowerMultiplier"));
                            } else
                            {
                                //SpellPowerMultiplier > 1
                                magicDesc = magicDesc + " x " + spellStep.getInt("SpellPowerMultiplier");
                            }
                            //Do not check if magicAmount is null - ints cannot be null
                            magicAmount = (int) Math.floor(magicAmount * spellStep.getDouble("SpellPowerMultiplier"));
                        }

                        if (spellStep.getInt("Amount") != 0)
                        {
                            if (!magicDesc.equals(magicText))
                            {
                                magicDesc = "(" + magicDesc + ")";
                            }

                            magicDesc = magicDesc + " + " + spellStep.getInt("Amount");

                            //Again, do not check if magicAmount is null
                            magicAmount += spellStep.getInt("Amount");
                        }

                        magicDesc = "[" + magicDesc + "]";
                    }
                }
            }
        }

        if (magicDesc.equals(""))
        {
            magicDesc = "[" + magicText + "]";
        }

        //Another magicAmount null check gone
        magicDesc = magicAmount + " " + magicDesc;

        if (!spellDesc.contains("{2}"))
        {
            spellDesc = spellDesc.replace("{1}", magicDesc);
        } else
        {
            spellDesc = spellDesc.replace("{1}", "(half)").replace("{2}", magicDesc);
        }

        return spellDesc + boostDesc;
    }
}
