package me.samboycoding.krystarabot.gemdb;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.stream.Stream;
import me.samboycoding.krystarabot.Language;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.apache.commons.io.IOUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class AshClient
{

    private static final Gson GSON = new Gson();

    public static <T> T query(String apiPathAndQuery, Class<T> c) throws IOException
    {
        return query(apiPathAndQuery, c, Language.ENGLISH);
    }

    public static <T> T query(String apiPathAndQuery, Class<T> c, Language lang) throws IOException
    {
        URL url = new URL("http://ashtender.com/gems/" + lang.getCode() + "/api/" + apiPathAndQuery);
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        String encoding = con.getContentEncoding();
        encoding = (encoding == null) ? "UTF-8" : encoding;
        String body = IOUtils.toString(in, encoding);
        return GSON.fromJson(body, c);
    }

    public static <T extends Nameable> T getSingleResult(IChannel chnl, List<T> resultList, String searchName, Language lang)
            throws MissingPermissionsException, RateLimitException, DiscordException
    {
        String searchNameLower = searchName.toLowerCase();

        int prefixMatchCount = 0;
        T prefixMatch = null;

        if (resultList.isEmpty())
        {
            String message = lang.localizeFormat(Language.LocString.NO_THING_FOUND_FORMAT, searchName);
            chnl.sendMessage(message);
            return null;
        }

        // Search for an exact match in the result list
        for (T result : resultList)
        {
            String resultNameLower = result.getName().toLowerCase();

            if (resultNameLower.equals(searchNameLower))
            {
                return result;
            }

            // Count prefix matches
            if (resultNameLower.startsWith(searchNameLower))
            {
                prefixMatchCount++;
                prefixMatch = result;
            }
        }

        // No exact match; was there only one prefix match?
        if (prefixMatchCount == 1)
        {
            // Assume it's correct
            return prefixMatch;
        }

        // No single prefix match; was there only one result?
        if (resultList.size() == 1)
        {
            T result = resultList.get(0);

            // Only one (fuzzy) match; assume it's correct but show a warning
            String message = lang.localizeFormat(Language.LocString.NO_THING_FOUND_FORMAT, searchName) + " "
                    + lang.localizeFormat(Language.LocString.ASSUMING_THING_FORMAT, result.getName());
            chnl.sendMessage(message);

            return result;
        }

        // Ambiguity
        Stream<String> str = resultList.stream().map(t -> t.getName());
        Utilities.sendDisambiguationMessage(chnl, lang.localizeFormat(Language.LocString.TERM_IS_AMBIGUOUS_FORMAT, searchName), str::iterator, lang);

        return null;
    }
}
