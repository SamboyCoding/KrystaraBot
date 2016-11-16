package me.samboycoding.krystarabot.utilities;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

/**
 * Various utilities
 *
 * @author Sam
 */
public class Utilities {

    /**
     * Deletes the specified message after the specified delay. This spawns a
     * new thread that terminates after *delay* milliseconds. Exceptions will be
     * ignored.
     *
     * @param msg The message to delete.
     * @param delay The time, in milliseconds, to wait before deleting the
     * message.
     */
    public static void cleanupMessage(IMessage msg, int delay) {
        new Timer("MsgSelfDestruct" + System.currentTimeMillis()).schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    msg.delete();
                } catch (Exception e) {
                    //Ignore
                }
            }
        }, delay);
    }

    /**
     * Checks if the specified user has the specified role on the specified
     * guild. Exceptions are ignored. For users with a large amount of roles,
     * this may take some time.
     *
     * @param srv The guild to check
     * @param usr The user to check
     * @param role The role to check for
     * @return true if the role is found, false otherwise, including if an
     * exception is thrown.
     */
    public static Boolean userHasRole(IGuild srv, IUser usr, IRole role) {
        try {
            List<IRole> roles = usr.getRolesForGuild(srv);
            for (IRole r : roles) {
                if (r.equals(role)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the user has either the admin, dev, or moderator role
     * @param usr The user to check
     * @param server The server to check on.
     * @return Whether the user can use admin commands.
     */
    public static Boolean canUseAdminCommand(IUser usr, IGuild server) {
        IRole admin = server.getRoleByID(IDReference.RoleID.ADMIN.toString());
        IRole dev = server.getRoleByID(IDReference.RoleID.DEV.toString());
        IRole mod = server.getRoleByID(IDReference.RoleID.MODERATOR.toString());
        
        return userHasRole(server, usr, admin) || userHasRole(server, usr, dev) || userHasRole(server, usr, mod);
    }
}
