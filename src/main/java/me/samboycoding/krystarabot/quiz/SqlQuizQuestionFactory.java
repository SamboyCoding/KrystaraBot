package me.samboycoding.krystarabot.quiz;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import me.samboycoding.krystarabot.GameData;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.GemsQueryRunner;
import me.samboycoding.krystarabot.gemdb.HeroClass;
import me.samboycoding.krystarabot.gemdb.Kingdom;
import me.samboycoding.krystarabot.gemdb.Troop;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

/**
 * Alternative question factory for the quiz
 *
 * @author Emily Ash
 */
public class SqlQuizQuestionFactory implements QuizQuestionFactory
{

    private QueryRunner runner;

    public SqlQuizQuestionFactory(QueryRunner run)
    {
        runner = run;
    }

    /**
     * Base class for most questions that use random items in the world data (or
     * features of troops) as answers.
     */
    private static abstract class QuizQuestion_RandomBase<T> extends QuizQuestion
    {

        private final QuizQuestionType type;
        private final QueryRunner runner;
        private final Class<T> classType;
        protected ArrayList<T> answers;
        protected T correctAnswer;
        protected Random random;
        protected long seed;
        protected int myRand;

        public QuizQuestion_RandomBase(Random r, QueryRunner run, QuizQuestionType t, Class<T> ct)
        {
            random = r;
            seed = Utilities.getSeed(r);
            myRand = random.nextInt();
            type = t;
            runner = run;
            classType = ct;
        }

        public QuizQuestion_RandomBase initialize() throws SQLException
        {
            HashMap<Object, Object> keyMap = new HashMap<>();
            ArrayList<Object> keys;

            do
            {
                answers = new ArrayList<>();
                correctAnswer = null;

                // Call back for the "answers" query
                String queryString = getAnswersQuery() + " ORDER BY RAND(" + myRand + ") LIMIT " + ANSWER_COUNT;
                ResultSetHandler<List<T>> handler = new BeanListHandler<>(classType);
                List<T> results = runner.query(queryString, handler);

                for (T answer : results)
                {
                    if (correctAnswer == null)
                    {
                        correctAnswer = answer;
                    }

                    answers.add(answer);
                    if (answers.size() >= ANSWER_COUNT)
                    {
                        break;
                    }
                }

                if (answers.size() < ANSWER_COUNT)
                {
                    throw new SQLException("Couldn't find enough unique answers!");
                }

                myRand = random.nextInt();
            } while (!areAnswersValid());

            // Shuffle the answers once we're done
            Collections.shuffle(answers, random);

            return this;
        }

        @Override
        public Difficulty getDifficulty()
        {
            return type.difficulty;
        }

        @Override
        public long getRandomSeed()
        {
            return seed;
        }

        protected abstract String getAnswersQuery();

        protected Boolean areAnswersValid()
        {
            return true;
        }

        public int getCorrectAnswerIndex()
        {
            return answers.indexOf(correctAnswer);
        }
    }

    /**
     * Base class for most questions that want troops with a specific feature as
     * answers.
     */
    private static abstract class QuizQuestion_FilteredBase<T> extends QuizQuestion
    {

        private final QuizQuestionType type;
        private final QueryRunner runner;
        private final Class<T> classType;
        protected ArrayList<T> answers;
        protected T correctAnswer;
        protected Random random;
        protected long seed;
        protected int myRand;

        public QuizQuestion_FilteredBase(Random r, QueryRunner run, QuizQuestionType t, Class<T> ct)
        {
            random = r;
            seed = Utilities.getSeed(random);
            myRand = random.nextInt();
            type = t;
            runner = run;
            classType = ct;
        }

        public QuizQuestion_FilteredBase initialize() throws SQLException
        {
            do
            {
                answers = new ArrayList<>();

                // Call back for the "answers" query
                String queryString = getCorrectAnswersQuery() + " ORDER BY RAND(" + myRand + ")";
                ResultSetHandler<List<T>> handler = new BeanListHandler<>(classType);
                List<T> correctAnswers = runner.query(queryString, handler);

                if (correctAnswers.size() < 1)
                {
                    throw new SQLException("Couldn't find a correct answer!");
                }

                for (T answer : correctAnswers)
                {
                    if (correctAnswer == null)
                    {
                        correctAnswer = answer;
                    }
                }

                answers.add(correctAnswer);

                // Call back for the "incorrect answers" query
                queryString = getIncorrectAnswersQuery(correctAnswers) + " ORDER BY RAND(" + myRand + ") LIMIT " + (ANSWER_COUNT - 1);
                List<T> incorrectAnswers = runner.query(queryString, handler);

                for (T answer : incorrectAnswers)
                {
                    answers.add(answer);
                    if (answers.size() >= ANSWER_COUNT)
                    {
                        break;
                    }
                }

                if (answers.size() < ANSWER_COUNT)
                {
                    throw new SQLException("Couldn't find enough unique answers!");
                }

                myRand = random.nextInt();
            } while (!areAnswersValid());

            // Shuffle the answers once we're done
            Collections.shuffle(answers, random);

            return this;
        }

        protected abstract String getCorrectAnswersQuery();

        protected abstract String getIncorrectAnswersQuery(List<T> correctAnswers);

        @Override
        public Difficulty getDifficulty()
        {
            return type.difficulty;
        }

        @Override
        public long getRandomSeed()
        {
            return seed;
        }

        protected Boolean areAnswersValid()
        {
            return true;
        }

        @Override
        public int getCorrectAnswerIndex()
        {
            return answers.indexOf(correctAnswer);
        }
    }

    /**
     * Base class for most questions that want troops with a specific effect in
     * their spell.
     */
    private static abstract class QuizQuestion_TroopsSpellFiltered extends QuizQuestion_FilteredBase<Troop>
    {

        public QuizQuestion_TroopsSpellFiltered(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t, Troop.class);
        }

        @Override
        protected String getCorrectAnswersQuery()
        {
            String queryEffects = "('" + String.join("', '", getEffectNames()) + "')";
            return "SELECT Troops.Name, Troops.Id, Spells.Name AS SpellName "
                    + "FROM Troops "
                    + "INNER JOIN Spells ON Troops.SpellId=Spells.Id AND Spells.Language=Troops.Language "
                    + "INNER JOIN SpellSteps ON SpellSteps.SpellId=Spells.Id AND ((SpellSteps.Type IN " + queryEffects + ") OR " + getExtraClause() + ") "
                    + "WHERE Troops.ReleaseDate<NOW() AND Troops.Language='en-US' "
                    + "GROUP BY SpellSteps.SpellId";
        }

        @Override
        protected String getIncorrectAnswersQuery(List<Troop> correctAnswers)
        {
            String[] queryNotIdArray = correctAnswers.stream().map(a -> Integer.toString(a.getId())).toArray(String[]::new);
            String queryNotIds = String.join(", ", queryNotIdArray);

            return "SELECT Troops.Name, Troops.Id "
                    + "FROM Troops "
                    + "WHERE Troops.ReleaseDate<NOW() AND Troops.Language='en-US' AND Troops.Id NOT IN (" + queryNotIds + ")";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }

        abstract protected String[] getEffectNames();

        protected String getExtraClause()
        {
            return "(FALSE)";
        }
    }

    /**
     * Base class for most questions that use troops (or features of troops) as
     * answers.
     */
    private static abstract class QuizQuestion_Troops extends QuizQuestion_RandomBase<Troop>
    {

        public QuizQuestion_Troops(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t, Troop.class);
        }
    }

    /**
     * Base class for most questions that use kingdoms (or features of kingdoms)
     * as answers.
     */
    private static abstract class QuizQuestion_Kingdoms extends QuizQuestion_RandomBase<Kingdom>
    {

        public QuizQuestion_Kingdoms(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t, Kingdom.class);
        }
    }

    /**
     * Base class for most questions that use classes (or features of classes)
     * as answers.
     */
    private static abstract class QuizQuestion_Classes extends QuizQuestion_RandomBase<HeroClass>
    {

        public QuizQuestion_Classes(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t, HeroClass.class);
        }
    }

    /**
     * Asks a user to identify a troop's kingdom.
     */
    private static class QuizQuestion_TroopToKingdom extends QuizQuestion_Troops
    {

        public QuizQuestion_TroopToKingdom(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **kingdom** is **" + correctAnswer.getName() + "** from?";
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT * FROM ( "
                    + "SELECT Troops.Name, Troops.Id, Troops.KingdomId, Kingdoms.Name AS KingdomName "
                    + "FROM Troops "
                    + "INNER JOIN Kingdoms ON Kingdoms.Id=Troops.KingdomId AND Kingdoms.Language=Troops.Language "
                    + "WHERE Troops.ReleaseDate<NOW() AND Troops.Language='en-US' "
                    + "ORDER BY RAND(" + myRand + ") "
                    + ") Troops "
                    + "GROUP BY Troops.KingdomId";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getKingdomName();
        }
    }

    /**
     * Asks a user to identify which troop is from the specified kingdom.
     */
    private static class QuizQuestion_KingdomToTroop extends QuizQuestion_TroopToKingdom
    {

        public QuizQuestion_KingdomToTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** comes from the kingdom of **" + correctAnswer.getKingdomName() + "**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify a troop's spell.
     */
    private static class QuizQuestion_TroopToSpell extends QuizQuestion_Troops
    {

        public QuizQuestion_TroopToSpell(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What is the name of **" + correctAnswer.getName() + "'s spell**?";
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT Troops.Name, Troops.Id, Spells.Name AS SpellName, Spells.Id AS SpellId "
                    + "FROM Troops "
                    + "LEFT JOIN Spells ON Spells.Id=Troops.SpellId AND Spells.Language=Troops.Language "
                    + "WHERE Troops.ReleaseDate<NOW() AND Troops.Language='en-US' ";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getSpellName();
        }
    }

    /**
     * Asks a user to identify a spell's troop.
     */
    private static class QuizQuestion_SpellToTroop extends QuizQuestion_TroopToSpell
    {

        public QuizQuestion_SpellToTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** uses the spell **\"" + correctAnswer.getSpellName() + "\"**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify a troop by its spell's art.
     */
    private static class QuizQuestion_SpellArtToTroop extends QuizQuestion_TroopToSpell
    {

        public QuizQuestion_SpellArtToTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop's spell** is pictured below?";
        }

        @Override
        public URL getQuestionImageUrl()
        {
            try
            {
                return new URL("http://ashtender.com/gems/assets/spells/" + correctAnswer.getSpellId() + "_small.jpg");
            } catch (MalformedURLException e)
            {
            }
            return null;
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify a troop by its card art.
     */
    private static class QuizQuestion_CardArtToTroop extends QuizQuestion_Troops
    {

        public QuizQuestion_CardArtToTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** is pictured below?";
        }

        @Override
        public URL getQuestionImageUrl()
        {
            try
            {
                return new URL("http://ashtender.com/gems/assets/cards/" + correctAnswer.getFileBase() + "_small.jpg");
            } catch (MalformedURLException e)
            {
            }
            return null;
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT Troops.Name, Troops.FileBase "
                    + "FROM Troops "
                    + "WHERE Troops.ReleaseDate<NOW() AND Troops.Language='en-US' ";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify a troop's type.
     */
    private static class QuizQuestion_TroopToType extends QuizQuestion_Troops
    {

        public QuizQuestion_TroopToType(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What is/are **" + correctAnswer.getName() + "'s type(s)**?";
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT * FROM ( "
                    + "SELECT Troops.Name, Troops.Id, Troops.Type "
                    + "FROM Troops "
                    + "WHERE Troops.ReleaseDate<NOW() AND Troops.Language='en-US' AND Troops.Name!=Troops.Type "
                    + "ORDER BY RAND(" + myRand + ") "
                    + ") Troops "
                    + "GROUP BY Troops.Type";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getType();
        }
    }

    /**
     * Asks a user to identify which troop is of the specified type.
     */
    private static class QuizQuestion_TypeToTroop extends QuizQuestion_TroopToType
    {

        public QuizQuestion_TypeToTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** is of type **" + getTroopType(correctAnswer) + "**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }

        private String getTroopType(Troop troop)
        {
            String type = troop.getType();
            if (!type.contains("-"))
            {
                type += " only";
            }
            return type;
        }
    }

    /**
     * Asks a user to identify one of a troop's traits.
     */
    private static class QuizQuestion_TroopToTrait extends QuizQuestion_Troops
    {

        protected final int traitIndex;

        public QuizQuestion_TroopToTrait(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
            traitIndex = r.nextInt(3);
        }

        @Override
        public String getQuestionText()
        {
            return "What is the name of one of **" + correctAnswer.getName() + "'s traits**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getTraitName(traitIndex);
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT * FROM ( "
                    + "SELECT Troops.Name, Troops.Id, T0.Name AS TraitName0, T1.Name AS TraitName1, T2.Name AS TraitName2 "
                    + "FROM Troops "
                    + "INNER JOIN TroopTraits TT0 ON TT0.TroopId=Troops.Id AND TT0.CostIndex=0 AND TT0.TraitIndex=0 "
                    + "INNER JOIN TroopTraits TT1 ON TT1.TroopId=Troops.Id AND TT1.CostIndex=0 AND TT1.TraitIndex=1 "
                    + "INNER JOIN TroopTraits TT2 ON TT2.TroopId=Troops.Id AND TT2.CostIndex=0 AND TT2.TraitIndex=2 "
                    + "INNER JOIN Traits T0 ON T0.Code=TT0.Code AND T0.Language=Troops.Language "
                    + "INNER JOIN Traits T1 ON T1.Code=TT1.Code AND T1.Language=Troops.Language "
                    + "INNER JOIN Traits T2 ON T2.Code=TT2.Code AND T2.Language=Troops.Language "
                    + "WHERE Troops.ReleaseDate<NOW() AND Troops.Language='en-US'"
                    + "ORDER BY RAND(" + myRand + ") "
                    + ") Troops "
                    + "GROUP BY TraitName" + traitIndex;
        }

        @Override
        protected Boolean areAnswersValid()
        {
            HashMap<String, Boolean> seen = new HashMap<>();
            for (Troop answer : answers)
            {
                if (seen.containsKey(answer.getTraitName(0))
                        || seen.containsKey(answer.getTraitName(1))
                        || seen.containsKey(answer.getTraitName(2)))
                {
                    return false;
                }
                seen.put(answer.getTraitName(0), true);
                seen.put(answer.getTraitName(1), true);
                seen.put(answer.getTraitName(2), true);
            }

            return true;
        }
    }

    /**
     * Asks a user to identify which troop has the specified trait.
     */
    private static class QuizQuestion_TraitToTroop extends QuizQuestion_TroopToTrait
    {

        public QuizQuestion_TraitToTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** has the trait **\"" + correctAnswer.getTraitName(traitIndex) + "\"**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify a troop's color.
     */
    private static class QuizQuestion_TroopToColor extends QuizQuestion_Troops
    {

        public QuizQuestion_TroopToColor(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **mana colors** does **" + correctAnswer.getName() + "** use?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return getTroopColors(answers.get(index));
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT * FROM ( "
                    + "SELECT Troops.Name, Troops.Id, Troops.Colors "
                    + "FROM Troops "
                    + "WHERE Troops.ReleaseDate<NOW() AND Troops.Language='en-US'"
                    + "ORDER BY RAND(" + myRand + ") "
                    + ") Troops "
                    + "GROUP BY Troops.Colors";
        }

        protected String getTroopColors(Troop troop)
        {
            GemColor[] gemColors = GemColor.fromInteger(troop.getColors());
            String[] gemColorNames = Arrays.stream(gemColors).map(c -> c.name()).toArray(String[]::new);
            String result = String.join("/", gemColorNames);

            if (gemColorNames.length == 1)
            {
                result += " only";
            }

            return result;
        }
    }

    /**
     * Asks a user to identify which troop is of the specified color.
     */
    private static class QuizQuestion_ColorToTroop extends QuizQuestion_TroopToColor
    {

        public QuizQuestion_ColorToTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** is **" + getTroopColors(correctAnswer) + "**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify a troop's rarity.
     */
    private static class QuizQuestion_TroopToRarity extends QuizQuestion_Troops
    {

        public QuizQuestion_TroopToRarity(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What is **" + correctAnswer.getName() + "'s** base **rarity**?";
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT * FROM ( "
                    + "SELECT Troops.Name, Troops.Id, Troops.Rarity "
                    + "FROM Troops "
                    + "WHERE Troops.ReleaseDate<NOW() AND Troops.Language='en-US'"
                    + "ORDER BY RAND(" + myRand + ") "
                    + ") Troops "
                    + "GROUP BY Troops.Rarity";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getRarity();
        }
    }

    /**
     * Asks a user to identify which troop is of the specified rarity.
     */
    private static class QuizQuestion_RarityToTroop extends QuizQuestion_TroopToRarity
    {

        public QuizQuestion_RarityToTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** has a **base rarity** of **" + correctAnswer.getRarity() + "**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify a troop from its flavor text.
     */
    private static class QuizQuestion_FlavorTextToTroop extends QuizQuestion_Troops
    {

        public QuizQuestion_FlavorTextToTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** is this?";
        }

        @Override
        public String getQuestionSecondaryText()
        {
            return "_" + getTroopFlavorText(correctAnswer) + "_";
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT Troops.Name, Troops.Description "
                    + "FROM Troops "
                    + "WHERE Troops.ReleaseDate<NOW() AND Troops.Language='en-US' ";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }

        @Override
        protected Boolean areAnswersValid()
        {
            return !correctAnswer.getDescription().toLowerCase().contains(
                    correctAnswer.getName().toLowerCase());
        }

        private String getTroopFlavorText(Troop troop)
        {
            String flavorText = troop.getDescription();
            if (flavorText.endsWith("\n"))
            {
                flavorText = flavorText.substring(0, flavorText.length() - 1);
            }
            if (!flavorText.isEmpty())
            {
                switch (flavorText.charAt(flavorText.length() - 1))
                {
                    case '.':
                    case '?':
                    case '!':
                        break;

                    default:
                        flavorText += ".";
                        break;
                }
            }

            return flavorText;
        }
    }

    /**
     * Asks a user to identify which troop causes true damage.
     */
    private static class QuizQuestion_TrueDamageTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_TrueDamageTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** does **true damage** as part of its spell?";
        }

        @Override
        protected String[] getEffectNames()
        {
            return new String[]
            {
                "TrueDamage", "TrueSplashDamage", "TrueScatterDamage"
            };
        }
    }

    /**
     * Asks a user to identify which troop creates gems.
     */
    private static class QuizQuestion_CreateGemsTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_CreateGemsTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop creates gems** as part of its spell?";
        }

        @Override
        protected String[] getEffectNames()
        {
            return new String[]
            {
                "CreateGems", "CreateGems2Colors"
            };
        }
    }

    /**
     * Asks a user to identify which troop converts gems.
     */
    private static class QuizQuestion_ConvertGemsTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_ConvertGemsTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop converts gems** as part of its spell?";
        }

        @Override
        protected String[] getEffectNames()
        {
            return new String[]
            {
                "ConvertGems"
            };
        }
    }

    /**
     * Asks a user to identify which troop destroys, removes, or explodes gems.
     */
    private static class QuizQuestion_DestroyGemsTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_DestroyGemsTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop destroys, removes,** or **explodes gems** as part of its spell?";
        }

        @Override
        protected String[] getEffectNames()
        {
            return new String[]
            {
                "DestroyGems", "DestroyColor", "DestroyRow",
                "ExplodeGems", "ExplodeColor", "RemoveGems", "RemoveColor"
            };
        }
    }

    /**
     * Asks a user to identify which troop increases stats.
     */
    private static class QuizQuestion_IncreaseStatsTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_IncreaseStatsTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** can **heal** or **increase stats** of itself or another troop as part of its spell?";
        }

        @Override
        protected String[] getEffectNames()
        {
            return new String[]
            {
                "Heal", "IncreaseHealth", "IncreaseArmor",
                "IncreaseAttack", "IncreaseSpellPower", "IncreaseRandom", "IncreaseAllStats", "StealRandomStat",
                "StealAttack", "StealArmor", "StealLife", "StealMagic", "Consume", "ConsumeConditional"
            };
        }
    }

    /**
     * Asks a user to identify which troop decreases stats.
     */
    private static class QuizQuestion_DecreaseStatsTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_DecreaseStatsTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** can **decrease stats** of itself or another troop (excluding normal damage) as part of its spell?";
        }

        @Override
        protected String[] getEffectNames()
        {
            return new String[]
            {
                "DecreaseRandom", "DecreaseAttack",
                "DecreaseSpellPower", "DecreaseArmor", "DecreaseAllStats", "StealRandomStat", "StealAttack",
                "StealArmor", "StealLife", "StealMagic"
            };
        }
    }

    /**
     * Asks a user to identify which troop gives resources (Gold, Souls, Maps).
     */
    private static class QuizQuestion_GiveResourcesTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_GiveResourcesTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** has a spell that can be used to generate **Gold, Souls,** or **Maps**?";
        }

        @Override
        protected String[] getEffectNames()
        {
            return new String[]
            {
                "GiveGold", "GiveSouls", "GiveTreasureMaps"
            };
        }
    }

    /**
     * Asks a user to identify which troop gives extra turns.
     */
    private static class QuizQuestion_GiveExtraTurnTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_GiveExtraTurnTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** has a spell that can give an **extra turn**?";
        }

        @Override
        protected String[] getEffectNames()
        {
            return new String[]
            {
                "ExtraTurn", "ExtraTurnConditional"
            };
        }
    }

    /**
     * Asks a user to identify which troop summons or transforms.
     */
    private static class QuizQuestion_SummonTransformTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_SummonTransformTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** has a spell that can **summon** or **transform** itself or another troop?";
        }

        @Override
        protected String[] getEffectNames()
        {
            return new String[]
            {
                "SummoningNoError", "SummoningType", "SummoningConditional",
                "SummoningTypeConditional", "Transform", "TransformType", "TransformTypeConditional", "TransformEnemy"
            };
        }
    }

    /**
     * Asks a user to identify which troop drains mana.
     */
    private static class QuizQuestion_DrainManaTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_DrainManaTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** has a spell that can **drain** or **steal mana** from another troop?";
        }

        @Override
        protected String[] getEffectNames()
        {
            return new String[]
            {
                "DecreaseMana"
            };
        }
    }

    /**
     * Asks a user to identify which troop causes a specific effect.
     */
    private static class QuizQuestion_EffectsTroop extends QuizQuestion_TroopsSpellFiltered
    {

        private final EffectEntry effectEntry;

        private static class EffectEntry
        {

            public final String effectName;
            public final String effectSpellStepType;
            public final boolean isDebuff;

            public EffectEntry(String n, String t, boolean d)
            {
                effectName = n;
                effectSpellStepType = t;
                isDebuff = d;
            }
        }

        private static final EffectEntry[] EffectTable =
        {
            new EffectEntry("Barrier", "CauseBarrier", false),
            new EffectEntry("Burning", "CauseBurning", true),
            new EffectEntry("Death Mark", "CauseDeathMark", true),
            new EffectEntry("Disease", "CauseDisease", true),
            new EffectEntry("Entangle", "CauseEntangle", true),
            new EffectEntry("Frozen", "CauseFrozen", true),
            new EffectEntry("Hunter's Mark", "CauseHuntersMark", true),
            new EffectEntry("Poison", "CausePoison", true),
            new EffectEntry("Silence", "CauseSilence", true),
            new EffectEntry("Stun", "CauseStun", true),
            new EffectEntry("Web", "CauseWeb", true)
        };

        public QuizQuestion_EffectsTroop(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);

            effectEntry = EffectTable[r.nextInt(EffectTable.length)];
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** can cause **" + effectEntry.effectName + "** as part of its spell?";
        }

        @Override
        protected String[] getEffectNames()
        {
            ArrayList<String> effects = new ArrayList<>();
            effects.add(effectEntry.effectSpellStepType);
            if (effectEntry.isDebuff)
            {
                effects.add("RandomStatusEffect");
            }
            return effects.toArray(new String[0]);
        }
    }

    /**
     * Asks a user to identify which traitstone can be found in the specified
     * kingdom.
     */
    private static class QuizQuestion_KingdomToTraitstone extends QuizQuestion_Kingdoms
    {

        public QuizQuestion_KingdomToTraitstone(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **Arcane Traitstone** can be found in **" + correctAnswer.getName() + "**?";
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT * FROM ( "
                    + "SELECT Kingdoms.Name, Kingdoms.Id, Kingdoms.ExploreTraitstoneId, Traitstones.Name AS TraitstoneName "
                    + "FROM Kingdoms "
                    + "LEFT JOIN Traitstones ON Traitstones.Id=Kingdoms.ExploreTraitstoneId AND Traitstones.Language=Kingdoms.Language "
                    + "WHERE Kingdoms.Language='en-US' AND Kingdoms.IsUsed AND Kingdoms.IsFullKingdom "
                    + "ORDER BY RAND(" + myRand + ") "
                    + ") Kingdoms "
                    + "GROUP BY Kingdoms.ExploreTraitstoneId";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getExploreTraitstoneName();
        }
    }

    /**
     * Asks a user to identify which kingdom has the specified traitstone.
     */
    private static class QuizQuestion_TraitstoneToKingdom extends QuizQuestion_KingdomToTraitstone
    {

        public QuizQuestion_TraitstoneToKingdom(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "In which **kingdom** can you find **" + correctAnswer.getExploreTraitstoneName() + "**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify which stat can be increased in the specified
     * kingdom.
     */
    private static class QuizQuestion_KingdomToStat extends QuizQuestion_Kingdoms
    {

        public QuizQuestion_KingdomToStat(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **stat** is increased by reaching level 10 in the kingdom of **" + correctAnswer.getName() + "**?";
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT * FROM ( "
                    + "SELECT Kingdoms.Name, Kingdoms.Id, Kingdoms.LevelStat "
                    + "FROM Kingdoms "
                    + "WHERE Kingdoms.Language='en-US' AND Kingdoms.IsFullKingdom AND Kingdoms.IsUsed "
                    + "ORDER BY RAND(" + myRand + ") "
                    + ") Kingdoms "
                    + "GROUP BY Kingdoms.LevelStat";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getLevelStat();
        }
    }

    /**
     * Asks a user to identify which kingdom has the specified stat increase.
     */
    private static class QuizQuestion_StatToKingdom extends QuizQuestion_KingdomToStat
    {

        public QuizQuestion_StatToKingdom(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **kingdom** gives an increase to **" + correctAnswer.getLevelStat() + "** at level 10?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify a kingdom by its banner art.
     */
    private static class QuizQuestion_BannerArtToKingdom extends QuizQuestion_Kingdoms
    {

        public QuizQuestion_BannerArtToKingdom(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **kingdom's banner** is pictured below?";
        }

        @Override
        public URL getQuestionImageUrl()
        {
            try
            {
                return new URL("http://ashtender.com/gems/assets/banners/" + correctAnswer.getFileBase() + "_small.png");
            } catch (MalformedURLException e)
            {
            }
            return null;
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT Kingdoms.Name, Kingdoms.Id, Kingdoms.FileBase "
                    + "FROM Kingdoms "
                    + "WHERE Kingdoms.IsUsed AND Kingdoms.IsFullKingdom AND Kingdoms.Language='en-US'";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify a kingdom by its shield art.
     */
    private static class QuizQuestion_ShieldArtToKingdom extends QuizQuestion_Kingdoms
    {

        public QuizQuestion_ShieldArtToKingdom(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **kingdom's shield** is pictured below?";
        }

        @Override
        public URL getQuestionImageUrl()
        {
            try
            {
                return new URL("http://ashtender.com/gems/assets/shields/" + correctAnswer.getFileBase() + "_small.png");
            } catch (MalformedURLException e)
            {
            }
            return null;
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT Kingdoms.Name, Kingdoms.Id, Kingdoms.FileBase "
                    + "FROM Kingdoms "
                    + "WHERE Kingdoms.IsUsed AND Kingdoms.Language='en-US'";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify which color weapons have affinity with a given
     * class.
     */
    private static class QuizQuestion_ClassToBonusColor extends QuizQuestion_Classes
    {

        public QuizQuestion_ClassToBonusColor(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **color weapons** get bonus Magic when used by the **" + correctAnswer.getName() + "** class?";
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT * FROM ( "
                    + "SELECT Classes.Name, Classes.Id, Classes.Colors "
                    + "FROM Classes "
                    + "WHERE Classes.ReleaseDate<NOW() AND Classes.Language='en-US'"
                    + "ORDER BY RAND(" + myRand + ") "
                    + ") Classes "
                    + "GROUP BY Classes.Rarity";
        }

        @Override
        public String getAnswerText(int index)
        {
            return getBonusColorForClass(answers.get(index));
        }

        protected String getBonusColorForClass(HeroClass heroClass)
        {
            GemColor[] gemColors = GemColor.fromInteger(heroClass.getColors());
            String colorName = gemColors[0].name();
            return colorName.substring(0, 1).toUpperCase() + colorName.substring(1);
        }
    }

    /**
     * Asks a user to identify which class has affinity to the specified color.
     */
    private static class QuizQuestion_BonusColorToClass extends QuizQuestion_ClassToBonusColor
    {

        public QuizQuestion_BonusColorToClass(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **class** gets bonus Magic when using **" + getBonusColorForClass(correctAnswer) + "** weapons?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify one of a class's traits.
     */
    private static class QuizQuestion_ClassToTrait extends QuizQuestion_Classes
    {

        protected final int traitIndex;

        public QuizQuestion_ClassToTrait(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
            traitIndex = r.nextInt(3);
        }

        @Override
        public String getQuestionText()
        {
            return "What is the name of one of the **traits** of the **" + correctAnswer.getName() + "** class?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getTraitName(traitIndex);
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT * FROM ( "
                    + "SELECT Classes.Name, Classes.Id, T0.Name AS TraitName0, T1.Name AS TraitName1, T2.Name AS TraitName2 "
                    + "FROM Classes "
                    + "INNER JOIN TroopTraits TT0 ON TT0.TroopId=Classes.Id AND TT0.CostIndex=0 AND TT0.TraitIndex=0 "
                    + "INNER JOIN TroopTraits TT1 ON TT1.TroopId=Classes.Id AND TT1.CostIndex=0 AND TT1.TraitIndex=1 "
                    + "INNER JOIN TroopTraits TT2 ON TT2.TroopId=Classes.Id AND TT2.CostIndex=0 AND TT2.TraitIndex=2 "
                    + "INNER JOIN Traits T0 ON T0.Code=TT0.Code AND T0.Language=Classes.Language "
                    + "INNER JOIN Traits T1 ON T1.Code=TT1.Code AND T1.Language=Classes.Language "
                    + "INNER JOIN Traits T2 ON T2.Code=TT2.Code AND T2.Language=Classes.Language "
                    + "WHERE Classes.ReleaseDate<NOW() AND Classes.Language='en-US'"
                    + "ORDER BY RAND(" + myRand + ") "
                    + ") Classes "
                    + "GROUP BY TraitName" + traitIndex;
        }

        @Override
        protected Boolean areAnswersValid()
        {
            HashMap<String, Boolean> seen = new HashMap<>();
            for (HeroClass answer : answers)
            {
                if (seen.containsKey(answer.getTraitName(0))
                        || seen.containsKey(answer.getTraitName(1))
                        || seen.containsKey(answer.getTraitName(2)))
                {
                    return false;
                }
                seen.put(answer.getTraitName(0), true);
                seen.put(answer.getTraitName(1), true);
                seen.put(answer.getTraitName(2), true);
            }

            return true;
        }
    }

    /**
     * Asks a user to identify which class has the specified trait.
     */
    private static class QuizQuestion_TraitToClass extends QuizQuestion_ClassToTrait
    {

        public QuizQuestion_TraitToClass(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **class** has the trait **\"" + correctAnswer.getTraitName(traitIndex) + "\"**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
    }

    /**
     * Asks a user to identify a troop by its card art.
     */
    private static class QuizQuestion_ClassArtToClass extends QuizQuestion_Classes
    {

        public QuizQuestion_ClassArtToClass(Random r, QueryRunner run, QuizQuestionType t)
        {
            super(r, run, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **class** is pictured below?";
        }

        @Override
        public URL getQuestionImageUrl()
        {
            try
            {
                return new URL("http://ashtender.com/gems/assets/classes/" + correctAnswer.getId() + "_small.png");
            } catch (MalformedURLException e)
            {
            }
            return null;
        }

        @Override
        protected String getAnswersQuery()
        {
            return "SELECT Classes.Name, Classes.Id "
                    + "FROM Classes "
                    + "WHERE Classes.ReleaseDate<NOW() AND Classes.Language='en-US' ";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getName();
        }
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

    @FunctionalInterface
    interface QuestionCreator
    {

        QuizQuestion create(Random r, QueryRunner run, QuizQuestionType t) throws SQLException;
    }

    private final static HashMap<QuizQuestionType, QuestionCreator> CREATOR_MAP = initCreatorMap();

    private static HashMap<QuizQuestionType, QuestionCreator> initCreatorMap()
    {
        HashMap<QuizQuestionType, QuestionCreator> map = new HashMap<>();
        map.put(QuizQuestionType.TroopToKingdom, (r, run, t) -> new QuizQuestion_TroopToKingdom(r, run, t).initialize());
        map.put(QuizQuestionType.KingdomToTroop, (r, run, t) -> new QuizQuestion_KingdomToTroop(r, run, t).initialize());
        map.put(QuizQuestionType.TroopToSpell, (r, run, t) -> new QuizQuestion_TroopToSpell(r, run, t).initialize());
        map.put(QuizQuestionType.SpellToTroop, (r, run, t) -> new QuizQuestion_SpellToTroop(r, run, t).initialize());
        map.put(QuizQuestionType.TroopToType, (r, run, t) -> new QuizQuestion_TroopToType(r, run, t).initialize());
        map.put(QuizQuestionType.TypeToTroop, (r, run, t) -> new QuizQuestion_TypeToTroop(r, run, t).initialize());
        map.put(QuizQuestionType.TroopToColor, (r, run, t) -> new QuizQuestion_TroopToColor(r, run, t).initialize());
        map.put(QuizQuestionType.ColorToTroop, (r, run, t) -> new QuizQuestion_ColorToTroop(r, run, t).initialize());
        map.put(QuizQuestionType.TroopToRarity, (r, run, t) -> new QuizQuestion_TroopToRarity(r, run, t).initialize());
        map.put(QuizQuestionType.RarityToTroop, (r, run, t) -> new QuizQuestion_RarityToTroop(r, run, t).initialize());
        map.put(QuizQuestionType.TroopToTrait, (r, run, t) -> new QuizQuestion_TroopToTrait(r, run, t).initialize());
        map.put(QuizQuestionType.TraitToTroop, (r, run, t) -> new QuizQuestion_TraitToTroop(r, run, t).initialize());
        map.put(QuizQuestionType.FlavorTextToTroop, (r, run, t) -> new QuizQuestion_FlavorTextToTroop(r, run, t).initialize());
        map.put(QuizQuestionType.SpellArtToTroop, (r, run, t) -> new QuizQuestion_SpellArtToTroop(r, run, t).initialize());
        map.put(QuizQuestionType.CardArtToTroop, (r, run, t) -> new QuizQuestion_CardArtToTroop(r, run, t).initialize());
        map.put(QuizQuestionType.TrueDamageTroop, (r, run, t) -> new QuizQuestion_TrueDamageTroop(r, run, t).initialize());
        map.put(QuizQuestionType.CreateGemsTroop, (r, run, t) -> new QuizQuestion_CreateGemsTroop(r, run, t).initialize());
        map.put(QuizQuestionType.ConvertGemsTroop, (r, run, t) -> new QuizQuestion_ConvertGemsTroop(r, run, t).initialize());
        map.put(QuizQuestionType.DestroyGemsTroop, (r, run, t) -> new QuizQuestion_DestroyGemsTroop(r, run, t).initialize());
        map.put(QuizQuestionType.IncreaseStatsTroop, (r, run, t) -> new QuizQuestion_IncreaseStatsTroop(r, run, t).initialize());
        map.put(QuizQuestionType.DecreaseStatsTroop, (r, run, t) -> new QuizQuestion_DecreaseStatsTroop(r, run, t).initialize());
        map.put(QuizQuestionType.GiveResourcesTroop, (r, run, t) -> new QuizQuestion_GiveResourcesTroop(r, run, t).initialize());
        map.put(QuizQuestionType.GiveExtraTurnTroop, (r, run, t) -> new QuizQuestion_GiveExtraTurnTroop(r, run, t).initialize());
        map.put(QuizQuestionType.SummonTransformTroop, (r, run, t) -> new QuizQuestion_SummonTransformTroop(r, run, t).initialize());
        map.put(QuizQuestionType.DrainManaTroop, (r, run, t) -> new QuizQuestion_DrainManaTroop(r, run, t).initialize());
        map.put(QuizQuestionType.EffectsTroop, (r, run, t) -> new QuizQuestion_EffectsTroop(r, run, t).initialize());

        map.put(QuizQuestionType.KingdomToTraitstone, (r, run, t) -> new QuizQuestion_KingdomToTraitstone(r, run, t).initialize());
        map.put(QuizQuestionType.TraitstoneToKingdom, (r, run, t) -> new QuizQuestion_TraitstoneToKingdom(r, run, t).initialize());
        map.put(QuizQuestionType.KingdomToStat, (r, run, t) -> new QuizQuestion_KingdomToStat(r, run, t).initialize());
        map.put(QuizQuestionType.StatToKingdom, (r, run, t) -> new QuizQuestion_StatToKingdom(r, run, t).initialize());
        map.put(QuizQuestionType.BannerArtToKingdom, (r, run, t) -> new QuizQuestion_BannerArtToKingdom(r, run, t).initialize());
        map.put(QuizQuestionType.ShieldArtToKingdom, (r, run, t) -> new QuizQuestion_ShieldArtToKingdom(r, run, t).initialize());

        map.put(QuizQuestionType.ClassToBonusColor, (r, run, t) -> new QuizQuestion_ClassToBonusColor(r, run, t).initialize());
        map.put(QuizQuestionType.BonusColorToClass, (r, run, t) -> new QuizQuestion_BonusColorToClass(r, run, t).initialize());
        map.put(QuizQuestionType.ClassToTrait, (r, run, t) -> new QuizQuestion_ClassToTrait(r, run, t).initialize());
        map.put(QuizQuestionType.TraitToClass, (r, run, t) -> new QuizQuestion_TraitToClass(r, run, t).initialize());
        map.put(QuizQuestionType.ClassArtToClass, (r, run, t) -> new QuizQuestion_ClassArtToClass(r, run, t).initialize());

        return map;
    }

    /**
     * Generates random questions of the specified type.
     *
     * @param r The random number generator to use.
     * @param type The type of question to create.
     * @return A new question of the specified type.
     * @throws java.sql.SQLException
     */
    @Override
    public QuizQuestion[] getQuestions(int count, Random r, QuizQuestionType type) throws SQLException
    {
        QuizQuestion[] questions = new QuizQuestion[count];
        for (int i = 0; i < count; i++)
        {
            questions[i] = CREATOR_MAP.get(type).create(r, runner, type);
        }
        return questions;
    }

    /**
     * Generates a random question of the specified difficulty, and a random
     * type.
     *
     * @param r The random number generator to use.
     * @param difficulty The difficulty of question to create.
     * @return A new question of the specified type.
     */
    @Override
    public QuizQuestion[] getQuestions(int count, Random r, QuizQuestion.Difficulty difficulty) throws SQLException
    {
        ArrayList<QuizQuestionType> types = getTypesForDifficulty(difficulty);
        QuizQuestionType type = types.get(r.nextInt(types.size()));
        return getQuestions(count, r, type);
    }

    /**
     * Generates a random question of a random type (and thereby a random
     * difficulty).
     *
     * @param r The random number generator to use.
     * @return A new question of the specified type.
     */
    @Override
    public QuizQuestion[] getQuestions(int count, Random r) throws SQLException
    {
        ArrayList<QuizQuestionType> types = getTypesForDifficulty(QuizQuestion.Difficulty.Easy);
        types.addAll(getTypesForDifficulty(QuizQuestion.Difficulty.Moderate));
        types.addAll(getTypesForDifficulty(QuizQuestion.Difficulty.Hard));
        QuizQuestionType type = types.get(r.nextInt(types.size()));
        return getQuestions(count, r, type);
    }
}
