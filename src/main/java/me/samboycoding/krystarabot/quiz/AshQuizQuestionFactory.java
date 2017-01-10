package me.samboycoding.krystarabot.quiz;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import me.samboycoding.krystarabot.gemdb.AshClient;
import org.apache.commons.lang3.StringUtils;

/**
 * Alternative question factory for the quiz
 *
 * @author Emily Ash
 */
public class AshQuizQuestionFactory implements QuizQuestionFactory
{

    private static class AshQuizQuestion extends QuizQuestion
    {

        private static class Answer
        {

            private String text = "";
            private String detailsLink = "";

            public String getText()
            {
                return text;
            }

            public String getDetailsLink()
            {
                return detailsLink;
            }
        };

        private long seed = -1;
        private String type = "";
        private int difficulty = 0;
        private String questionText = "";
        private String questionSecondaryText = "";
        private String questionImage = "";
        private int correctAnswerIndex = -1;
        private ArrayList<Answer> answers = new ArrayList<>();

        @Override
        public String getQuestionText()
        {
            return questionText;
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getText();
        }

        @Override
        public int getCorrectAnswerIndex()
        {
            return correctAnswerIndex;
        }

        @Override
        public Difficulty getDifficulty()
        {
            return Difficulty.fromInteger(difficulty);
        }

        @Override
        public long getRandomSeed()
        {
            return seed;
        }

        @Override
        public String getQuestionSecondaryText()
        {
            return questionSecondaryText;
        }

        @Override
        public URL getQuestionImageUrl()
        {
            URL result = null;

            if (StringUtils.isEmpty(questionImage))
            {
                return null;
            }

            try
            {
                result = new URL(questionImage);
            } catch (MalformedURLException e)
            {
            }

            return result;
        }
    }

    private static class AshQuizResponse
    {

        private ArrayList<AshQuizQuestion> questions = new ArrayList<>();

        public List<AshQuizQuestion> getQuestions()
        {
            return Collections.unmodifiableList(this.questions);
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

    private QuizQuestion[] getQuestionsFromQuery(String apiPathAndQuery) throws IOException
    {
        AshQuizResponse response = AshClient.query(apiPathAndQuery, AshQuizResponse.class);
        return response.getQuestions().toArray(new QuizQuestion[0]);
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
        return getQuestionsFromQuery(
                "quiz?type=" + type.name() + "&count=" + count + "&seed=" + (r.nextInt() & 0xffffffffL));
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
        return getQuestionsFromQuery(
                "quiz?difficulty=" + difficulty.ordinal() + "&count=" + count + "&seed=" + (r.nextInt() & 0xffffffffL));
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
        return getQuestionsFromQuery(
                "quiz?count=" + count + "&seed=" + (r.nextInt() & 0xffffffffL));
    }
}
