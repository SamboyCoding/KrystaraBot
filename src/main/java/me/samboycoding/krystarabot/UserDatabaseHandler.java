package me.samboycoding.krystarabot;

import me.samboycoding.krystarabot.utilities.IDReference;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the MessageCounter JSON file
 *
 * @author MrSnake
 */
public class UserDatabaseHandler
{

    File messageCounterOld = new File("MessageCounter.json");
    File userDb = new File("Userdb.json");
    JSONObject userDBJSON;

    //check is file is existent
    public void loadJSON()
    {
        try
        {
            Main.logToBoth("Attempting to load user database...");
            if (userDb.exists())
            { //file existent -> load
                Main.logToBoth("Existing user database found, loading...");
                loadFromJSON();
            } else
            { // file not existent -> create 
                Main.logToBoth("No message counter file found, attempting to import old data...");
                if (messageCounterOld.exists())
                {
                    Main.logToBoth("Old file exists... migrating");
                    messageCounterOld.renameTo(userDb);
                    loadFromJSON();
                    JSONObject server = userDBJSON.getJSONObject("" + IDReference.SERVERID);

                    JSONObject newServer = new JSONObject();
                    //Loop through all the users and add the missing values
                    for (String id : server.keySet())
                    {
                        JSONObject usr = server.getJSONObject(id);
                        newServer.put(id, usr);
                    }

                    userDBJSON.remove("" + IDReference.SERVERID);
                    userDBJSON.put("" + IDReference.SERVERID, newServer);
                    Main.logToBoth("Successfully migrated");
                    return;
                }
                Main.logToBoth("Creating a fresh user database...");

                userDb.createNewFile();
                FileUtils.writeStringToFile(userDb, "{}", Charset.defaultCharset());
                userDBJSON = new JSONObject();
            }
            Main.logToBoth("Success!");
        } catch (IOException e)
        {
            Main.logToBoth("Error loading/creating message counter file! Messages will NOT be counted...!");
            e.printStackTrace();
        }
    }

    //load the file content
    private void loadFromJSON()
    {
        try
        {
            String jsonRaw = FileUtils.readFileToString(userDb, Charset.defaultCharset());
            userDBJSON = new JSONObject(jsonRaw);

            JSONObject server = userDBJSON.getJSONObject("" + IDReference.SERVERID);

            for (String id : server.keySet())
            {
                JSONObject usr = server.getJSONObject(id);
                if (!usr.has("QuizScore") || usr.isNull("QuizScore"))
                {
                    usr.put("QuizScore", 0);
                }
            }
            userDBJSON.remove("" + IDReference.SERVERID);
            userDBJSON.put("" + IDReference.SERVERID, server);

            FileUtils.writeStringToFile(userDb, userDBJSON.toString(4), Charset.defaultCharset(), false);

            Main.logToBoth("Succesfully loaded user database from file!");
        } catch (IOException ex)
        {
            Main.logToBoth("Error reading user database file!");
            ex.printStackTrace();
        }
    }

    /**
     * Gets a user's quiz score.
     *
     * @param usr The user
     * @param server The guild
     * @return The score
     */
    public int getQuizScore(IUser usr, IGuild server)
    {
        if (!userDBJSON.has(server.getStringID()))
        {
            userDBJSON.put(server.getStringID(), new JSONObject());
        }

        JSONObject serverJSON = userDBJSON.getJSONObject(server.getStringID());
        if (serverJSON.isNull(usr.getStringID()))
        {
            return -1;
        } else
        {
            return serverJSON.getJSONObject(usr.getStringID()).getInt("QuizScore");
        }
    }

    /**
     * Increases the specified users quiz score by the specified amount in the
     * specified server.
     *
     * @param usr The user
     * @param server The guild
     * @param amount The amount to increment by
     * @throws IOException If the changes cannot be written to the file.
     * @throws sx.blah.discord.util.MissingPermissionsException If the bot
     * cannot add roles, but needs too
     * @throws sx.blah.discord.util.RateLimitException If the bot is rate
     * limited
     * @throws sx.blah.discord.util.DiscordException If something else goes
     * wrong
     */
    public void increaseUserQuizScore(IUser usr, IGuild server, int amount) throws IOException, MissingPermissionsException, RateLimitException, DiscordException
    {
        if (!userDBJSON.has(server.getStringID()))
        {
            userDBJSON.put(server.getStringID(), new JSONObject());
        }

        JSONObject serverJSON = userDBJSON.getJSONObject(server.getStringID());
        if (serverJSON.isNull(usr.getStringID()))
        {
            //if key with sender id is NULL create new JSONObject for new user 
            JSONObject newUser = new JSONObject();
            newUser.put("messages", 1);
            newUser.put("name", usr.getName());
            newUser.put("commands", 0);
            newUser.put("QuizScore", amount);
            serverJSON.put(usr.getStringID(), newUser);
        } else
        { //if key already exists, increase message counter
            JSONObject currentUser = serverJSON.getJSONObject(usr.getStringID());
            int current = currentUser.getInt("QuizScore");
            int newVal = current + amount;
            currentUser.put("QuizScore", newVal);
            if (newVal >= 1500 && current < 1500)
            {
                //Quiz Master!
                IRole qMaster = server.getRoleByID(IDReference.QUIZMASTERROLE);
                List<IRole> currentRoles = usr.getRolesForGuild(server);
                if (!currentRoles.contains(qMaster))
                {
                    usr.addRole(qMaster);
                    server.getChannelByID(IDReference.GLOBALCHANNEL).sendMessage("Congratulations to " + usr.mention() + " on reaching 1500 quiz points! You are now a quiz master!");
                }
            }
        }
        FileUtils.writeStringToFile(userDb, userDBJSON.toString(4), Charset.defaultCharset());
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
        if (!userDBJSON.has(server.getStringID()))
        {
            userDBJSON.put(server.getStringID(), new JSONObject());
        }
        JSONObject serverJSON = userDBJSON.getJSONObject(server.getStringID());
        if (serverJSON.isNull(usr.getStringID()))
        {
            //if key with sender id is NULL create new JSONObject for new ueser 
            JSONObject newUser = new JSONObject();
            newUser.put("messages", 1);
            newUser.put("name", usr.getName());
            newUser.put("commands", 0);
            newUser.put("QuizScore", 0);
            serverJSON.put(usr.getStringID(), newUser);
        } else
        { //if key already exists, increase message counter
            JSONObject currentUser = serverJSON.getJSONObject(usr.getStringID());
            int messages = currentUser.getInt("messages");
            messages++;
            currentUser.put("messages", messages);
        }
        FileUtils.writeStringToFile(userDb, userDBJSON.toString(4), Charset.defaultCharset());
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
        if (!userDBJSON.has(server.getStringID()))
        {
            userDBJSON.put(server.getStringID(), new JSONObject());
        }

        JSONObject serverJSON = userDBJSON.getJSONObject(server.getStringID());
        if (serverJSON.isNull(usr.getStringID()))
        {
            //if key with sender id is NULL create new JSONObject for new ueser 
            JSONObject newUser = new JSONObject();
            newUser.put("messages", 1);
            newUser.put("name", usr.getName());
            newUser.put("commands", 1);
            newUser.put("QuizScore", 0);
            serverJSON.put(usr.getStringID(), newUser);
        } else
        {
            //if key already exists, increase commands counter
            JSONObject currentUser = serverJSON.getJSONObject(usr.getStringID());
            int commands = currentUser.getInt("commands");
            commands++;
            currentUser.put("commands", commands);
        }
        FileUtils.writeStringToFile(userDb, userDBJSON.toString(4), Charset.defaultCharset());
    }

    /**
     * Gets a list of user IDs in the counter file.
     *
     * @param forServer Which server to fetch the list for
     * @return An ArrayList of IDs.
     */
    public ArrayList<String> getUserIDList(IGuild forServer)
    {
        if (!userDBJSON.has(forServer.getStringID()))
        {
            return null;
        }
        ArrayList<String> res = new ArrayList<>();
        for (String s : userDBJSON.getJSONObject(forServer.getStringID()).keySet())
        {
            if (forServer.getUserByID(Long.parseLong(s)) == null)
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
        if (who == null || where == null)
        {
            return 0; //Null checking.   
        }
        if (userDBJSON.isNull(where.getStringID()))
        {
            Main.logToBoth("No server data for server: " + where.getName() + " with id: " + where.getStringID() + "!");
            return -1; //No server data. (Almost) impossible since the countXxx function is should be called before this, so the server data should be created.
        }

        if (userDBJSON.getJSONObject(where.getStringID()).isNull(who.getStringID()))
        {
            return 0; //No user data
        }

        return userDBJSON.getJSONObject(where.getStringID()).getJSONObject(who.getStringID()).getInt("messages");
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
        if (who == null || where == null)
        {
            return 0; //Null checking.   
        }

        if (userDBJSON.isNull(where.getStringID()))
        {
            Main.logToBoth("No server data for server: " + where.getName() + " with id: " + where.getStringID() + "!");
            return -1; //No server data. (Almost) impossible since the countXxx function is should be called before this, so the server data should be created.
        }

        if (userDBJSON.getJSONObject(where.getStringID()).isNull(who.getStringID()))
        {
            return 0; //No user data
        }

        return userDBJSON.getJSONObject(where.getStringID()).getJSONObject(who.getStringID()).getInt("commands");
    }
}
