package me.samboycoding.krystarabot.quiz;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Alternative question factory for the quiz
 *
 * @author Emily Ash
 */
public class AshQuizQuestionFactory implements QuizQuestionFactory
{

    private static class AshQuizQuestion extends QuizQuestion
    {

        private final JSONObject question;

        public AshQuizQuestion(JSONObject q)
        {
            question = q;
        }

        @Override
        public String getQuestionText()
        {
            return question.getString("QuestionText");
        }

        @Override
        public String getAnswerText(int index)
        {
            return question.getJSONArray("Answers").getJSONObject(index).getString("Text");
        }

        @Override
        public int getCorrectAnswerIndex()
        {
            return question.getInt("CorrectAnswerIndex");
        }

        @Override
        public Difficulty getDifficulty()
        {
            return Difficulty.fromInteger(question.getInt("Difficulty"));
        }

        @Override
        public long getRandomSeed()
        {
            return question.getLong("Seed");
        }

        @Override
        public String getQuestionSecondaryText()
        {
            if (!question.has("QuestionSecondaryText"))
            {
                return "";
            }

            return question.getString("QuestionSecondaryText");
        }

        @Override
        public URL getQuestionImageUrl()
        {
            if (!question.has("QuestionImage"))
            {
                return null;
            }

            String imageStr = question.getString("QuestionImage");
            URL result = null;
            if (StringUtils.isEmpty(imageStr))
            {
                return null;
            }

            try
            {
                result = new URL(imageStr);
            } catch (MalformedURLException e)
            {
            }

            return result;
        }
    }

    public AshQuizQuestionFactory()
    {
    }

    private static ArrayList<QuizQuestionType> getTypesForDifficulty(QuizQuestion.Difficulty difficulty)
    {
        ArrayList<QuizQuestionType> result = new ArrayList<>();

        for (int i = 0; i < QuizQuestionType.Count; i++)
        {
            QuizQuestionType type = QuizQuestionType.fromInteger(i);
            if (type.difficulty == difficulty)
            {
                result.add(type);
            }
        }

        return result;
    }

    private QuizQuestion[] getQuestionsFromUrl(URL url) throws IOException
    {
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        String encoding = con.getContentEncoding();
        encoding = encoding == null ? "UTF-8" : encoding;
        String body = IOUtils.toString(in, encoding);
        JSONObject response = new JSONObject(body);
        JSONArray questions = response.getJSONArray("Questions");
        int count = questions.length();
        QuizQuestion[] results = new QuizQuestion[count];
        for (int i = 0; i < count; i++)
        {
            results[i] = new AshQuizQuestion(questions.getJSONObject(i));
        }
        return results;
    }

    /**
     * Generates a random question of the specified type.
     *
     * @param count
     * @param r The random number generator to use.
     * @param type The type of question to create.
     * @return A new question of the specified type.
     * @throws java.net.MalformedURLException
     */
    @Override
    public QuizQuestion[] getQuestions(int count, Random r, QuizQuestionType type) throws MalformedURLException, IOException
    {
        return getQuestionsFromUrl(
                new URL("http://ashtender.com/gems/api/quiz?type=" + type.name() + "&count=" + count + "&seed=" + (r.nextInt() & 0xffffffffL)));
    }

    /**
     * Generates a random question of the specified difficulty, and a random
     * type.
     *
     * @param count The number of questions to get
     * @param r The random number generator to use.
     * @param difficulty The difficulty of question to create.
     * @return A new question of the specified type.
     * @throws java.net.MalformedURLException
     */
    @Override
    public QuizQuestion[] getQuestions(int count, Random r, QuizQuestion.Difficulty difficulty) throws MalformedURLException, IOException
    {
        return getQuestionsFromUrl(
                new URL("http://ashtender.com/gems/api/quiz?difficulty=" + difficulty.ordinal() + "&count=" + count + "&seed=" + (r.nextInt() & 0xffffffffL)));
    }

    /**
     * Generates a random question of a random type (and thereby a random
     * difficulty).
     *
     * @param count The number of questions to get
     * @param r The random number generator to use.
     * @return A new question of the specified type.
     * @throws java.net.MalformedURLException
     * @throws java.io.IOException
     */
    @Override
    public QuizQuestion[] getQuestions(int count, Random r) throws MalformedURLException, IOException
    {
        return getQuestionsFromUrl(
                new URL("http://ashtender.com/gems/api/quiz?count=" + count + "&seed=" + (r.nextInt() & 0xffffffffL)));
    }
}
