package me.samboycoding.krystarabot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

/**
 * Handles the Code JSON file
 *
 * @author MrSnake
 */
public class MessageCounterHandler
{

    public void MessageCounterHandler(String srvID) {
        
    }
    
    File messageCounter = new File("messageCounter.json");
    JSONObject messageCounterJSON;

    public void loadJSON()
    {
        try
        {
            main.log("Attempting to load message counter...");
            if (messageCounter.exists())
            {
                main.log("Existing message counter found, loading...");
                loadFromJSON();
            } else
            {
                main.log("No message counter file found, creating a new one...");
                messageCounter.createNewFile();
                FileUtils.writeStringToFile(messageCounter, "{}", Charset.defaultCharset());
                messageCounterJSON = new JSONObject();
            }
            main.log("Success!");
        } catch (IOException e)
        {
            main.log("Error loading/creating message counter file! Messages will NOT be counted...!");
            e.printStackTrace();
        }
    }
    
    private void loadFromJSON()
    {
        try
        {
            String jsonRaw = FileUtils.readFileToString(messageCounter, Charset.defaultCharset());
            messageCounterJSON = new JSONObject(jsonRaw);
            main.log("Succesfully loaded message counter from file!");
        } catch (IOException ex)
        {
            main.log("Error reading codes file!");
            ex.printStackTrace();
        }
    }
    
    public void countMessage(String sdr) throws IOException {
        if(messageCounterJSON.isNull(sdr)) {
            JSONObject newUser = new JSONObject();
            newUser.put("messages", 1);
            messageCounterJSON.put(sdr, newUser);
        } else {
            JSONObject currentUser = messageCounterJSON.getJSONObject(sdr);
            int messages = currentUser.getInt("messages");
            messages++;
            currentUser.put("messages", messages);
        }
        FileUtils.writeStringToFile(messageCounter, messageCounterJSON.toString(4), Charset.defaultCharset());
    }
}
