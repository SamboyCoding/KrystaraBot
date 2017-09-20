package me.samboycoding.krystarabot.utilities;

/**
 * Class to contain references for various IDs
 *
 * @author Sam
 */
public class IDReference
{

    public static final RuntimeEnvironment ENVIRONMENT = RuntimeEnvironment.DEV;
    public static String MYTOKEN; //Set in Main method.

    public static long LOGSCHANNEL, CODESCHANNEL, BOTCOMMANDSCHANNEL, TEAMSCHANNEL, GLOBALCHANNEL, CHATFRENCH, CHATGERMAN, CHATITALIAN, CHATSPANISH;
    public static long ADMINROLE, DEVROLE, MODROLE, STREAMERROLE, PCMOBILEROLE, CONSOLEROLE, QUIZMASTERROLE, FRENCHROLE, GERMANROLE, ITALIANROLE, SPANISHROLE;
    public static long SERVERID;

    public enum RuntimeEnvironment {
        LIVE,
        DEV,
        LYYA
    }
    public static long MYID; //Gets set once readyevent done.

    public IDReference()
    {
        switch (ENVIRONMENT)
        {
            case LIVE:
                //Channels
                LOGSCHANNEL = 237546188994379776L;
                CODESCHANNEL = 237455339857903617L;
                BOTCOMMANDSCHANNEL = 237500381880516609L;
                TEAMSCHANNEL = 236904343859757057L;
                GLOBALCHANNEL = 237788799982239745L;
                CHATFRENCH = 262240512268959745L;
                CHATGERMAN = 262240429435650048L;
                CHATITALIAN = 268811063712153601L;
                CHATSPANISH = 262240467989561354L;

                //Roles
                ADMINROLE = 236897540262330370L;
                DEVROLE = 237461438548017154L;
                MODROLE = 236897692599451648L;
                STREAMERROLE = 240689877325185024L;
                PCMOBILEROLE = 237500925982277634L;
                CONSOLEROLE = 237500960576897024L;
                QUIZMASTERROLE = 258958497012645888L;
                FRENCHROLE = 262240971817877535L;
                GERMANROLE = 262240808378302464L;
                ITALIANROLE = 268810835890012170L;
                SPANISHROLE = 262241006710161408L;

                SERVERID = 236897364697284618L;
                break;

            case DEV:
                //Channels
                LOGSCHANNEL = 247394226319392768L;
                CODESCHANNEL = 247394202844004363L;
                BOTCOMMANDSCHANNEL = 247394319449849856L;
                TEAMSCHANNEL = 253244926618370048L;
                GLOBALCHANNEL = 247393304151326721L;

                //Roles
                ADMINROLE = 247394012162424843L;
                DEVROLE = 247395805567123467L;
                MODROLE = 247395922177163264L;
                STREAMERROLE = 247395963243593728L;
                PCMOBILEROLE = 247395991127457792L;
                CONSOLEROLE = 247396031422005248L;
                QUIZMASTERROLE = 258958152815345674L;

                SERVERID = 247393304151326721L;
                break;

            case LYYA:
                //Channels
                LOGSCHANNEL = 257917868811878402L;
                CODESCHANNEL = 247394202844004363L;
                BOTCOMMANDSCHANNEL = 257920671345016834L;
                TEAMSCHANNEL = 257917963506548737L;
                GLOBALCHANNEL = 257915045487443981L;
                CHATFRENCH = 268803308783665152L;
                CHATGERMAN = 268803350466789376L;
                CHATITALIAN = 268811997032742912L;
                CHATSPANISH = 268803429550260234L;

                //Roles
                ADMINROLE = 257921387648253953L;
                DEVROLE = 257921576333344769L;
                MODROLE = 257919290366230529L;
                STREAMERROLE = 257921850913456128L;
                PCMOBILEROLE = 257921732491476993L;
                CONSOLEROLE = 257921797888933890L;
                QUIZMASTERROLE = 258992463216246785L;

                SERVERID = 257915045487443981L;
                break;
        }
    }
}
