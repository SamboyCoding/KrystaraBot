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
        LIVE,
        DEV,
        LYYA
    }

    public static final RuntimeEnvironment ENVIRONMENT = RuntimeEnvironment.LYYA;

    public static String LOGSCHANNEL, CODESCHANNEL, BOTCOMMANDSCHANNEL, TEAMSCHANNEL, GLOBALCHANNEL, CHATFRENCH, CHATGERMAN, CHATITALIAN, CHATSPANISH;
    public static String ADMINROLE, DEVROLE, MODROLE, STREAMERROLE, PCMOBILEROLE, CONSOLEROLE, QUIZMASTERROLE, FRENCHROLE, GERMANROLE, ITALIANROLE, SPANISHROLE;
    public static String SERVERID;
    public static String MYTOKEN; //Set in main method.
    public static String MYID; //Gets set once readyevent done.

    public IDReference()
    {
        switch (ENVIRONMENT)
        {
            case LIVE:
                //Channels
                LOGSCHANNEL = "237546188994379776";
                CODESCHANNEL = "237455339857903617";
                BOTCOMMANDSCHANNEL = "237500381880516609";
                TEAMSCHANNEL = "236904343859757057";
                GLOBALCHANNEL = "237788799982239745";
                CHATFRENCH = "262240512268959745";
                CHATGERMAN = "262240429435650048";
                CHATITALIAN = "268811063712153601";
                CHATSPANISH = "262240467989561354";

                //Roles
                ADMINROLE = "236897540262330370";
                DEVROLE = "237461438548017154";
                MODROLE = "236897692599451648";
                STREAMERROLE = "240689877325185024";
                PCMOBILEROLE = "237500925982277634";
                CONSOLEROLE = "237500960576897024";
                QUIZMASTERROLE = "258958497012645888";
                FRENCHROLE = "262240971817877535";
                GERMANROLE = "262240808378302464";
                ITALIANROLE = "268810835890012170";
                SPANISHROLE = "262241006710161408";

                SERVERID = "236897364697284618";
                break;

            case DEV:
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
                QUIZMASTERROLE = "258958152815345674";

                SERVERID = "247393304151326721";
                break;

            case LYYA:
                //Channels
                LOGSCHANNEL = "257917868811878402";
                CODESCHANNEL = "247394202844004363";
                BOTCOMMANDSCHANNEL = "257920671345016834";
                TEAMSCHANNEL = "257917963506548737";
                GLOBALCHANNEL = "257915045487443981";
                CHATFRENCH = "268803308783665152";
                CHATGERMAN = "268803350466789376";
                CHATITALIAN = "268811997032742912";
                CHATSPANISH = "268803429550260234";

                //Roles
                ADMINROLE = "257921387648253953";
                DEVROLE = "257921576333344769";
                MODROLE = "257919290366230529";
                STREAMERROLE = "257921850913456128";
                PCMOBILEROLE = "257921732491476993";
                CONSOLEROLE = "257921797888933890";
                QUIZMASTERROLE = "258992463216246785";

                SERVERID = "257915045487443981";
                break;
        }
    }
}
