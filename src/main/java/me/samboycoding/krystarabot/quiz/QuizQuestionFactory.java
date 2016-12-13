package me.samboycoding.krystarabot.quiz;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import me.samboycoding.krystarabot.GameData;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Alternative question factory for the quiz
 * @author Emily Ash
 */
public class QuizQuestionFactory
{
    public enum QuestionType
    {
        TroopToKingdom("Which kingdom is this troop from?", QuizQuestion.Difficulty.Easy),
        KingdomToTroop("Which troop is in this kingdom?", QuizQuestion.Difficulty.Easy),
        TroopToSpell("Which spell does this troop have?", QuizQuestion.Difficulty.Moderate),
        SpellToTroop("Which troop has this spell?", QuizQuestion.Difficulty.Moderate),
        TroopToType("Which type does this troop have?", QuizQuestion.Difficulty.Moderate),
        TypeToTroop("Which troop has this type?", QuizQuestion.Difficulty.Moderate),
        TroopToColor("Which color(s) does this troop use?", QuizQuestion.Difficulty.Hard),
        ColorToTroop("Which troop uses this/these color(s)?", QuizQuestion.Difficulty.Hard),
        TroopToRarity("Which rarity is this troop?", QuizQuestion.Difficulty.Easy),
        RarityToTroop("Which troop is of this rarity?", QuizQuestion.Difficulty.Easy),
        TroopToTrait("Which trait does this troop have?", QuizQuestion.Difficulty.Hard),
        TraitToTroop("Which troop has this trait?", QuizQuestion.Difficulty.Hard),
        FlavorTextToTroop("Which troop has this flavour text?", QuizQuestion.Difficulty.Easy),
        SpellArtToTroop("Which troop has the pictured spell?", QuizQuestion.Difficulty.Easy),
        
        TrueDamageTroop("Which of these troops does true damage?", QuizQuestion.Difficulty.Easy),
        CreateGemsTroop("Which of these troops creates gems?", QuizQuestion.Difficulty.Easy),
        ConvertGemsTroop("Which of these troops converts gems?", QuizQuestion.Difficulty.Easy),
        DestroyGemsTroop("Which of these troops destroys gems?", QuizQuestion.Difficulty.Easy),
        IncreaseStatsTroop("Which of these troops increases the stats of itself or others?", QuizQuestion.Difficulty.Moderate),
        DecreaseStatsTroop("Which of these troops decreases the stats of itself or others?", QuizQuestion.Difficulty.Moderate),
        GiveResourcesTroop("Which of these troops gives resources?", QuizQuestion.Difficulty.Easy),
        GiveExtraTurnTroop("Which of these troops gives an extra turn?", QuizQuestion.Difficulty.Easy),
        SummonTransformTroop("Which of these troops summons or transforms a troop?", QuizQuestion.Difficulty.Easy),
        DrainManaTroop("Which of these troops drains mana?", QuizQuestion.Difficulty.Easy),
        EffectsTroop("Which of these troops gives effects?", QuizQuestion.Difficulty.Moderate),
        
        KingdomToTraitstone("Which traitstone is found in this kingdom?", QuizQuestion.Difficulty.Hard),
        TraitstoneToKingdom("Which kingdom contains this traitstone?", QuizQuestion.Difficulty.Hard),
        KingdomToStat("Which stat is got from this kingdom?", QuizQuestion.Difficulty.Hard),
        StatToKingdom("Which kingdom gives this stat?", QuizQuestion.Difficulty.Hard),
        BannerArtToKingdom("Which kingdom has the pictured banner?", QuizQuestion.Difficulty.Easy),
        ShieldArtToKingdom("Which kingdom has the pictured shield?", QuizQuestion.Difficulty.Moderate),
        
        ClassToBonusColor("Which bonus color is got from this class?", QuizQuestion.Difficulty.Unused),
        BonusColorToClass("Which class gives this bonus color?", QuizQuestion.Difficulty.Unused),
        ClassToTrait("Which trait is got from this class?", QuizQuestion.Difficulty.Unused),
        TraitToClass("Which class gives this trait?", QuizQuestion.Difficulty.Unused);
        
        
        private final String descriptionText;
        public final QuizQuestion.Difficulty difficulty;
        
        private QuestionType(String desc, QuizQuestion.Difficulty d)
        {
            descriptionText = desc;
            difficulty = d;
        }
        
        public String toString()
        {
            return descriptionText;
        }
        
        private static final QuestionType[] Types =
        {
            TroopToKingdom,
            KingdomToTroop,
            TroopToSpell,
            SpellToTroop,
            TroopToType,
            TypeToTroop,
            TroopToColor,
            ColorToTroop,
            TroopToRarity,
            RarityToTroop,
            TroopToTrait,
            TraitToTroop,
            FlavorTextToTroop,
            SpellArtToTroop,
            TrueDamageTroop,
            CreateGemsTroop,
            ConvertGemsTroop,
            DestroyGemsTroop,
            IncreaseStatsTroop,
            DecreaseStatsTroop,
            GiveResourcesTroop,
            GiveExtraTurnTroop,
            SummonTransformTroop,
            DrainManaTroop,
            EffectsTroop,
            KingdomToTraitstone,
            TraitstoneToKingdom,
            KingdomToStat,
            StatToKingdom,
            BannerArtToKingdom,
            ShieldArtToKingdom,
            ClassToBonusColor,
            BonusColorToClass,
            ClassToTrait,
            TraitToClass
        };
        
        public static final int Count = Types.length;
        
        public static QuestionType fromInteger(int x)
        {
            return Types[x];
        }
        
        public static QuestionType fromString(String s)
        {
            for (QuestionType t : Types)
            {
                if (t.name().equalsIgnoreCase(s))
                {
                    return t;
                }
            }
            throw new InvalidParameterException();
        }
        
        public static QuestionType[] getTypes()
        {
            return Types.clone();
        }
    }
    
    /**
     * Base class for most questions that use random items in the world data (or features of troops) as answers.
     */
    private static abstract class QuizQuestion_RandomBase extends QuizQuestion
    {
        public QuizQuestion_RandomBase(Random r) { super(r); }

        public QuizQuestion_RandomBase initialize()
        {
            HashMap<Object, Object> keyMap = new HashMap<>();
            ArrayList<Object> keys;

            // Choose an answer at random as the "correct answer"
            // Mark its key as "in use"; keys are defined in derived classes
            do
            {
                correctAnswer = getRandomAnswer();
                keys = getKeys(correctAnswer);
            }
            while (!addKeysToKeyMap(keys, correctAnswer, keyMap));

            answers.add(correctAnswer);

            int panic = 0;
            while (answers.size() < AnswerCount)
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
    }

    /**
     * Base class for most questions that want troops with a specific feature as answers.
     */
    private static abstract class QuizQuestion_TroopsFiltered extends QuizQuestion
    {
        public QuizQuestion_TroopsFiltered(Random r) { super(r); }

        public QuizQuestion_TroopsFiltered initialize()
        {
            // Choose a troop at random as the "correct answer" until we find one that
            // satisfies the criteria
            // TODO: Does this need to be more performant for cases where only a few
            // troops satisfy the criteria?
            do
            {
                correctAnswer = getRandomTroop();
            }
            while (!matchesFilter(correctAnswer));

            answers.add(correctAnswer);

            while (answers.size() < AnswerCount)
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

        protected abstract boolean matchesFilter(JSONObject obj);
        
        protected JSONObject getRandomTroop()
        {
            return GameData.arrayTroops.getJSONObject(random.nextInt(GameData.arrayTroops.length()));
        }
    }
    
    /**
     * Base class for most questions that want troops with a specific effect in their spell.
     */
    private static abstract class QuizQuestion_TroopsSpellFiltered extends QuizQuestion_TroopsFiltered
    {
        public QuizQuestion_TroopsSpellFiltered(Random r) { super(r); }

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
     * Base class for most questions that use troops (or features of troops) as answers.
     */
    private static abstract class QuizQuestion_Troops extends QuizQuestion_RandomBase
    {
        public QuizQuestion_Troops(Random r) { super(r); }

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
     * Base class for most questions that use kingdoms (or features of kingdoms) as answers.
     */
    private static abstract class QuizQuestion_Kingdoms extends QuizQuestion_RandomBase
    {
        public QuizQuestion_Kingdoms(Random r) { super(r); }

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
     * Base class for most questions that use classes (or features of classes) as answers.
     */
    private static abstract class QuizQuestion_Classes extends QuizQuestion_RandomBase
    {
        public QuizQuestion_Classes(Random r) { super(r); }

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
        public QuizQuestion_TroopToKingdom(Random r) { super(r); }

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
                JSONObject kingdom = (JSONObject)oKingdom;
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
        public QuizQuestion_KingdomToTroop(Random r) { super(r); }

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
        public QuizQuestion_TroopToSpell(Random r) { super(r); }

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
        public QuizQuestion_SpellToTroop(Random r) { super(r); }

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
        public QuizQuestion_SpellArtToTroop(Random r) { super(r); }

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
            }
            catch (MalformedURLException e)
            {}
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
        public QuizQuestion_TroopToType(Random r) { super(r); }

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
        public QuizQuestion_TypeToTroop(Random r) { super(r); }

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
        
        public QuizQuestion_TroopToTrait(Random r) 
        {
            super(r);
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
        public QuizQuestion_TraitToTroop(Random r) { super(r); }

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
        public QuizQuestion_TroopToColor(Random r) { super(r); }

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
        public QuizQuestion_ColorToTroop(Random r) { super(r); }

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
        public QuizQuestion_TroopToRarity(Random r) { super(r); }

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
        public QuizQuestion_RarityToTroop(Random r) { super(r); }

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
        public QuizQuestion_FlavorTextToTroop(Random r) { super(r); }

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
        public QuizQuestion_TrueDamageTroop(Random r) { super(r); }
        
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
        public QuizQuestion_CreateGemsTroop(Random r) { super(r); }
        
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
        public QuizQuestion_ConvertGemsTroop(Random r) { super(r); }
        
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
        public QuizQuestion_DestroyGemsTroop(Random r) { super(r); }
        
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
        public QuizQuestion_IncreaseStatsTroop(Random r) { super(r); }
        
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
        public QuizQuestion_DecreaseStatsTroop(Random r) { super(r); }
        
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
        public QuizQuestion_GiveResourcesTroop(Random r) { super(r); }
        
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
        public QuizQuestion_GiveExtraTurnTroop(Random r) { super(r); }
        
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
        public QuizQuestion_SummonTransformTroop(Random r) { super(r); }
        
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
        public QuizQuestion_DrainManaTroop(Random r) { super(r); }
        
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

        public QuizQuestion_EffectsTroop(Random r) 
        {
            super(r);
            
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
     * Asks a user to identify which traitstone can be found in the specified kingdom.
     */
    private static class QuizQuestion_KingdomToTraitstone extends QuizQuestion_Kingdoms
    {
        public QuizQuestion_KingdomToTraitstone(Random r) { super(r); }

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
        public QuizQuestion_TraitstoneToKingdom(Random r) { super(r); }

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
     * Asks a user to identify which stat can be increased in the specified kingdom.
     */
    private static class QuizQuestion_KingdomToStat extends QuizQuestion_Kingdoms
    {
        public QuizQuestion_KingdomToStat(Random r) { super(r); }

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
        public QuizQuestion_StatToKingdom(Random r) { super(r); }

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
        public QuizQuestion_BannerArtToKingdom(Random r) { super(r); }

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
            }
            catch (MalformedURLException e)
            {}
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
        public QuizQuestion_ShieldArtToKingdom(Random r) { super(r); }

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
            }
            catch (MalformedURLException e)
            {}
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
     * Asks a user to identify which color weapons have affinity with a given class.
     */
    private static class QuizQuestion_ClassToBonusColor extends QuizQuestion_Classes
    {
        public QuizQuestion_ClassToBonusColor(Random r) { super(r); }

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
        public QuizQuestion_BonusColorToClass(Random r) { super(r); }

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
        
        public QuizQuestion_ClassToTrait(Random r) 
        {
            super(r);
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
        public QuizQuestion_TraitToClass(Random r) { super(r); }

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

    private static ArrayList<QuestionType> getTypesForDifficulty(QuizQuestion.Difficulty difficulty)
    {
        ArrayList<QuestionType> result = new ArrayList<>();
        
        for (int i = 0; i < QuestionType.Count; i++)
        {
            QuestionType type = QuestionType.fromInteger(i);
            if (type.difficulty == difficulty)
            {
                result.add(type);
            }
        }
        
        return result;
    }
 
    /**
     * Generates a random question of the specified type.
     * @param r The random number generator to use.
     * @param type The type of question to create.
     * @return A new question of the specified type.
     */
    public static QuizQuestion getQuestion(Random r, QuestionType type)
    {
        switch (type)
        {
            case TroopToKingdom:
                return new QuizQuestion_TroopToKingdom(r).initialize();
                
            case KingdomToTroop:
                return new QuizQuestion_KingdomToTroop(r).initialize();
                
            case TroopToSpell:
                return new QuizQuestion_TroopToSpell(r).initialize();

            case SpellToTroop:
                return new QuizQuestion_SpellToTroop(r).initialize();
                
            case TroopToType:
                return new QuizQuestion_TroopToType(r).initialize();
                
            case TypeToTroop:
                return new QuizQuestion_TypeToTroop(r).initialize();
                
            case TroopToColor:
                return new QuizQuestion_TroopToColor(r).initialize();
                
            case ColorToTroop:
                return new QuizQuestion_ColorToTroop(r).initialize();
                
            case TroopToRarity:
                return new QuizQuestion_TroopToRarity(r).initialize();
                
            case RarityToTroop:
                return new QuizQuestion_RarityToTroop(r).initialize();
                
            case TroopToTrait:
                return new QuizQuestion_TroopToTrait(r).initialize();
                
            case TraitToTroop:
                return new QuizQuestion_TraitToTroop(r).initialize();
                
            case FlavorTextToTroop:
                return new QuizQuestion_FlavorTextToTroop(r).initialize();
                
            case SpellArtToTroop:
                return new QuizQuestion_SpellArtToTroop(r).initialize();
                
            case TrueDamageTroop:
                return new QuizQuestion_TrueDamageTroop(r).initialize();
                
            case CreateGemsTroop:
                return new QuizQuestion_CreateGemsTroop(r).initialize();
                
            case ConvertGemsTroop:
                return new QuizQuestion_ConvertGemsTroop(r).initialize();
                
            case DestroyGemsTroop:
                return new QuizQuestion_DestroyGemsTroop(r).initialize();
                
            case IncreaseStatsTroop:
                return new QuizQuestion_IncreaseStatsTroop(r).initialize();
                
            case DecreaseStatsTroop:
                return new QuizQuestion_DecreaseStatsTroop(r).initialize();
                
            case GiveResourcesTroop:
                return new QuizQuestion_GiveResourcesTroop(r).initialize();
                
            case GiveExtraTurnTroop:
                return new QuizQuestion_GiveExtraTurnTroop(r).initialize();
                
            case SummonTransformTroop:
                return new QuizQuestion_SummonTransformTroop(r).initialize();
                
            case DrainManaTroop:
                return new QuizQuestion_DrainManaTroop(r).initialize();
                
            case EffectsTroop:
                return new QuizQuestion_EffectsTroop(r).initialize();
                
            case KingdomToTraitstone:
                return new QuizQuestion_KingdomToTraitstone(r).initialize();
                
            case TraitstoneToKingdom:
                return new QuizQuestion_TraitstoneToKingdom(r).initialize();
                
            case KingdomToStat:
                return new QuizQuestion_KingdomToStat(r).initialize();
                
            case StatToKingdom:
                return new QuizQuestion_StatToKingdom(r).initialize();
                
            case BannerArtToKingdom:
                return new QuizQuestion_BannerArtToKingdom(r).initialize();

            case ShieldArtToKingdom:
                return new QuizQuestion_ShieldArtToKingdom(r).initialize();

            case ClassToBonusColor:
                return new QuizQuestion_ClassToBonusColor(r).initialize();
                
            case BonusColorToClass:
                return new QuizQuestion_BonusColorToClass(r).initialize();

            case ClassToTrait:
                return new QuizQuestion_ClassToTrait(r).initialize();
                
            case TraitToClass:
                return new QuizQuestion_TraitToClass(r).initialize();
        }
        
        throw new InvalidParameterException("Invalid question type specified!");
    }
    
    /**
     * Generates a random question of the specified difficulty, and a random type.
     * @param r The random number generator to use.
     * @param difficulty The difficulty of question to create.
     * @return A new question of the specified type.
     */
    public static QuizQuestion getQuestion(Random r, QuizQuestion.Difficulty difficulty)
    {
        ArrayList<QuestionType> types = getTypesForDifficulty(difficulty);
        QuestionType type = types.get(r.nextInt(types.size()));
        return getQuestion(r, type);
    }

    /**
     * Generates a random question of a random type (and thereby a random difficulty).
     * @param r The random number generator to use.
     * @return A new question of the specified type.
     */
    public static QuizQuestion getQuestion(Random r)
    {
        ArrayList<QuestionType> types = getTypesForDifficulty(QuizQuestion.Difficulty.Easy);
        types.addAll(getTypesForDifficulty(QuizQuestion.Difficulty.Moderate));
        types.addAll(getTypesForDifficulty(QuizQuestion.Difficulty.Hard));
        QuestionType type = types.get(r.nextInt(types.size()));
        return getQuestion(r, type);
    }
}
