package me.samboycoding.krystarabot.gemdb;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.io.FileUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 *
 * @author Emily Ash
 */
public class GemsQueryRunner
{

    private static QueryRunner queryRunner;
    private static final Object lock = new Object();

    public static QueryRunner getQueryRunner() throws SQLException, IOException
    {
        synchronized (lock)
        {
            if (queryRunner == null)
            {
                String userData = null;

                // Look for a password file
                try
                {
                    userData = FileUtils.readFileToString(new File("../gems.user"), Charset.defaultCharset());
                } catch (IOException e)
                {
                    userData = FileUtils.readFileToString(new File("./gems.user"), Charset.defaultCharset());
                }

                String[] creds = userData.split(":");

                BasicDataSource basicDataSource = new BasicDataSource();
                basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                basicDataSource.setUsername(creds[0]);
                basicDataSource.setPassword(creds[1]);
                basicDataSource.setUrl("jdbc:mysql://mysql.ashtender.com:3306/gems");
                basicDataSource.setValidationQuery("SELECT 1");
                queryRunner = new QueryRunner(basicDataSource);
            }
            return queryRunner;
        }
    }

    public static <T extends Nameable> T runQueryForSingleResultByName(IChannel chnl, String query, String typeString, Class<T> type, String name)
            throws SQLException, IOException, MissingPermissionsException, RateLimitException, DiscordException
    {
        ResultSetHandler<List<T>> handler = new BeanListHandler<>(type);
        QueryRunner run = getQueryRunner();
        T result = null;

        List<T> list = run.query(query, handler, name + "%");
        if (list.isEmpty())
        {
            String message = "No " + typeString + " `" + name + "` found.";
            query = query.replace("LIKE ?", "SOUNDS LIKE ?");
            list = run.query(query, handler, name);
            if (list.size() == 1)
            {
                message += "  Assuming '" + list.get(0).getName() + "'...";
                result = list.get(0);
            }
            chnl.sendMessage(message);
        } else if ((list.size() > 1) && (!list.get(0).getName().toLowerCase().equals(name.toLowerCase())))
        {
            Stream<String> str = list.stream().map(t -> t.getName());
            Utilities.sendDisambiguationMessage(chnl, "Search term \"" + name + "\" is ambiguous.", str::iterator);
        } else
        {
            result = list.get(0);
        }

        return result;
    }
}
