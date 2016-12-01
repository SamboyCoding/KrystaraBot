package me.samboycoding.krystarabot.utilities;

/**
 * Class to contain references for various IDs
 *
 * @author Sam
 */
public class IDReference
{

    public static final boolean LIVE = false;
    
    public static String LOGSCHANNEL, CODESCHANNEL, BOTCOMMANDSCHANNEL, TEAMSCHANNEL;
    public static String ADMINROLE, DEVROLE, MODROLE, STREAMERROLE, MUTEDROLE, PCMOBILEROLE, CONSOLEROLE;
    public static String SERVERID;
    public static String MYTOKEN; //Set in main method.
    public static String MYID; //Gets set once readyevent done.
    
    public IDReference()
    {
        if (LIVE)
        {
            //REAL
            
            //Channels
            LOGSCHANNEL = "237546188994379776";
            CODESCHANNEL = "237455339857903617";
            BOTCOMMANDSCHANNEL = "237500381880516609";
            TEAMSCHANNEL = "236904343859757057";
            
            
            //Roles
            ADMINROLE = "236897540262330370";
            DEVROLE = "237461438548017154";
            MODROLE = "236897692599451648";
            STREAMERROLE = "240689877325185024";
            MUTEDROLE = "241943239374929920";
            PCMOBILEROLE = "237500925982277634";
            CONSOLEROLE = "237500960576897024";
            
            SERVERID = "236897364697284618";
        } else
        {
            //DEV
            
            //Channels
            LOGSCHANNEL = "247394226319392768";
            CODESCHANNEL = "247394202844004363";
            BOTCOMMANDSCHANNEL = "247394319449849856";
            TEAMSCHANNEL = "253244926618370048";
            
            //Roles
            ADMINROLE = "247394012162424843";
            DEVROLE = "247395805567123467";
            MODROLE = "247395922177163264";
            STREAMERROLE = "247395963243593728";
            MUTEDROLE = "247396046416773121";
            PCMOBILEROLE = "247395991127457792";
            CONSOLEROLE = "247396031422005248";
            
            SERVERID = "247393304151326721";
        }
    }
}
