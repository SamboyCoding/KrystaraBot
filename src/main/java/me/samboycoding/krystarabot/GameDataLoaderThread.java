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
            String body = IOUtils.toString(in, encoding).replace("'", "\\'"); //Escape single quotes - org.json doesn't seem to like them?

            //main.logToBoth("[Game Data Loader]  Passing to GameData class...");
            //main.data.readJSONFromString(body);
            JSONObject rawData = new JSONObject(body);

            main.logToBoth("[Game Data Loader] Loading Kingdoms...");
            GameData.arrayKingdoms = rawData.getJSONArray("Kingdoms");
            main.logToBoth("[Game Data Loader] Loaded " + GameData.arrayKingdoms.length() + " Kingdoms.");

            main.logToBoth("[Game Data Loader] Loading classes...");
            GameData.arrayClasses = rawData.getJSONArray("HeroClasses");

            JSONArray newClasses = new JSONArray();

            for (Object c : GameData.arrayClasses) {
                JSONObject hClass = (JSONObject) c;
                int kingdomId = hClass.getInt("KingdomId");
                boolean found = false;
                for (Object k : GameData.arrayKingdoms) {
                    JSONObject kingdom = (JSONObject) k;
                    int id = kingdom.getInt("Id");
                    if (kingdomId == id) {
                        found = true;
                        hClass.put("Kingdom", kingdom.get("Name").toString());
                        break;
                    }
                }

                if (!found) {
                    main.logToBoth("[Game Data Loader] Could not load kingdom info for Hero Class \"" + hClass.getString("Name") + "\"! It will not be available!");
                    continue;
                }

                newClasses.put(hClass);
            }
            GameData.arrayClasses = newClasses;
            main.logToBoth("[Game Data Loader] Loaded " + GameData.arrayClasses.length() + " Hero Classes.");
            main.logToBoth("[Game Data Loader] Loading Troops");
            GameData.arrayTroops = rawData.getJSONArray("Troops");

            JSONArray modifiedTroops = new JSONArray();

            for (Object trp : GameData.arrayTroops) {
                JSONObject trp2 = (JSONObject) trp;
                int id = trp2.getInt("Id");
                boolean found = false;

                for (Object kng : GameData.arrayKingdoms) {
                    JSONObject kngdom = (JSONObject) kng;

                    for (Iterator<Object> it = kngdom.getJSONArray("TroopIds").iterator(); it.hasNext();) {
                        int troopId = (int) it.next();
                        if (troopId == id) {
                            found = true;
                            trp2.put("Kingdom", kngdom.getString("Name"));
                            break;
                        }
                    }

                    if (found) {
                        break; //We've found the troop - don't continue to loop through the kingdoms
                    }
                }

                if (!trp2.has("Kingdom")) {
                    main.logToBoth("[Game Data Loader] ERROR! Could not find kingdom for troop \"" + trp2.getString("Name") + "\"! It will not be available!");
                    continue; //Do not add
                }

                modifiedTroops.put(trp2); //Add to new troops array
            }

            GameData.arrayTroops = modifiedTroops;

            main.logToBoth("[Game Data Loader] Loaded " + GameData.arrayTroops.length() + " Troops.");

            main.logToBoth("[Game Data Loader] Loading Traits...");
            JSONArray traits = new JSONArray();

            //For each troop, save any traits not yet known.
            for (Iterator<Object> it = GameData.arrayTroops.iterator(); it.hasNext();) {
                JSONObject troop = (JSONObject) it.next();
                JSONArray traitsForTroop = troop.getJSONArray("ParsedTraits");
                for (Iterator<Object> it2 = traitsForTroop.iterator(); it2.hasNext();) {
                    JSONObject trait = (JSONObject) it2.next();
                    JSONObject newTrait = new JSONObject();

                    //Check if trait already known
                    boolean known = false;
                    for (Object knownTrait : traits) {
                        JSONObject obj = (JSONObject) knownTrait;
                        if (obj.getString("Name").equals(trait.getString("Name"))) {
                            known = true;
                            break;
                        }
                    }
                    if (known) {
                        continue;
                    }

                    //Data slimming
                    newTrait.put("Name", trait.get("Name"));
                    newTrait.put("Description", trait.get("Description"));

                    traits.put(newTrait);
                }
            }
            
            //And again, for the hero class traits.
            for (Iterator<Object> it = GameData.arrayClasses.iterator(); it.hasNext();) {
                JSONObject hClass = (JSONObject) it.next();
                JSONArray traitsForClass = hClass.getJSONArray("ParsedTraits");
                for (Iterator<Object> it2 = traitsForClass.iterator(); it2.hasNext();) {
                    JSONObject trait = (JSONObject) it2.next();
                    JSONObject newTrait = new JSONObject();

                    //Check if trait already known
                    boolean known = false;
                    for (Object knownTrait : traits) {
                        JSONObject obj = (JSONObject) knownTrait;
                        if (obj.getString("Name").equals(trait.getString("Name"))) {
                            known = true;
                            break;
                        }
                    }
                    if (known) {
                        continue;
                    }

                    //Data slimming
                    newTrait.put("Name", trait.get("Name"));
                    newTrait.put("Description", trait.get("Description"));

                    traits.put(newTrait);
                }
            }
            

            GameData.arrayTraits = traits;
            main.logToBoth("[Game Data Loader] Loaded " + GameData.arrayTraits.length() + " traits.");

            main.logToBoth("[Game Data Loader] Loading spells...");
            JSONArray newSpells = new JSONArray();
            //Go through all troops, and add spells.
            for (Object t : GameData.arrayTroops) {
                //Gather raw data
                JSONObject trp = (JSONObject) t;
                JSONObject spellInfo = trp.getJSONObject("Spell");
                JSONObject spell = new JSONObject();

                //Format spell description
                String desc = spellInfo.getString("Description");
                JSONObject firstSpellStep = spellInfo.getJSONArray("SpellSteps").getJSONObject(0);
                desc = desc.replace("{1}", getMagicValue(firstSpellStep));

                //Only copy over what's needed
                spell.put("Name", spellInfo.getString("Name"));
                spell.put("Description", desc);
                spell.put("Cost", spellInfo.getInt("Cost"));

                //Add to new spells array.
                newSpells.put(spell);
            }
            GameData.arraySpells = newSpells;
            main.logToBoth("[Game Data Loader] Loaded " + GameData.arraySpells.length() + " spells");

            main.logToBoth("[Game Data Loader] Finished loading game data.");
            GameData.dataLoaded = true;
        } catch (IOException ex) {
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
    private String getMagicValue(JSONObject firstStep) {
        //get the magic value for spell descriptions
        String magicDesc = "Magic";
        if (firstStep.getBoolean("Primarypower")) {
            if (firstStep.getInt("SpellPowerMultiplier") == 0) {
                return "" + firstStep.getInt("Amount");
            }
            if (firstStep.getInt("Amount") != 0) {
                magicDesc = magicDesc + "+" + firstStep.getInt("Amount");
            }
            if (firstStep.getInt("SpellPowerMultiplier") != 1) {
                if (!magicDesc.equals("Magic")) {
                    magicDesc = "(" + magicDesc + ")";
                }
                if (firstStep.getInt("SpellPowerMultiplier") < 1) {
                    magicDesc = magicDesc + " / " + (1 / firstStep.getInt("SpellPowerMultiplier"));
                } else {
                    magicDesc = magicDesc + " x " + firstStep.getInt("SpellPowerMultiplier");
                }
            }
        }
        magicDesc = "[" + magicDesc + "]";
        return magicDesc;
    }
}
