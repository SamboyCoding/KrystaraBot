package me.samboycoding.krystarabot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
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

    /**
     * Adds one to a users message count.
     *
     * @param usr The user who send the message.
     * @param server The server the message was sent in.
     * @throws IOException If the file can not be updated.
     */
    public void countMessage(IUser usr, IGuild server) throws IOException
    {
        if (!msgCounterJSON.has(server.getID()))
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
            newUser.put("commands", 0);
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

    /**
     * Adds one to a users command count.
     *
     * @param usr The user who send the message.
     * @param server The server the message was sent in.
     * @throws IOException If the file can not be updated.
     */
    public void countCommand(IUser usr, IGuild server) throws IOException
    {
        if (!msgCounterJSON.has(server.getID()))
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
            newUser.put("commands", 1);
            serverJSON.put(usr.getID(), newUser);
        } else
        {
            //if key already exists, increase commands counter
            JSONObject currentUser = serverJSON.getJSONObject(usr.getID());
            int commands = currentUser.getInt("commands");
            commands++;
            currentUser.put("commands", commands);
        }
        FileUtils.writeStringToFile(messageCounter, msgCounterJSON.toString(4), Charset.defaultCharset());
    }

    /**
     * Gets a list of user IDs in the counter file.
     *
     * @param forServer Which server to fetch the list for
     * @return An ArrayList of IDs.
     */
    public ArrayList<String> getUserIDList(IGuild forServer)
    {
        if (!msgCounterJSON.has(forServer.getID()))
        {
            return null;
        }
        ArrayList<String> res = new ArrayList<>();
        for (String s : msgCounterJSON.getJSONObject(forServer.getID()).keySet())
        {
            if(forServer.getUserById(s) == null)
            {
                continue;   
            }
            res.add(s);
        }

        return res;
    }

    /**
     * Gets the amount of messages the specified user has sent.
     *
     * @param who The user to fetch the count for.
     * @param where The server to fetch the count from.
     * @return The number of messages the user has sent in the specified server,
     * since this update was added.
     * @since Version 1.2
     */
    public int getMessageCountForUser(IUser who, IGuild where)
    {
        if(who == null || where == null)
        {
            return 0; //Null checking.   
        }
        if (msgCounterJSON.isNull(where.getID()))
        {
            main.log("No server data for server: " + where.getName() + " with id: " + where.getID() + "!");
            return -1; //No server data. (Almost) impossible since the countXxx function is should be called before this, so the server data should be created.
        }

        if (msgCounterJSON.getJSONObject(where.getID()).isNull(who.getID()))
        {
            return 0; //No user data
        }

        return msgCounterJSON.getJSONObject(where.getID()).getJSONObject(who.getID()).getInt("messages");
    }

    /**
     * Gets the amount of commands the specified user has sent.
     *
     * @param who The user to fetch the count for.
     * @param where The server to fetch the count from.
     * @return The number of commands the user has sent in the specified server,
     * since this update was added.
     * @since Version 1.2
     */
    public int getCommandCountForUser(IUser who, IGuild where)
    {
        if(who == null || where == null)
        {
            return 0; //Null checking.   
        }
        
        if (msgCounterJSON.isNull(where.getID()))
        {
            main.log("No server data for server: " + where.getName() + " with id: " + where.getID() + "!");
            return -1; //No server data. (Almost) impossible since the countXxx function is should be called before this, so the server data should be created.
        }

        if (msgCounterJSON.getJSONObject(where.getID()).isNull(who.getID()))
        {
            return 0; //No user data
        }

        return msgCounterJSON.getJSONObject(where.getID()).getJSONObject(who.getID()).getInt("commands");
    }
}
