package me.samboycoding.krystarabot;

import java.io.File;
import org.json.JSONArray;

/**
 * Class that collects and provides data from JSON-Source File
 *
 * @author MrSnake
 */
public class GameData {
    
    private final File sourceJSON = new File("Data.json");
    //JSON arrays for original data
    private static JSONArray arrayTroops, arrayTraits, arraySpells;
    
    public void importData() {
        if(sourceJSON.exists()) {
            main.log("Source-File 'Data.json' is available.");
        } else {
            main.log("Source-File 'Data.json' is NOT available.");
        }
    }
}
