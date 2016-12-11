package me.samboycoding.krystarabot.quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import me.samboycoding.krystarabot.GameData;
import me.samboycoding.krystarabot.command.Top10Command;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.IDReference;
import org.json.JSONArray;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Handles the creation/destruction of a quiz, questions + answers, and scoring.
 *
 * @author Sam
 */
public class QuizHandler
{

    IChannel quizChannel = null;
    public static Thread quizThread = null;
    public static QuizQuestionTimer qt = null;

    private Question currentQ = null;
    private int lastDifficulty = -1;

    LinkedHashMap<IUser, Integer> unordered = new LinkedHashMap<>();
    Top10Command.ValueComparator comp = new Top10Command.ValueComparator((Map<IUser, Integer>) unordered);
    TreeMap<IUser, Integer> ordered = new TreeMap<>(comp);

    ArrayList<QuestionTemplate> easyTemplates = new ArrayList<>();
    ArrayList<QuestionTemplate> normTemplates = new ArrayList<>();
    ArrayList<QuestionTemplate> hardTemplates = new ArrayList<>();

    public QuizHandler()
    {
        //Troop
        //Easy #0
        easyTemplates.add(new QuestionTemplate("What kingdom is %%TROOP%% found in?", "kingdom", "troop"));
        //Easy #1
        easyTemplates.add(new QuestionTemplate("What is %%TROOP%%'s base rarity?", "rarity", "troop"));
        //Easy #2
        easyTemplates.add(new QuestionTemplate("Which of these troops does true damage as part of its spell?", "truedamage", "spell"));
        //Easy #3
        easyTemplates.add(new QuestionTemplate("Which of these troops creates (not converts) gems as part of its spell?", "creategems", "spell"));
        //Easy #4
        easyTemplates.add(new QuestionTemplate("Which of these troops destroys/explodes gems as part of its spell (and gives you the mana)?", "destroygems", "spell")); //If any spellstep has "DestroyGems" or "ExplodeGem"

        //Normal #0
        normTemplates.add(new QuestionTemplate("What is the name of %%TROOP%%'s spell?", "spellname", "troop"));
        //Normal #1
        normTemplates.add(new QuestionTemplate("What is/are %%TROOP%%'s types?", "type", "troop"));
        //Normal #2
        normTemplates.add(new QuestionTemplate("Which troop has the spell \"%%SPELL%%\"?", "spell", "troop"));
        //Normal #3
        normTemplates.add(new QuestionTemplate("Which troop has the trait \"%%TRAIT%%\"?", "trait", "troop")); //"trait" singular
        //Normal #4
        normTemplates.add(new QuestionTemplate("Which spell causes debuffs?", "debuff", "spell"));
        //Normal #5
        normTemplates.add(new QuestionTemplate("Which spell converts gems?", "convertgems", "spell"));
        //Normal #6
        normTemplates.add(new QuestionTemplate("Which spell removes gems (without giving you mana)?", "removegems", "spell"));
        //Normal #7
        normTemplates.add(new QuestionTemplate("Which spell can heal or increase stats of a troop or its allies?", "increasestat", "spell")); //"IncreaseArmor", "IncreaseAttack", "IncreaseHealth" or "IncreaseSpellPower"

        //Hard #0
        hardTemplates.add(new QuestionTemplate("What mana colors does %%TROOP%% use?", "manacolor", "troop")); //"manacolor" singular
        //Hard #1
        hardTemplates.add(new QuestionTemplate("Which of these is one of %%TROOP%%'s traits?", "traits", "troop")); //Note "traits" plural
        //Hard #2
        hardTemplates.add(new QuestionTemplate("What is the name of %%TROOP%%'s third trait?", "thirdtrait", "leg/mythic_troop"));
        //Hard #3
        hardTemplates.add(new QuestionTemplate("Which of these troops uses colors %%MANACOLOR1%% / %%MANACOLOR2%%?", "manacolors", "troop")); //Again, "manacolors" plural

        //Arcane traitstone - Cannot be done? No information on colors can be found.
        /*
        //Hard #4
        hardTemplates.add(new QuestionTemplate("What is the name of the %%COL1%% / %%COL2%% traitstone?", "colors", "traitstone")); //Singular
        //Hard #5
        hardTemplates.add(new QuestionTemplate("What color are arcane %%NAME%% traitstones?", "color", "traitstone")); //Plural
        //Hard #6
        hardTemplates.add(new QuestionTemplate("Where are Arcane %%NAME%% traitstones be found?", "location", "traitstone"));
         */
        //Kingdom
        //Easy #5
        easyTemplates.add(new QuestionTemplate("Which kingdom gives team bonuses \"Lord, Duke and King of %%TYPE%%\"?", "bonuses", "kingdom"));
        //Easy #6
        easyTemplates.add(new QuestionTemplate("Which of these troops is from %%NAME%%?", "troops", "kingdom"));
        //Easy #7
        easyTemplates.add(new QuestionTemplate("Which kingdom has the banner %%BANNERNAME%%?", "banner", "kingdom"));
        //Normal #8
        normTemplates.add(new QuestionTemplate("What stat bonus is unlocked from reaching level 10 in %%KINGDOMNAME%%?", "level10", "kingdom"));
    }

    public QuizQuestionTimer.QuizSubmitResult submitAnswer(Question question, IUser user, int answer)
    {
        synchronized (this)
        {
            if (qt.phase == QuizQuestionTimer.QuizPhase.Introduction)
            {
                return QuizQuestionTimer.QuizSubmitResult.TooEarly;
            } else if ((qt.phase == QuizQuestionTimer.QuizPhase.Pausing) || (qt.phase == QuizQuestionTimer.QuizPhase.Completed)
                    || (qt.q != question))
            {
                return QuizQuestionTimer.QuizSubmitResult.TooLate;
            }

            boolean isFirst = true;
            boolean isCorrect = (question.correctAnswer == answer);

            for (QuizQuestionTimer.QuizSubmitEntry entry : qt.submissions)
            {
                if (entry.user == user)
                {
                    return QuizQuestionTimer.QuizSubmitResult.AlreadyAnswered;
                }
                if (entry.result == QuizQuestionTimer.QuizSubmitResult.FirstCorrect)
                {
                    isFirst = false;
                }
            }

            QuizQuestionTimer.QuizSubmitResult result = QuizQuestionTimer.QuizSubmitResult.Incorrect;
            if (isCorrect)
            {
                result = isFirst ? QuizQuestionTimer.QuizSubmitResult.FirstCorrect : QuizQuestionTimer.QuizSubmitResult.Correct;
            }

            qt.submissions.add(new QuizQuestionTimer.QuizSubmitEntry(user, result));

            return result;
        }
    }

    public boolean isQuizRunning()
    {
        return qt != null || quizThread != null;
    }

    public IChannel getQuizChannel()
    {
        return quizChannel;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public void initializeQuiz(IGuild srv, IUser sdr, IChannel source) throws Exception
    {
        if (isQuizRunning())
        {
            source.sendMessage("There is a quiz in progress.  Please wait for it to finish before starting a new quiz. You can join it here: " + quizChannel.mention());
            return;
        }

        List<IChannel> quizChannels = srv.getChannelsByName("quiz");
        if (quizChannels.isEmpty())
        {
            quizChannel = srv.createChannel("quiz");
        } else
        {
            quizChannel = quizChannels.get(0);
            quizChannel.delete();

            srv.createChannel("quiz");

            quizChannel = quizChannels.get(0);
        }

        //quizChannel is now set
        unordered = new LinkedHashMap<>();
        comp = new Top10Command.ValueComparator((Map<IUser, Integer>) unordered);
        ordered = new TreeMap<>(comp);

        unordered.put(sdr, new Integer(0));

        quizThread = new Thread(new QuizStartTimer(quizChannel), "Quiz start timer");
        quizThread.start();

        if (quizChannel != source)
        {
            srv.getChannelByID(IDReference.GLOBALCHANNEL).sendMessage("A new quiz is starting in " + quizChannel.mention() + "!  Enter the channel to join in.");
        }
    }

    public Question generateQuestion(int difficulty)
    {
        Random r = new Random();
        
        Question theQ;

        lastDifficulty = difficulty;
        
        switch (difficulty)
        {
            case 1:
                theQ = handleTemplate(easyTemplates.get(r.nextInt(easyTemplates.size())), r);
                break;
            case 2:
                theQ = handleTemplate(normTemplates.get(r.nextInt(normTemplates.size())), r);
                break;
            case 3:
                theQ = handleTemplate(hardTemplates.get(r.nextInt(hardTemplates.size())), r);
                break;
            default:
                theQ = null;
                break;
        }

        currentQ = theQ;
        return theQ;
    }
    
    public void finishQuestion()
    {
        currentQ = null;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public void handleAnswer(IMessage msg) throws MissingPermissionsException, RateLimitException, DiscordException
    {
        Question theQ = currentQ;

        if (!isQuizRunning())
        {
            msg.getAuthor().getOrCreatePMChannel().sendMessage("The quiz hasn't started yet, or has finished. Sorry!");
            msg.delete();
            return;
        }

        IUser usr = msg.getAuthor();
        String text = msg.getContent();
        IChannel c = msg.getChannel();

        msg.delete();

        int currentScore = 0;
        if (!unordered.containsKey(usr))
        {
            unordered.put(usr, currentScore);
        } else
        {
            currentScore = unordered.get(usr);
        }

        int answer = Integer.parseInt(text); //We can just do this as Listener checks if it is valid

        int pos = answer - 1;

        int scoreDelta = 0;

        switch (submitAnswer(theQ, usr, pos))
        {
            case Incorrect:
                break;
            case Correct:
                scoreDelta = lastDifficulty;
                break;
            case FirstCorrect:
                scoreDelta = lastDifficulty + 2;
                break;
            case AlreadyAnswered:
                break;
            case TooEarly:
                break;
            case TooLate:
                break;
            default:
                break;
        }
        if (scoreDelta > 0)
        {
            //Correct
            currentScore += scoreDelta;
            unordered.remove(usr);
            unordered.put(usr, new Integer(currentScore));
        }
    }

    public Question getSpecificQuestion(int difficulty, int index)
    {
        System.out.println("Getting Question! Diff: " + difficulty + " index: " + index);

        QuestionTemplate templ;
        switch (difficulty)
        {
            case 1:
                if (index >= easyTemplates.size())
                {
                    return null;
                }
                templ = easyTemplates.get(index);
                break;
            case 2:
                if (index >= normTemplates.size())
                {
                    return null;
                }
                templ = normTemplates.get(index);
                break;
            case 3:
                if (index >= hardTemplates.size())
                {
                    return null;
                }
                templ = hardTemplates.get(index);
                break;
            default:
                return null;
        }

        System.out.println(templ.templateText);
        return handleTemplate(templ, new Random());
    }

    private Question handleTemplate(QuestionTemplate templ, Random r)
    {
        String type = templ.searchIn;
        //Possible types: "troop", "spell", "leg/mythic_troop", "traitstone", "kingdom"

        switch (type)
        {
            case "troop":
                return handleTroopTemplate(templ, r);

            case "spell":
                return handleSpellTemplate(templ, r);
            /*
            case "traitstone":
                return handleTraitstoneTemplate(templ, r);
             */
            case "kingdom":
                return handleKingdomTemplate(templ, r);
        }

        //Leg/mythic troop
        //Only question is What is the name of %%TROOP%%'s third trait?
        JSONObject randomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));
        while (randomTroop.getInt("RarityIndex") < 5)
        {
            randomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));
        }
        String qText = templ.templateText.replace("%%TROOP%%", randomTroop.getString("Name"));

        ArrayList<String> answers = new ArrayList<>();
        answers.add(randomTroop.getJSONArray("ParsedTraits").getJSONObject(2).getString("Name"));

        while (answers.size() < 4)
        {
            JSONObject anotherRandomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));
            while (anotherRandomTroop.getInt("RarityIndex") < 5)
            {
                anotherRandomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));
            }

            String trait = anotherRandomTroop.getJSONArray("ParsedTraits").getJSONObject(2).getString("Name");
            if (!answers.contains(trait))
            {
                answers.add(trait);
            }
        }

        return new Question(qText, answers, 0);
    }

    private Question handleTroopTemplate(QuestionTemplate temp, Random r)
    {
        String search = temp.searchFor;
        //Get a random troop JSONObject
        JSONObject randomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));

        Question result = null;
        String questionText;
        ArrayList<String> answers;

        //"kingdom", "rarity", "spellname", "type", "spell", "trait", "manacolor", "traits", "manacolors"
        switch (search)
        {
            case "kingdom":
                //Which kingdom is x found in?
                questionText = temp.templateText.replace("%%TROOP%%", randomTroop.getString("Name"));
                String correctKingdom = randomTroop.getString("Kingdom");

                answers = new ArrayList<>();
                answers.add(correctKingdom);
                while (answers.size() < 4)
                {
                    JSONObject randomKingdom = GameData.arrayKingdoms.getJSONObject(r.nextInt(GameData.arrayKingdoms.length()));
                    String name = randomKingdom.getString("Name");
                    if (!answers.contains(name))
                    {
                        answers.add(name);
                    }
                }

                result = new Question(questionText, answers, 0);
                break;
            case "rarity":
                //What is x's base rarity?
                //Get its rarity and get some others for incorrect
                questionText = temp.templateText.replace("%%TROOP%%", randomTroop.getString("Name"));
                String correctRarity = randomTroop.getString("TroopRarity");
                answers = new ArrayList<>();
                answers.add(correctRarity);
                String[] rarities = new String[]
                {
                    "Common", "Rare", "Ultra-Rare", "Epic", "Legendary", "Mythic"
                };

                while (answers.size() < 4)
                {
                    int rnd = r.nextInt(rarities.length);
                    String rar = rarities[rnd];

                    if (!answers.contains(rar))
                    {
                        answers.add(rar);
                    }
                }

                result = new Question(questionText, answers, 0);
                break;
            case "spellname":
                //What is x's spell's name?
                questionText = temp.templateText.replace("%%TROOP%%", randomTroop.getString("Name"));
                String correctSpellName = randomTroop.getJSONObject("Spell").getString("Name");
                answers = new ArrayList<>();
                answers.add(correctSpellName);

                while (answers.size() < 4)
                {
                    JSONObject randomSpell = GameData.arraySpells.getJSONObject(r.nextInt(GameData.arraySpells.length()));
                    String spellName = randomSpell.getString("Name");

                    if (!answers.contains(spellName))
                    {
                        answers.add(spellName);
                    }
                }

                result = new Question(questionText, answers, 0);
                break;
            case "type":
                //What is/are x's types?
                questionText = temp.templateText.replace("%%TROOP%%", randomTroop.getString("Name"));
                String correctType = randomTroop.getString("Type").replace("-", "/");
                answers = new ArrayList<>();
                answers.add(correctType);

                while (answers.size() < 4)
                {
                    JSONObject anotherRandomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));

                    String typ = anotherRandomTroop.getString("Type").replace("-", "/");
                    if (!answers.contains(typ))
                    {
                        answers.add(typ);
                    }
                }

                result = new Question(questionText, answers, 0);
                break;
            case "spell":
                //Which troop has spell x?
                questionText = temp.templateText.replace("%%SPELL%%", randomTroop.getJSONObject("Spell").getString("Name"));
                String correctTroop = randomTroop.getString("Name");
                answers = new ArrayList<>();
                answers.add(correctTroop);

                while (answers.size() < 4)
                {
                    JSONObject anotherRandomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));

                    String spl = anotherRandomTroop.getJSONObject("Spell").getString("Name");

                    if (!spl.equals(randomTroop.getJSONObject("Spell").getString("Name")))
                    {
                        answers.add(anotherRandomTroop.getString("Name"));
                    }
                }

                result = new Question(questionText, answers, 0);
                break;
            case "trait":
                //Which troop has trait x?
                String trait = randomTroop.getJSONArray("ParsedTraits").getJSONObject(r.nextInt(3)).getString("Name");
                questionText = temp.templateText.replace("%%TRAIT%%", trait);
                String correctTroop2 = randomTroop.getString("Name");
                answers = new ArrayList<>();
                answers.add(correctTroop2);

                while (answers.size() < 4)
                {
                    JSONObject anotherRandomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));

                    String trpnme = anotherRandomTroop.getString("Name");
                    boolean hasTrait = false;
                    for (Object t : anotherRandomTroop.getJSONArray("ParsedTraits"))
                    {
                        JSONObject trt = (JSONObject) t;
                        if (trt.getString("Name").equals(trait))
                        {
                            hasTrait = true;
                            break;
                        }
                    }

                    if (hasTrait)
                    {
                        continue;
                    }

                    if (!answers.contains(trpnme))
                    {
                        answers.add(trpnme);
                    }
                }

                result = new Question(questionText, answers, 0);
                break;
            case "manacolor":
                //What mana colors does x use?
                String correctColors = "";

                while (!correctColors.contains("/"))
                {
                    JSONObject manaColors = randomTroop.getJSONObject("ManaColors");
                    for (String color : manaColors.keySet())
                    {
                        if (color.equals("ColorOrange"))
                        {
                            continue; //Ignore orange - it's not in the game.
                        }

                        if (manaColors.getBoolean(color))
                        {
                            correctColors += color.replace("Color", "") + "/";
                        }
                    }
                    correctColors = correctColors.substring(0, correctColors.length() - 1); //Remove trailing slash

                    if (!correctColors.contains("/"))
                    {
                        correctColors = "";
                        randomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));
                    }
                }

                questionText = temp.templateText.replace("%%TROOP%%", randomTroop.getString("Name"));
                answers = new ArrayList<>();
                answers.add(correctColors);

                while (answers.size() < 4)
                {
                    JSONObject anotherRandomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));

                    String correctColors2 = "";
                    JSONObject manaColors2 = anotherRandomTroop.getJSONObject("ManaColors");

                    for (String color : manaColors2.keySet())
                    {
                        if (color.equals("ColorOrange"))
                        {
                            continue; //Ignore orange - it's not in the game.
                        }

                        if (manaColors2.getBoolean(color))
                        {
                            correctColors2 += color.replace("Color", "") + "/";
                        }
                    }
                    correctColors2 = correctColors2.substring(0, correctColors2.length() - 1); //Remove trailing slash

                    if (!correctColors2.contains("/"))
                    {
                        continue; //Ensure we get two colors.
                    }

                    if (correctColors2.equals(correctColors))
                    {
                        continue; //Don't get duplicate answers
                    }

                    if (!answers.contains(correctColors2))
                    {
                        answers.add(correctColors2);
                    }
                }

                result = new Question(questionText, answers, 0);
                break;
            case "traits":
                //Which of these is one of x's traits?
                questionText = temp.templateText.replace("%%TROOP%%", randomTroop.getString("Name"));
                String correctTrait = randomTroop.getJSONArray("ParsedTraits").getJSONObject(0).getString("Name"); //Get a random trait from the troop
                answers = new ArrayList<>();
                answers.add(correctTrait);

                while (answers.size() < 4)
                {
                    JSONObject anotherRandomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));

                    String trait2 = anotherRandomTroop.getJSONArray("ParsedTraits").getJSONObject(0).getString("Name");
                    if (correctTrait.equals(trait2)) //If the current trait is the same as the correct one
                    {
                        continue; //Get another
                    }

                    if (!answers.contains(trait2))
                    {
                        answers.add(trait2);
                    }
                }

                result = new Question(questionText, answers, 0);
                break;
            case "manacolors":
                //Which of these troops uses %%MANACOLOR1%% / %%MANACOLOR2%%?

                ArrayList<String> manaCols = new ArrayList<>();

                //Loop until we get a troop with 2 colors.
                while (manaCols.size() != 2)
                {
                    manaCols.clear();
                    for (String manacol : randomTroop.getJSONObject("ManaColors").keySet())
                    {
                        if (manacol.equals("ColorOrange"))
                        {
                            continue; //Ignore orange
                        }
                        if (randomTroop.getJSONObject("ManaColors").getBoolean(manacol))
                        {
                            manaCols.add(manacol.replace("Color", ""));
                        }
                    }
                    if (manaCols.size() != 2)
                    {
                        randomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())); //Get another troop
                    }
                }

                questionText = temp.templateText.replace("%%MANACOLOR1%%", manaCols.get(0)).replace("%%MANACOLOR2%%", manaCols.get(1));
                String correctTroop3 = randomTroop.getString("Name");
                answers = new ArrayList<>();
                answers.add(correctTroop3);

                while (answers.size() < 4)
                {
                    JSONObject anotherRandomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));

                    String nm = anotherRandomTroop.getString("Name");

                    if (!answers.contains(nm))
                    {
                        answers.add(nm);
                    }
                }
                result = new Question(questionText, answers, 0);
                break;
        }

        return result;
    }

    private Question handleSpellTemplate(QuestionTemplate temp, Random r)
    {
        //Possible values: "truedamge", "creategems", "generatemana", "destroygems", "debuff", "convertgems", "removecolor", "increasestat"

        Question result;
        ArrayList<String> answers;

        String sf = temp.searchFor;

        switch (sf)
        {
            case "truedamage":
                //Which of these troop's spells does true damage?
                boolean tdFound = false;
                String correctTroop = null;

                while (!tdFound)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (step.getString("Type").equals("TrueDamage") || step.getString("Type").equals("TrueSplashDamage") || step.getString("Type").equals("TrueScatterDamage"))
                        {
                            tdFound = true;
                            for (Object t : GameData.arrayTroops)
                            {
                                JSONObject trp = (JSONObject) t;
                                if (trp.getJSONObject("Spell").getString("Name").equals(randomSpell.getString("Name")))
                                {
                                    correctTroop = trp.getString("Name");
                                    break;
                                }
                            }
                        }
                    }
                }

                answers = new ArrayList<>();
                answers.add(correctTroop);

                while (answers.size() < 4)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    boolean hasTrueDamage = false;
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (step.getString("Type").equals("TrueDamage") || step.getString("Type").equals("TrueSplashDamage") || step.getString("Type").equals("TrueScatterDamage"))
                        {
                            hasTrueDamage = true;
                            break;
                        }
                    }
                    if (!hasTrueDamage)
                    {
                        for (Object t : GameData.arrayTroops)
                        {
                            JSONObject trp = (JSONObject) t;
                            if (trp.getJSONObject("Spell").getString("Name").equals(randomSpell.getString("Name")))
                            {
                                answers.add(trp.getString("Name"));
                                break;
                            }
                        }
                    }
                }

                result = new Question(temp.templateText, answers, 0); //No need to format text.
                break;
            case "creategems":
                //Which of these troop's spells creates gems?
                //Spellstep.type = "CreateGems"

                boolean cgFound = false;
                correctTroop = null;
                while (!cgFound)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (step.getString("Type").equals("CreateGems") || step.getString("Type").equals("CreateGems2Colors"))
                        {
                            cgFound = true;
                            for (Object t : GameData.arrayTroops)
                            {
                                JSONObject trp = (JSONObject) t;
                                if (trp.getJSONObject("Spell").getString("Name").equals(randomSpell.getString("Name")))
                                {
                                    correctTroop = trp.getString("Name");
                                    break;
                                }
                            }
                        }
                    }
                }

                answers = new ArrayList<>();
                answers.add(correctTroop);

                while (answers.size() < 4)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    boolean createsGems = false;
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (step.getString("Type").equals("CreateGems") || step.getString("Type").equals("CreateGems2Colors"))
                        {
                            createsGems = true;
                            break;
                        }
                    }
                    if (!createsGems)
                    {
                        for (Object t : GameData.arrayTroops)
                        {
                            JSONObject trp = (JSONObject) t;
                            if (trp.getJSONObject("Spell").getString("Name").equals(randomSpell.getString("Name")) && !answers.contains(trp.getString("Name")))
                            {
                                answers.add(trp.getString("Name"));
                                break;
                            }
                        }
                    }
                }

                result = new Question(temp.templateText, answers, 0); //No need to format text.
                break;
            /*
            case "generatemana":
                //Which of these spells can be used to generate mana?
                //If any spellstep has "ConvertGems", "CreateGems", "GenerateMana", "ExplodeGem" or "DestroyGems". NOT "RemoveColor" - it doesn't give you mana
                boolean gmFound = false;
                correctSpell = null;

                ArrayList<String> acceptableSpellTypes = new ArrayList<>(Arrays.asList("ConvertGems", "CreateGems", "GenerateMana", "ExplodeGem", "ExplodeGems", "DestroyGems"));
                while (!gmFound)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (acceptableSpellTypes.contains(step.getString("Type")))
                        {
                            gmFound = true;
                            correctSpell = randomSpell.getString("Name");
                        }
                    }
                }

                answers = new ArrayList<>();
                answers.add(correctSpell);

                while (answers.size() < 4)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    boolean generatesMana = false;
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (acceptableSpellTypes.contains(step.getString("Type")))
                        {
                            generatesMana = true;
                            break;
                        }
                    }
                    if (!generatesMana)
                    {
                        answers.add(randomSpell.getString("Name"));
                    }
                }

                result = new Question(temp.templateText, answers, 0);
                break;
             */
            case "destroygems":
                //Which of these troop's spells destroys/explodes (and gives you the mana)?
                //If any spellstep has "DestroyGems" or "ExplodeGem", NOT "RemoveColor"
                boolean dgFound = false;
                correctTroop = null;

                ArrayList<String> acceptableSpellTypes = new ArrayList<>(Arrays.asList("ExplodeGem", "DestroyGems", "ExplodeGems", "DestroyGems", "DestroyRow"));
                while (!dgFound)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (acceptableSpellTypes.contains(step.getString("Type")))
                        {
                            dgFound = true;
                            for (Object t : GameData.arrayTroops)
                            {
                                JSONObject trp = (JSONObject) t;
                                if (trp.getJSONObject("Spell").getString("Name").equals(randomSpell.getString("Name")))
                                {
                                    correctTroop = trp.getString("Name");
                                    break;
                                }
                            }
                        }
                    }
                }

                answers = new ArrayList<>();
                answers.add(correctTroop);

                while (answers.size() < 4)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    boolean destroysGems = false;
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (acceptableSpellTypes.contains(step.getString("Type")))
                        {
                            destroysGems = true;
                            break;
                        }
                    }
                    if (!destroysGems)
                    {
                        for (Object t : GameData.arrayTroops)
                        {
                            JSONObject trp = (JSONObject) t;
                            if (trp.getJSONObject("Spell").getString("Name").equals(randomSpell.getString("Name")))
                            {
                                answers.add(trp.getString("Name"));
                                break;
                            }
                        }
                    }
                }

                result = new Question(temp.templateText, answers, 0);
                break;
            case "debuff":
                //Which of these spells causes debuffs?
                boolean debuffFound = false;
                correctTroop = null;

                while (!debuffFound)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if ((step.getString("Type").contains("Cause") && !step.getString("Type").equals("CausesBarrier")) || step.getString("Type").equals("RandomStatusEffect"))
                        {
                            debuffFound = true;
                            correctTroop = randomSpell.getString("Name");
                        }
                    }
                }

                answers = new ArrayList<>();
                answers.add(correctTroop);

                while (answers.size() < 4)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    boolean causesDebuff = false;
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if ((step.getString("Type").contains("Cause") && !step.getString("Type").equals("CausesBarrier")) || step.getString("Type").equals("RandomStatusEffect"))
                        {
                            causesDebuff = true;
                            break;
                        }
                    }
                    if (!causesDebuff)
                    {
                        answers.add(randomSpell.getString("Name"));
                    }
                }

                result = new Question(temp.templateText, answers, 0);
                break;
            case "convertgems":
                //Which spell converts gems?
                boolean convGFound = false;
                correctTroop = null;

                while (!convGFound)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (step.getString("Type").equals("ConvertGems"))
                        {
                            convGFound = true;
                            correctTroop = randomSpell.getString("Name");
                        }
                    }
                }

                answers = new ArrayList<>();
                answers.add(correctTroop);

                while (answers.size() < 4)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    boolean convertsGems = false;
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (step.getString("Type").equals("ConvertGems"))
                        {
                            convertsGems = true;
                            break;
                        }
                    }
                    if (!convertsGems)
                    {
                        answers.add(randomSpell.getString("Name"));
                    }
                }

                result = new Question(temp.templateText, answers, 0);
                break;
            case "removegems":
                //Which spell removes gems, without giving you the mana?
                boolean rcFound = false;
                correctTroop = null;

                while (!rcFound)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (step.getString("Type").equals("RemoveColor") || step.getString("Type").equals("RemoveGems"))
                        {
                            rcFound = true;
                            correctTroop = randomSpell.getString("Name");
                        }
                    }
                }

                answers = new ArrayList<>();
                answers.add(correctTroop);

                while (answers.size() < 4)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    boolean removesColor = false;
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (step.getString("Type").equals("RemoveColor") || step.getString("Type").equals("RemoveGems"))
                        {
                            removesColor = true;
                            break;
                        }
                    }
                    if (!removesColor)
                    {
                        answers.add(randomSpell.getString("Name"));
                    }
                }

                result = new Question(temp.templateText, answers, 0);
                break;
            case "increasestat":
                //Which spell can heal or increase stats of a troop or its allies?
                boolean increaseStatFound = false;
                correctTroop = null;

                acceptableSpellTypes = new ArrayList<>(Arrays.asList("Heal", "IncreaseHealth", "IncreaseArmor", "IncreaseAttack", "IncreaseSpellPower", "IncreaseRandom", "IncreaseAllStats", "StealRandomStat", "StealAttack", "StealArmor", "StealLife", "StealMagic", "Consume", "ConsumeConditional"));
                while (!increaseStatFound)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (acceptableSpellTypes.contains(step.getString("Type")))
                        {
                            increaseStatFound = true;
                            correctTroop = randomSpell.getString("Name");
                        }
                    }
                }

                answers = new ArrayList<>();
                answers.add(correctTroop);

                while (answers.size() < 4)
                {
                    JSONObject randomSpell = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length())).getJSONObject("Spell");
                    JSONArray spellSteps = randomSpell.getJSONArray("SpellSteps");
                    boolean increasesStat = false;
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (acceptableSpellTypes.contains(step.getString("Type")))
                        {
                            increasesStat = true;
                            break;
                        }
                    }
                    if (!increasesStat)
                    {
                        answers.add(randomSpell.getString("Name"));
                    }
                }

                result = new Question(temp.templateText, answers, 0);
                break;
            default:
                main.logToBoth("Unknown question type " + temp.searchFor);
                result = null;
                break;
        }
        return result;
    }

    private Question handleTraitstoneTemplate(QuestionTemplate temp, Random r)
    {
        //"color", "colors", or "location"

        //As far as i can see there is no way to find the colors of traitstones.
        /*JSONArray traitstones = new JSONArray();
        for(Object k : GameData.arrayKingdoms)
        {
            JSONObject kingdom = (JSONObject) k;
            
            JSONObject traitstone = 
        }*/
        //TODO: Investigate.
        Question result = null;
        ArrayList<String> answers;

        switch (temp.searchFor)
        {
            case "colors":
                //What is the name of the %%COL1%% / %%COL2%% traitstone?
                break;
            case "color":
                //What color are arcane %%NAME%% traitstones?
                break;
            case "location":
                //Where are Arcane %%NAME%% traitstones be found?
                break;
            default:
                main.logToBoth("Unknown question type " + temp.searchFor);
                result = null;
                break;
        }
        return result;
    }

    private Question handleKingdomTemplate(QuestionTemplate temp, Random r)
    {
        Question result = null;
        ArrayList<String> answers;

        JSONObject randomKingdom = GameData.arrayKingdoms.getJSONObject(r.nextInt(GameData.arrayKingdoms.length()));

        switch (temp.searchFor)
        {
            case "bonuses":
                //Which kingdom has bonuses "Lord, Duke and King of %%TYPE%%"?
                String fullBonusString = randomKingdom.getJSONObject("Bonus2").getString("Name");
                String bonusType = fullBonusString.substring(fullBonusString.lastIndexOf(" ") + 1);
                String questionText = temp.templateText.replace("%%TYPE%%", bonusType);

                answers = new ArrayList<>();

                String correctKingdom = randomKingdom.getString("Name");
                answers.add(correctKingdom);

                while (answers.size() < 4)
                {
                    JSONObject anotherRandomKingdom = GameData.arrayKingdoms.getJSONObject(r.nextInt(GameData.arrayKingdoms.length()));
                    String bnsStringFull = anotherRandomKingdom.getJSONObject("Bonus2").getString("Name");
                    String bnsString = bnsStringFull.substring(bnsStringFull.lastIndexOf(" ") + 1);

                    if (!bnsString.equals(bonusType))
                    {
                        answers.add(anotherRandomKingdom.getString("Name"));
                    }
                }

                result = new Question(questionText, answers, 0);
                break;
            case "troops":
                //Which of these troops is from %%NAME%%?
                int randomTroopId = -1;
                while (randomTroopId == -1)
                {
                    randomTroopId = randomKingdom.getJSONArray("TroopIds").getInt(r.nextInt(randomKingdom.getJSONArray("TroopIds").length()));
                }

                JSONObject randomTroop = main.data.getTroopById(randomTroopId);

                if (randomTroop == null)
                {
                    throw new NullPointerException("RandomTroop is null!");
                }
                questionText = temp.templateText.replace("%%NAME%%", randomKingdom.getString("Name"));

                answers = new ArrayList<>();

                correctKingdom = randomTroop.getString("Name");
                answers.add(correctKingdom);

                while (answers.size() < 4)
                {
                    JSONObject anotherRandomKingdom = GameData.arrayKingdoms.getJSONObject(r.nextInt(GameData.arrayKingdoms.length()));
                    JSONObject anotherRandomTroop = null;
                    while (anotherRandomTroop == null)
                    {
                        int anotherRandomTroopId = -1;
                        while (anotherRandomTroopId == -1)
                        {
                            anotherRandomTroopId = anotherRandomKingdom.getJSONArray("TroopIds").getInt(r.nextInt(anotherRandomKingdom.getJSONArray("TroopIds").length()));
                        }
                        anotherRandomTroop = main.data.getTroopById(anotherRandomTroopId);
                    }

                    if (!answers.contains(anotherRandomTroop.getString("Name")))
                    {
                        answers.add(anotherRandomTroop.getString("Name"));
                    }
                }

                result = new Question(questionText, answers, 0);
                break;

            case "banner":
                //Which kingdom has the banner %%BANNERNAME%%?
                String banner = randomKingdom.getString("BannerName");

                while(banner.equals("Unnamed Banner"))
                {
                    randomKingdom = GameData.arrayKingdoms.getJSONObject(r.nextInt(GameData.arrayKingdoms.length()));
                    banner = randomKingdom.getString("BannerName");
                }
                
                questionText = temp.templateText.replace("%%BANNERNAME%%", banner);
                String correct = randomKingdom.getString("Name");
                answers = new ArrayList<>();
                answers.add(correct);

                while (answers.size() < 4)
                {
                    JSONObject anotherRandomKingdom = GameData.arrayKingdoms.getJSONObject(r.nextInt(GameData.arrayKingdoms.length()));
                    String kngName = anotherRandomKingdom.getString("Name");

                    if (!answers.contains(kngName))
                    {
                        answers.add(kngName);
                    }
                }

                result = new Question(questionText, answers, 0);
                break;
            case "level10":
                //What stat bonus is unlocked from reaching level 10 in %%KINGDOMNAME%%

                ArrayList<String> validValues = new ArrayList<>(Arrays.asList("Health", "Armor", "Attack", "Magic"));
                while (randomKingdom.isNull("LevelData"))
                {
                    randomKingdom = GameData.arrayKingdoms.getJSONObject(r.nextInt(GameData.arrayKingdoms.length()));
                }
                String text = randomKingdom.getJSONObject("LevelData").getString("Stat");
                questionText = temp.templateText.replace("%%KINGDOMNAME%%", randomKingdom.getString("Name"));
                String stat;

                switch (text)
                {
                    case "armor":
                        stat = validValues.get(1);
                        break;
                    case "magic":
                        stat = validValues.get(3);
                        break;
                    case "attack":
                        stat = validValues.get(2);
                        break;
                    case "life":
                        stat = validValues.get(0);
                        break;
                    default:
                        stat = "INVALIDSTAT";
                        break;
                }

                answers = new ArrayList<>();
                answers.add(stat);

                while (answers.size() < 4)
                {
                    String val = validValues.get(r.nextInt(4));
                    if (!answers.contains(val))
                    {
                        answers.add(val);
                    }
                }

                result = new Question(questionText, answers, 0);
                break;
            default:
                main.logToBoth("Unknown question type " + temp.searchFor);
                result = null;
                break;
        }
        return result;
    }
}
