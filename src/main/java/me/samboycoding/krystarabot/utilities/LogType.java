package me.samboycoding.krystarabot.utilities;

import java.awt.Color;

/**
 *
 * @author r3byass
 */
public enum LogType
{
    PLATFORMASSIGN("[Platform Assigned]", Color.BLUE), MESSAGEDELETE("[Messages Deleted]", Color.yellow), USERJOIN("[New User]", Color.GREEN), MILESTONE("[New Milestone!]", Color.green), USERLEAVE("[User Left]", Color.red), RENAME("[Nickname Changed]", Color.cyan), NEWCODE("[New Code]", Color.green), DEADCODE("[Code Dead]", Color.gray), WARN("[User Warned]", Color.yellow), KICK("[User Kicked]", Color.red), BAN("[User Banned]", Color.red);

    public String logTitle;
    public Color color;

    private LogType(String txt, Color clr)
    {
        logTitle = txt;
        color = clr;
    }
}
