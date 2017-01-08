package me.samboycoding.krystarabot.quiz;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.function.BiFunction;
import me.samboycoding.krystarabot.GameData;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Alternative question factory for the quiz
 *
 * @author Emily Ash
 */
public class JsonQuizQuestionFactory implements QuizQuestionFactory
{

    /**
     * Base class for most questions that use random items in the world data (or
     * features of troops) as answers.
     */
    private static abstract class QuizQuestion_RandomBase extends QuizQuestion
    {

        private final QuizQuestionType type;
        protected JSONObject correctAnswer;
        protected ArrayList<JSONObject> answers;
        protected Random random;
        protected long seed;
        protected int myRand;

        public QuizQuestion_RandomBase(Random r, QuizQuestionType t)
        {
            random = r;
            seed = Utilities.getSeed(random);
            myRand = random.nextInt();
            type = t;
        }

        public QuizQuestion_RandomBase initialize()
        {
            HashMap<Object, Object> keyMap = new HashMap<>();
            ArrayList<Object> keys;

            answers = new ArrayList<>();

            // Choose an answer at random as the "correct answer"
            // Mark its key as "in use"; keys are defined in derived classes
            do
            {
                correctAnswer = getRandomAnswer();
                keys = getKeys(correctAnswer);
            } while (!addKeysToKeyMap(keys, correctAnswer, keyMap));

            answers.add(correctAnswer);

            int panic = 0;
            while (answers.size() < ANSWER_COUNT)
            {
                // Choose an answer at random as an "incorrect answer"
                JSONObject incorrectAnswer = getRandomAnswer();

                // Check to see if its key is in use; if not, add it as a possible
                // answer and mark the key as "in use"
                keys = getKeys(incorrectAnswer);
                if (addKeysToKeyMap(keys, incorrectAnswer, keyMap))
                {
                    answers.add(incorrectAnswer);
                }

                if (panic++ > 1000)
                {
                    // TODO: Throw exception?
                    // echo "Failed to find incorrect answers!";
                    break;
                }
            }

            // Shuffle the answers once we're done
            Collections.shuffle(answers, random);

            return this;
        }

        @Override
        public Difficulty getDifficulty()
        {
            return type.difficulty;
        }

        protected abstract ArrayList<Object> getKeys(JSONObject obj);

        protected abstract JSONObject getRandomAnswer();

        private static boolean addKeysToKeyMap(Iterable<Object> keys, Object obj, HashMap<Object, Object> keyMap)
        {
            if (keys == null)
            {
                return false;
            }

            for (Object key : keys)
            {
                if (keyMap.containsKey(key))
                {
                    return false;
                }
            }

            for (Object key : keys)
            {
                keyMap.put(key, obj);
            }

            return true;
        }

        @Override
        public int getCorrectAnswerIndex()
        {
            return answers.indexOf(correctAnswer);
        }

        @Override
        public long getRandomSeed()
        {
            return seed;
        }
    }

    /**
     * Base class for most questions that want troops with a specific feature as
     * answers.
     */
    private static abstract class QuizQuestion_TroopsFiltered extends QuizQuestion
    {

        private QuizQuestionType type;
        protected JSONObject correctAnswer;
        protected ArrayList<JSONObject> answers;
        protected Random random;
        protected long seed;
        protected int myRand;

        public QuizQuestion_TroopsFiltered(Random r, QuizQuestionType t)
        {
            random = r;
            seed = Utilities.getSeed(random);
            myRand = random.nextInt();
            type = t;
        }

        public QuizQuestion_TroopsFiltered initialize()
        {
            answers = new ArrayList<>();

            // Choose a troop at random as the "correct answer" until we find one that
            // satisfies the criteria
            // TODO: Does this need to be more performant for cases where only a few
            // troops satisfy the criteria?
            do
            {
                correctAnswer = getRandomTroop();
            } while (!matchesFilter(correctAnswer));

            answers.add(correctAnswer);

            while (answers.size() < ANSWER_COUNT)
            {
                // Choose a troop at random as an "incorrect answer"
                JSONObject incorrectAnswer = getRandomTroop();

                // Only add it if it doesn't satisfy the filter criteria
                if (!matchesFilter(incorrectAnswer) && (answers.indexOf(incorrectAnswer) < 0))
                {
                    answers.add(incorrectAnswer);
                }
            }

            // Shuffle the answers once we're done
            Collections.shuffle(answers, random);

            return this;
        }

        @Override
        public Difficulty getDifficulty()
        {
            return type.difficulty;
        }

        protected abstract boolean matchesFilter(JSONObject obj);

        protected JSONObject getRandomTroop()
        {
            return GameData.arrayTroops.getJSONObject(random.nextInt(GameData.arrayTroops.length()));
        }

        @Override
        public int getCorrectAnswerIndex()
        {
            return answers.indexOf(correctAnswer);
        }

        @Override
        public long getRandomSeed()
        {
            return seed;
        }
    }

    /**
     * Base class for most questions that want troops with a specific effect in
     * their spell.
     */
    private static abstract class QuizQuestion_TroopsSpellFiltered extends QuizQuestion_TroopsFiltered
    {

        public QuizQuestion_TroopsSpellFiltered(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        // Returns true if any part of the troop's spell has any one of the types specified in the
        // stepTypes array.
        protected boolean hasSpellStep(JSONObject troop, Iterable<String> stepTypes)
        {
            JSONArray stepArray = troop.getJSONObject("Spell").getJSONArray("SpellSteps");
            int stepCount = stepArray.length();
            for (int i = 0; i < stepCount; i++)
            {
                JSONObject step = stepArray.getJSONObject(i);
                String myStepType = step.getString("Type");
                for (String stepType : stepTypes)
                {
                    if (myStepType.equals(stepType))
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }
    }

    /**
     * Base class for most questions that use troops (or features of troops) as
     * answers.
     */
    private static abstract class QuizQuestion_Troops extends QuizQuestion_RandomBase
    {

        public QuizQuestion_Troops(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        protected JSONObject getRandomAnswer()
        {
            return getRandomTroop();
        }

        protected JSONObject getRandomTroop()
        {
            return GameData.arrayTroops.getJSONObject(random.nextInt(GameData.arrayTroops.length()));
        }
    }

    /**
     * Base class for most questions that use kingdoms (or features of kingdoms)
     * as answers.
     */
    private static abstract class QuizQuestion_Kingdoms extends QuizQuestion_RandomBase
    {

        public QuizQuestion_Kingdoms(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        protected JSONObject getRandomAnswer()
        {
            return getRandomKingdom();
        }

        protected JSONObject getRandomKingdom()
        {
            return GameData.arrayKingdoms.getJSONObject(random.nextInt(GameData.arrayKingdoms.length()));
        }
    }

    /**
     * Base class for most questions that use classes (or features of classes)
     * as answers.
     */
    private static abstract class QuizQuestion_Classes extends QuizQuestion_RandomBase
    {

        public QuizQuestion_Classes(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        protected JSONObject getRandomAnswer()
        {
            return getRandomClass();
        }

        protected JSONObject getRandomClass()
        {
            return GameData.arrayClasses.getJSONObject(random.nextInt(GameData.arrayClasses.length()));
        }
    }

    /**
     * Asks a user to identify a troop's kingdom.
     */
    private static class QuizQuestion_TroopToKingdom extends QuizQuestion_Troops
    {

        public QuizQuestion_TroopToKingdom(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **kingdom** is **" + correctAnswer.getString("Name") + "** from?";
        }

        @Override
        public String getAnswerText(int index)
        {
            JSONObject kingdom = getKingdomForTroop(answers.get(index));
            return kingdom.getString("Name");
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            JSONObject kingdom = getKingdomForTroop(obj);
            return new ArrayList<>(Arrays.asList(kingdom));
        }

        protected JSONObject getKingdomForTroop(JSONObject troop)
        {
            int myId = troop.getInt("Id");
            for (Object oKingdom : GameData.arrayKingdoms)
            {
                JSONObject kingdom = (JSONObject) oKingdom;
                JSONArray troopIds = kingdom.getJSONArray("TroopIds");
                for (int i = 0; i < troopIds.length(); i++)
                {
                    int troopId = troopIds.getInt(i);
                    if (troopId == myId)
                    {
                        return kingdom;
                    }
                }
            }

            return null;
        }
    }

    /**
     * Asks a user to identify which troop is from the specified kingdom.
     */
    private static class QuizQuestion_KingdomToTroop extends QuizQuestion_TroopToKingdom
    {

        public QuizQuestion_KingdomToTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            JSONObject kingdom = getKingdomForTroop(correctAnswer);
            return "What **troop** comes from the kingdom of **" + kingdom.getString("Name") + "**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }
    }

    /**
     * Asks a user to identify a troop's spell.
     */
    private static class QuizQuestion_TroopToSpell extends QuizQuestion_Troops
    {

        public QuizQuestion_TroopToSpell(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What is the name of **" + correctAnswer.getString("Name") + "'s spell**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getJSONObject("Spell").getString("Name");
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            return new ArrayList<>(Arrays.asList(obj));
        }
    }

    /**
     * Asks a user to identify a spell's troop.
     */
    private static class QuizQuestion_SpellToTroop extends QuizQuestion_TroopToSpell
    {

        public QuizQuestion_SpellToTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** uses the spell **\"" + correctAnswer.getJSONObject("Spell").getString("Name") + "\"**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }
    }

    /**
     * Asks a user to identify a troop by its spell's art.
     */
    private static class QuizQuestion_SpellArtToTroop extends QuizQuestion_TroopToSpell
    {

        public QuizQuestion_SpellArtToTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
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
                return new URL("http://ashtender.com/gems/assets/spells/" + correctAnswer.getJSONObject("Spell").getInt("Id") + "_small.jpg");
            } catch (MalformedURLException e)
            {
            }
            return null;
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }
    }

    /**
     * Asks a user to identify a troop by its card art.
     */
    private static class QuizQuestion_CardArtToTroop extends QuizQuestion_TroopToSpell
    {

        public QuizQuestion_CardArtToTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
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
                return new URL("http://ashtender.com/gems/assets/cards/" + correctAnswer.getString("FileBase") + "_small.jpg");
            } catch (MalformedURLException e)
            {
            }
            return null;
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }
    }

    /**
     * Asks a user to identify a troop's type.
     */
    private static class QuizQuestion_TroopToType extends QuizQuestion_Troops
    {

        public QuizQuestion_TroopToType(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What is/are **" + correctAnswer.getString("Name") + "'s type(s)**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Type");
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            String type = obj.getString("Type");
            if (type.equals(obj.getString("Name")))
            {
                return null;
            }
            return new ArrayList<>(Arrays.asList(type));
        }
    }

    /**
     * Asks a user to identify which troop is of the specified type.
     */
    private static class QuizQuestion_TypeToTroop extends QuizQuestion_TroopToType
    {

        public QuizQuestion_TypeToTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** is of type **" + getTroopType(correctAnswer) + "**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }

        private String getTroopType(JSONObject obj)
        {
            String type = obj.getString("Type");
            if (type.indexOf("-") < 0)
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

        public QuizQuestion_TroopToTrait(Random r, QuizQuestionType t)
        {
            super(r, t);
            traitIndex = r.nextInt(3);
        }

        @Override
        public String getQuestionText()
        {
            return "What is the name of one of **" + correctAnswer.getString("Name") + "'s traits**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getJSONArray("ParsedTraits").getJSONObject(traitIndex).getString("Name");
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            JSONArray traitArray = obj.getJSONArray("ParsedTraits");
            Object[] traits =
            {
                traitArray.getJSONObject(0).getString("Name"),
                traitArray.getJSONObject(1).getString("Name"),
                traitArray.getJSONObject(2).getString("Name")
            };
            return new ArrayList<>(Arrays.asList(traits));
        }
    }

    /**
     * Asks a user to identify which troop has the specified trait.
     */
    private static class QuizQuestion_TraitToTroop extends QuizQuestion_TroopToTrait
    {

        public QuizQuestion_TraitToTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** has the trait **\"" + correctAnswer.getJSONArray("ParsedTraits").getJSONObject(traitIndex).getString("Name") + "\"**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }
    }

    /**
     * Asks a user to identify a troop's color.
     */
    private static class QuizQuestion_TroopToColor extends QuizQuestion_Troops
    {

        public QuizQuestion_TroopToColor(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **mana colors** does **" + correctAnswer.getString("Name") + "** use?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return getTroopColors(answers.get(index));
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            return new ArrayList<>(Arrays.asList(getTroopColors(obj)));
        }

        protected String getTroopColors(JSONObject obj)
        {
            JSONObject manaColors = obj.getJSONObject("ManaColors");
            ArrayList<String> colors = new ArrayList<>();
            if (manaColors.getBoolean("ColorBlue"))
            {
                colors.add("Blue");
            }
            if (manaColors.getBoolean("ColorGreen"))
            {
                colors.add("Green");
            }
            if (manaColors.getBoolean("ColorRed"))
            {
                colors.add("Red");
            }
            if (manaColors.getBoolean("ColorYellow"))
            {
                colors.add("Yellow");
            }
            if (manaColors.getBoolean("ColorPurple"))
            {
                colors.add("Purple");
            }
            if (manaColors.getBoolean("ColorBrown"))
            {
                colors.add("Brown");
            }
            String result = String.join("/", colors);
            if (colors.size() == 1)
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

        public QuizQuestion_ColorToTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** is **" + getTroopColors(correctAnswer) + "**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }
    }

    /**
     * Asks a user to identify a troop's rarity.
     */
    private static class QuizQuestion_TroopToRarity extends QuizQuestion_Troops
    {

        public QuizQuestion_TroopToRarity(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What is **" + correctAnswer.getString("Name") + "'s** base **rarity**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("TroopRarity");
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            return new ArrayList<>(Arrays.asList(obj.getString("TroopRarity")));
        }
    }

    /**
     * Asks a user to identify which troop is of the specified rarity.
     */
    private static class QuizQuestion_RarityToTroop extends QuizQuestion_TroopToRarity
    {

        public QuizQuestion_RarityToTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** has a **base rarity** of **" + correctAnswer.getString("TroopRarity") + "**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }
    }

    /**
     * Asks a user to identify a troop from its flavor text.
     */
    private static class QuizQuestion_FlavorTextToTroop extends QuizQuestion_Troops
    {

        public QuizQuestion_FlavorTextToTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
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
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            String flavorText = getTroopFlavorText(obj);
            String name = obj.getString("Name");
            if (flavorText.length() > 80)
            {
                return null;
            }
            if (flavorText.toLowerCase().contains(name.toLowerCase()))
            {
                return null;
            }
            return new ArrayList<>(Arrays.asList(flavorText));
        }

        private String getTroopFlavorText(JSONObject troop)
        {
            String flavorText = troop.getString("Description");
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

        public QuizQuestion_TrueDamageTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** does **true damage** as part of its spell?";
        }

        @Override
        protected boolean matchesFilter(JSONObject obj)
        {
            return hasSpellStep(obj, new ArrayList<>(Arrays.asList("TrueDamage", "TrueSplashDamage", "TrueScatterDamage")));
        }
    }

    /**
     * Asks a user to identify which troop creates gems.
     */
    private static class QuizQuestion_CreateGemsTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_CreateGemsTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop creates gems** as part of its spell?";
        }

        @Override
        protected boolean matchesFilter(JSONObject obj)
        {
            return hasSpellStep(obj, new ArrayList<>(Arrays.asList("CreateGems", "CreateGems2Colors")));
        }
    }

    /**
     * Asks a user to identify which troop converts gems.
     */
    private static class QuizQuestion_ConvertGemsTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_ConvertGemsTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop converts gems** as part of its spell?";
        }

        @Override
        protected boolean matchesFilter(JSONObject obj)
        {
            return hasSpellStep(obj, new ArrayList<>(Arrays.asList("ConvertGems")));
        }
    }

    /**
     * Asks a user to identify which troop destroys, removes, or explodes gems.
     */
    private static class QuizQuestion_DestroyGemsTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_DestroyGemsTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop destroys, removes,** or **explodes gems** as part of its spell?";
        }

        @Override
        protected boolean matchesFilter(JSONObject obj)
        {
            return hasSpellStep(obj, new ArrayList<>(Arrays.asList("DestroyGems", "DestroyColor", "DestroyRow",
                    "ExplodeGems", "ExplodeColor", "RemoveGems", "RemoveColor")));
        }
    }

    /**
     * Asks a user to identify which troop increases stats.
     */
    private static class QuizQuestion_IncreaseStatsTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_IncreaseStatsTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** can **heal** or **increase stats** of itself or another troop as part of its spell?";
        }

        @Override
        protected boolean matchesFilter(JSONObject obj)
        {
            return hasSpellStep(obj, new ArrayList<>(Arrays.asList("Heal", "IncreaseHealth", "IncreaseArmor",
                    "IncreaseAttack", "IncreaseSpellPower", "IncreaseRandom", "IncreaseAllStats", "StealRandomStat",
                    "StealAttack", "StealArmor", "StealLife", "StealMagic", "Consume", "ConsumeConditional")));
        }
    }

    /**
     * Asks a user to identify which troop decreases stats.
     */
    private static class QuizQuestion_DecreaseStatsTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_DecreaseStatsTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** can **decrease stats** of itself or another troop (excluding normal damage) as part of its spell?";
        }

        @Override
        protected boolean matchesFilter(JSONObject obj)
        {
            return hasSpellStep(obj, new ArrayList<>(Arrays.asList("DecreaseRandom", "DecreaseAttack",
                    "DecreaseSpellPower", "DecreaseArmor", "DecreaseAllStats", "StealRandomStat", "StealAttack",
                    "StealArmor", "StealLife", "StealMagic")));
        }
    }

    /**
     * Asks a user to identify which troop gives resources (Gold, Souls, Maps).
     */
    private static class QuizQuestion_GiveResourcesTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_GiveResourcesTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** has a spell that can be used to generate **Gold, Souls,** or **Maps**?";
        }

        @Override
        protected boolean matchesFilter(JSONObject obj)
        {
            return hasSpellStep(obj, new ArrayList<>(Arrays.asList("GiveGold", "GiveSouls", "GiveTreasureMaps")));
        }
    }

    /**
     * Asks a user to identify which troop gives extra turns.
     */
    private static class QuizQuestion_GiveExtraTurnTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_GiveExtraTurnTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** has a spell that can give an **extra turn**?";
        }

        @Override
        protected boolean matchesFilter(JSONObject obj)
        {
            return hasSpellStep(obj, new ArrayList<>(Arrays.asList("ExtraTurn", "ExtraTurnConditional")));
        }
    }

    /**
     * Asks a user to identify which troop summons or transforms.
     */
    private static class QuizQuestion_SummonTransformTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_SummonTransformTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** has a spell that can **summon** or **transform** itself or another troop?";
        }

        @Override
        protected boolean matchesFilter(JSONObject obj)
        {
            return hasSpellStep(obj, new ArrayList<>(Arrays.asList("SummoningNoError", "SummoningType", "SummoningConditional",
                    "SummoningTypeConditional", "Transform", "TransformType", "TransformTypeConditional", "TransformEnemy")));
        }
    }

    /**
     * Asks a user to identify which troop drains mana.
     */
    private static class QuizQuestion_DrainManaTroop extends QuizQuestion_TroopsSpellFiltered
    {

        public QuizQuestion_DrainManaTroop(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** has a spell that can **drain** or **steal mana** from another troop?";
        }

        @Override
        protected boolean matchesFilter(JSONObject obj)
        {
            return hasSpellStep(obj, new ArrayList<>(Arrays.asList("DecreaseMana")));
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

        public QuizQuestion_EffectsTroop(Random r, QuizQuestionType t)
        {
            super(r, t);

            effectEntry = EffectTable[r.nextInt(EffectTable.length)];
        }

        @Override
        public String getQuestionText()
        {
            return "What **troop** can cause **" + effectEntry.effectName + "** as part of its spell?";
        }

        @Override
        protected boolean matchesFilter(JSONObject obj)
        {
            ArrayList<String> effects = new ArrayList<>();
            effects.add(effectEntry.effectSpellStepType);
            if (effectEntry.isDebuff)
            {
                effects.add("RandomStatusEffect");
            }
            return hasSpellStep(obj, effects);
        }
    }

    /**
     * Asks a user to identify which traitstone can be found in the specified
     * kingdom.
     */
    private static class QuizQuestion_KingdomToTraitstone extends QuizQuestion_Kingdoms
    {

        public QuizQuestion_KingdomToTraitstone(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **Arcane Traitstone** can be found in **" + correctAnswer.getString("Name") + "**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("ExploreRuneName");
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            if (!obj.has("ExploreRuneName"))
            {
                return null;
            }
            return new ArrayList<>(Arrays.asList(obj.getString("ExploreRuneName")));
        }
    }

    /**
     * Asks a user to identify which kingdom has the specified traitstone.
     */
    private static class QuizQuestion_TraitstoneToKingdom extends QuizQuestion_KingdomToTraitstone
    {

        public QuizQuestion_TraitstoneToKingdom(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "In which **kingdom** can you find **" + correctAnswer.getString("ExploreRuneName") + "**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }
    }

    /**
     * Asks a user to identify which stat can be increased in the specified
     * kingdom.
     */
    private static class QuizQuestion_KingdomToStat extends QuizQuestion_Kingdoms
    {

        public QuizQuestion_KingdomToStat(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **stat** is increased by reaching level 10 in the kingdom of **" + correctAnswer.getString("Name") + "**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return getStatForKingdom(answers.get(index));
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            if (!obj.has("ExploreRuneName"))
            {
                return null;
            }
            return new ArrayList<>(Arrays.asList(getStatForKingdom(obj)));
        }

        protected String getStatForKingdom(JSONObject obj)
        {
            String statName = obj.getJSONObject("LevelData").getString("Stat");
            return statName.substring(0, 1).toUpperCase() + statName.substring(1);
        }
    }

    /**
     * Asks a user to identify which kingdom has the specified stat increase.
     */
    private static class QuizQuestion_StatToKingdom extends QuizQuestion_KingdomToStat
    {

        public QuizQuestion_StatToKingdom(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **kingdom** gives an increase to **" + getStatForKingdom(correctAnswer) + "** at level 10?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }
    }

    /**
     * Asks a user to identify a kingdom by its banner art.
     */
    private static class QuizQuestion_BannerArtToKingdom extends QuizQuestion_Kingdoms
    {

        public QuizQuestion_BannerArtToKingdom(Random r, QuizQuestionType t)
        {
            super(r, t);
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
                return new URL("http://ashtender.com/gems/assets/banners/" + correctAnswer.getString("FileBase") + "_small.png");
            } catch (MalformedURLException e)
            {
            }
            return null;
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            if (obj.isNull("BannerFileBase"))
            {
                return null;
            }
            return new ArrayList<>(Arrays.asList(obj));
        }
    }

    /**
     * Asks a user to identify a kingdom by its shield art.
     */
    private static class QuizQuestion_ShieldArtToKingdom extends QuizQuestion_Kingdoms
    {

        public QuizQuestion_ShieldArtToKingdom(Random r, QuizQuestionType t)
        {
            super(r, t);
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
                return new URL("http://ashtender.com/gems/assets/shields/" + correctAnswer.getString("FileBase") + "_small.png");
            } catch (MalformedURLException e)
            {
            }
            return null;
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            return new ArrayList<>(Arrays.asList(obj));
        }
    }

    /**
     * Asks a user to identify which color weapons have affinity with a given
     * class.
     */
    private static class QuizQuestion_ClassToBonusColor extends QuizQuestion_Classes
    {

        public QuizQuestion_ClassToBonusColor(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **color weapons** get bonus Magic when used by the **" + correctAnswer.getString("Name") + "** class?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return getBonusColorForClass(answers.get(index));
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            return new ArrayList<>(Arrays.asList(getBonusColorForClass(obj)));
        }

        protected String getBonusColorForClass(JSONObject obj)
        {
            String colorName = obj.getString("BonusWeapon");
            return colorName.substring(0, 1).toUpperCase() + colorName.substring(1);
        }
    }

    /**
     * Asks a user to identify which class has affinity to the specified color.
     */
    private static class QuizQuestion_BonusColorToClass extends QuizQuestion_ClassToBonusColor
    {

        public QuizQuestion_BonusColorToClass(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **class** gets bonus Magic when using **" + getBonusColorForClass(correctAnswer) + "** weapons?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }
    }

    /**
     * Asks a user to identify one of a class's traits.
     */
    private static class QuizQuestion_ClassToTrait extends QuizQuestion_Classes
    {

        protected final int traitIndex;

        public QuizQuestion_ClassToTrait(Random r, QuizQuestionType t)
        {
            super(r, t);
            traitIndex = r.nextInt(3);
        }

        @Override
        public String getQuestionText()
        {
            return "What is the name of one of the **traits** of the **" + correctAnswer.getString("Name") + "** class?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getJSONArray("ParsedTraits").getJSONObject(traitIndex).getString("Name");
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            JSONArray traitArray = obj.getJSONArray("ParsedTraits");
            Object[] traits =
            {
                traitArray.getJSONObject(0).getString("Name"),
                traitArray.getJSONObject(1).getString("Name"),
                traitArray.getJSONObject(2).getString("Name")
            };
            return new ArrayList<>(Arrays.asList(traits));
        }
    }

    /**
     * Asks a user to identify which class has the specified trait.
     */
    private static class QuizQuestion_TraitToClass extends QuizQuestion_ClassToTrait
    {

        public QuizQuestion_TraitToClass(Random r, QuizQuestionType t)
        {
            super(r, t);
        }

        @Override
        public String getQuestionText()
        {
            return "What **class** has the trait **\"" + correctAnswer.getJSONArray("ParsedTraits").getJSONObject(traitIndex).getString("Name") + "\"**?";
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }
    }

    /**
     * Asks a user to identify a troop by its card art.
     */
    private static class QuizQuestion_ClassArtToClass extends QuizQuestion_Classes
    {

        public QuizQuestion_ClassArtToClass(Random r, QuizQuestionType t)
        {
            super(r, t);
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
                return new URL("http://ashtender.com/gems/assets/classes/" + correctAnswer.getInt("Id") + "_small.png");
            } catch (MalformedURLException e)
            {
            }
            return null;
        }

        @Override
        public String getAnswerText(int index)
        {
            return answers.get(index).getString("Name");
        }

        @Override
        protected ArrayList<Object> getKeys(JSONObject obj)
        {
            return new ArrayList<>(Arrays.asList(obj));
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

        QuizQuestion create(Random r, QuizQuestionType t);
    }

    private final static HashMap<QuizQuestionType, QuestionCreator> CREATOR_MAP = initCreatorMap();

    private static HashMap<QuizQuestionType, QuestionCreator> initCreatorMap()
    {
        HashMap<QuizQuestionType, QuestionCreator> map = new HashMap<>();
        map.put(QuizQuestionType.TroopToKingdom, (r, t) -> new QuizQuestion_TroopToKingdom(r, t).initialize());
        map.put(QuizQuestionType.KingdomToTroop, (r, t) -> new QuizQuestion_KingdomToTroop(r, t).initialize());
        map.put(QuizQuestionType.TroopToSpell, (r, t) -> new QuizQuestion_TroopToSpell(r, t).initialize());
        map.put(QuizQuestionType.SpellToTroop, (r, t) -> new QuizQuestion_SpellToTroop(r, t).initialize());
        map.put(QuizQuestionType.TroopToType, (r, t) -> new QuizQuestion_TroopToType(r, t).initialize());
        map.put(QuizQuestionType.TypeToTroop, (r, t) -> new QuizQuestion_TypeToTroop(r, t).initialize());
        map.put(QuizQuestionType.TroopToColor, (r, t) -> new QuizQuestion_TroopToColor(r, t).initialize());
        map.put(QuizQuestionType.ColorToTroop, (r, t) -> new QuizQuestion_ColorToTroop(r, t).initialize());
        map.put(QuizQuestionType.TroopToRarity, (r, t) -> new QuizQuestion_TroopToRarity(r, t).initialize());
        map.put(QuizQuestionType.RarityToTroop, (r, t) -> new QuizQuestion_RarityToTroop(r, t).initialize());
        map.put(QuizQuestionType.TroopToTrait, (r, t) -> new QuizQuestion_TroopToTrait(r, t).initialize());
        map.put(QuizQuestionType.TraitToTroop, (r, t) -> new QuizQuestion_TraitToTroop(r, t).initialize());
        map.put(QuizQuestionType.FlavorTextToTroop, (r, t) -> new QuizQuestion_FlavorTextToTroop(r, t).initialize());
        map.put(QuizQuestionType.SpellArtToTroop, (r, t) -> new QuizQuestion_SpellArtToTroop(r, t).initialize());
        map.put(QuizQuestionType.CardArtToTroop, (r, t) -> new QuizQuestion_CardArtToTroop(r, t).initialize());
        map.put(QuizQuestionType.TrueDamageTroop, (r, t) -> new QuizQuestion_TrueDamageTroop(r, t).initialize());
        map.put(QuizQuestionType.CreateGemsTroop, (r, t) -> new QuizQuestion_CreateGemsTroop(r, t).initialize());
        map.put(QuizQuestionType.ConvertGemsTroop, (r, t) -> new QuizQuestion_ConvertGemsTroop(r, t).initialize());
        map.put(QuizQuestionType.DestroyGemsTroop, (r, t) -> new QuizQuestion_DestroyGemsTroop(r, t).initialize());
        map.put(QuizQuestionType.IncreaseStatsTroop, (r, t) -> new QuizQuestion_IncreaseStatsTroop(r, t).initialize());
        map.put(QuizQuestionType.DecreaseStatsTroop, (r, t) -> new QuizQuestion_DecreaseStatsTroop(r, t).initialize());
        map.put(QuizQuestionType.GiveResourcesTroop, (r, t) -> new QuizQuestion_GiveResourcesTroop(r, t).initialize());
        map.put(QuizQuestionType.GiveExtraTurnTroop, (r, t) -> new QuizQuestion_GiveExtraTurnTroop(r, t).initialize());
        map.put(QuizQuestionType.SummonTransformTroop, (r, t) -> new QuizQuestion_SummonTransformTroop(r, t).initialize());
        map.put(QuizQuestionType.DrainManaTroop, (r, t) -> new QuizQuestion_DrainManaTroop(r, t).initialize());
        map.put(QuizQuestionType.EffectsTroop, (r, t) -> new QuizQuestion_EffectsTroop(r, t).initialize());

        map.put(QuizQuestionType.KingdomToTraitstone, (r, t) -> new QuizQuestion_KingdomToTraitstone(r, t).initialize());
        map.put(QuizQuestionType.TraitstoneToKingdom, (r, t) -> new QuizQuestion_TraitstoneToKingdom(r, t).initialize());
        map.put(QuizQuestionType.KingdomToStat, (r, t) -> new QuizQuestion_KingdomToStat(r, t).initialize());
        map.put(QuizQuestionType.StatToKingdom, (r, t) -> new QuizQuestion_StatToKingdom(r, t).initialize());
        map.put(QuizQuestionType.BannerArtToKingdom, (r, t) -> new QuizQuestion_BannerArtToKingdom(r, t).initialize());
        map.put(QuizQuestionType.ShieldArtToKingdom, (r, t) -> new QuizQuestion_ShieldArtToKingdom(r, t).initialize());

        map.put(QuizQuestionType.ClassToBonusColor, (r, t) -> new QuizQuestion_ClassToBonusColor(r, t).initialize());
        map.put(QuizQuestionType.BonusColorToClass, (r, t) -> new QuizQuestion_BonusColorToClass(r, t).initialize());
        map.put(QuizQuestionType.ClassToTrait, (r, t) -> new QuizQuestion_ClassToTrait(r, t).initialize());
        map.put(QuizQuestionType.TraitToClass, (r, t) -> new QuizQuestion_TraitToClass(r, t).initialize());
        map.put(QuizQuestionType.ClassArtToClass, (r, t) -> new QuizQuestion_ClassArtToClass(r, t).initialize());

        return map;
    }

    /**
     * Generates a random question of the specified type.
     *
     * @param r The random number generator to use.
     * @param type The type of question to create.
     * @return A new question of the specified type.
     */
    @Override
    public QuizQuestion[] getQuestions(int count, Random r, QuizQuestionType type)
    {
        QuizQuestion[] questions = new QuizQuestion[count];
        for (int i = 0; i < count; i++)
        {
            questions[i] = CREATOR_MAP.get(type).create(r, type);
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
    public QuizQuestion[] getQuestions(int count, Random r, QuizQuestion.Difficulty difficulty)
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
    public QuizQuestion[] getQuestions(int count, Random r)
    {
        ArrayList<QuizQuestionType> types = getTypesForDifficulty(QuizQuestion.Difficulty.Easy);
        types.addAll(getTypesForDifficulty(QuizQuestion.Difficulty.Moderate));
        types.addAll(getTypesForDifficulty(QuizQuestion.Difficulty.Hard));
        QuizQuestionType type = types.get(r.nextInt(types.size()));
        return getQuestions(count, r, type);
    }
}
