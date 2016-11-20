package me.samboycoding.krystarabot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

/**
 * Handles the MessageCounter JSON file
 *
 * @author MrSnake
 */
public class MessageCounterHandler
{

    File messageCounter = new File("MessageCounter.json");
    JSONObject msgCounterJSON;

    //check is file is existent
    public void loadJSON()
    {
        try
        {
            main.log("Attempting to load message counter...");
            if (messageCounter.exists())
            { //file existent -> load
                main.log("Existing message counter found, loading...");
                loadFromJSON();
            } else
            { // file not existent -> create 
                main.log("No message counter file found, creating a new one...");
                messageCounter.createNewFile();
                FileUtils.writeStringToFile(messageCounter, "{}", Charset.defaultCharset());
                msgCounterJSON = new JSONObject();
            }
            main.log("Success!");
        } catch (IOException e)
        {
            main.log("Error loading/creating message counter file! Messages will NOT be counted...!");
            e.printStackTrace();
        }
    }

    //load the file content
    private void loadFromJSON()
    {
        try
        {
            String jsonRaw = FileUtils.readFileToString(messageCounter, Charset.defaultCharset());
            msgCounterJSON = new JSONObject(jsonRaw);
            main.log("Succesfully loaded message counter from file!");
        } catch (IOException ex)
        {
            main.log("Error reading codes file!");
            ex.printStackTrace();
        }
    }

    //count the messages for the user
    public void countMessage(IUser usr, IGuild server) throws IOException
    {
        if(!msgCounterJSON.has(server.getID()))
        {
            msgCounterJSON.put(server.getID(), new JSONObject());
        }
        JSONObject serverJSON = msgCounterJSON.getJSONObject(server.getID());
        if (serverJSON.isNull(usr.getID()))
        {
            //if key with sender id is NULL create new JSONObject for new ueser 
            JSONObject newUser = new JSONObject();
            newUser.put("messages", 1);
            newUser.put("name", usr.getName());
            serverJSON.put(usr.getID(), newUser);
        } else
        { //if key already exists, increase message counter
            JSONObject currentUser = serverJSON.getJSONObject(usr.getID());
            int messages = currentUser.getInt("messages");
            messages++;
            currentUser.put("messages", messages);
        }
        FileUtils.writeStringToFile(messageCounter, msgCounterJSON.toString(4), Charset.defaultCharset());
    }
}
