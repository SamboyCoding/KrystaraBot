package me.samboycoding.krystarabot.utilities;

/**
 * Class to contain references for various IDs
 *
 * @author Sam
 */
public class IDReference
{
    
    public enum ChannelID
    {
        //LOGS("247394226319392768"), CODES("247394202844004363"); //Testing
        LOGS("237546188994379776"), CODES("237455339857903617"); //Real
        
        private final String id;
        
        private ChannelID(String ID)
        {
            this.id = ID;
        }
        
        @Override
        public String toString()
        {
            return id;
        }
    }
    
    public enum RoleID
    {
        //ADMIN("247394012162424843"), DEV("247395805567123467"), MODERATOR("247395922177163264"), STREAMER("247395963243593728"), MUTED("247396046416773121"), PCMOBILE("247395991127457792"), CONSOLE("247396031422005248"); //Testing
        ADMIN("236897540262330370"), DEV("237461438548017154"), MODERATOR("236897692599451648"), STREAMER("240689877325185024"), MUTED("241943239374929920"), PCMOBILE("237500925982277634"), CONSOLE("237500960576897024"); //Real
        
        private final String id;
        
        private RoleID(String ID)
        {
            this.id = ID;
        }
        
        @Override
        public String toString()
        {
            return id;
        }
    }
    //public static final String SERVERID = "247393304151326721"; //Testing
    public static final String SERVERID = "236897364697284618"; //Real
    
    public static final String MYTOKEN = "MjQ3Mzk2MzA0NDc1NTIxMDI1.CwooOQ.PAiqYMD92L-dEYb5RqDuUGoIHQY";
        
    public static String MYID = null; //Gets set once readyevent done.
}
