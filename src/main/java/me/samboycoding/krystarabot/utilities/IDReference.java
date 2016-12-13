package me.samboycoding.krystarabot.utilities;

/**
 * Class to contain references for various IDs
 *
 * @author Sam
 */
public class IDReference
{
    public static enum RuntimeEnvironment
    {
        Live,
        Dev,
        Lyya
    }
    
    public static final RuntimeEnvironment Environment = RuntimeEnvironment.Dev;
    
    public static String LOGSCHANNEL, CODESCHANNEL, BOTCOMMANDSCHANNEL, TEAMSCHANNEL, GLOBALCHANNEL;
    public static String ADMINROLE, DEVROLE, MODROLE, STREAMERROLE, PCMOBILEROLE, CONSOLEROLE;
    public static String SERVERID;
    public static String MYTOKEN; //Set in main method.
    public static String MYID; //Gets set once readyevent done.
    
    public IDReference()
    {
        switch (Environment)
        {
            case Live:
                //Channels
                LOGSCHANNEL = "237546188994379776";
                CODESCHANNEL = "237455339857903617";
                BOTCOMMANDSCHANNEL = "237500381880516609";
                TEAMSCHANNEL = "236904343859757057";
                GLOBALCHANNEL = "237788799982239745";


                //Roles
                ADMINROLE = "236897540262330370";
                DEVROLE = "237461438548017154";
                MODROLE = "236897692599451648";
                STREAMERROLE = "240689877325185024";
                PCMOBILEROLE = "237500925982277634";
                CONSOLEROLE = "237500960576897024";

                SERVERID = "236897364697284618";
                break;
                
            case Dev:
                //Channels
                LOGSCHANNEL = "247394226319392768";
                CODESCHANNEL = "247394202844004363";
                BOTCOMMANDSCHANNEL = "247394319449849856";
                TEAMSCHANNEL = "253244926618370048";
                GLOBALCHANNEL = "247393304151326721";

                //Roles
                ADMINROLE = "247394012162424843";
                DEVROLE = "247395805567123467";
                MODROLE = "247395922177163264";
                STREAMERROLE = "247395963243593728";
                PCMOBILEROLE = "247395991127457792";
                CONSOLEROLE = "247396031422005248";

                SERVERID = "247393304151326721";
                break;
                
            case Lyya:
                //Channels
                LOGSCHANNEL = "257917868811878402";
                CODESCHANNEL = "247394202844004363";
                BOTCOMMANDSCHANNEL = "257920671345016834";
                TEAMSCHANNEL = "257917963506548737";
                GLOBALCHANNEL = "257915045487443981";

                //Roles
                ADMINROLE = "257921387648253953";
                DEVROLE = "257921576333344769";
                MODROLE = "257919290366230529";
                STREAMERROLE = "257921850913456128";
                PCMOBILEROLE = "257921732491476993";
                CONSOLEROLE = "257921797888933890";

                SERVERID = "257915045487443981";
                break;
        }
    }
}
