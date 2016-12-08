package me.samboycoding.krystarabot.quiz;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import me.samboycoding.krystarabot.GameData;
import me.samboycoding.krystarabot.utilities.IDReference;
import org.json.JSONArray;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RoleBuilder;

/**
 * Handles the creation/destruction of a quiz, questions + answers, and scoring.
 *
 * @author Sam
 */
public class QuizHandler
{

    IChannel quizChannel = null;
    IRole quizRole = null;

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
        easyTemplates.add(new QuestionTemplate("Which of these spells does true damage?", "truedamage", "spell"));
        //Easy #3
        easyTemplates.add(new QuestionTemplate("Which of these spells creates gems?", "creategems", "spell"));
        //Easy #4
        easyTemplates.add(new QuestionTemplate("Which of these spells can be used to generate mana?", "generatemana", "spell")); //If any spellstep has "ConvertGems", "CreateGems", "GenerateMana", "ExplodeGem" or "DestroyGems". NOT "RemoveColor" - it doesn't give you mana
        //Easy #5
        easyTemplates.add(new QuestionTemplate("Which of these spells destroys gems?", "destroygems", "spell")); //If any spellstep has "DestroyGems" or "ExplodeGem"

        //Normal #0
        normTemplates.add(new QuestionTemplate("What is %%TROOP%%'s spell's name?", "spellname", "troop"));
        //Normal #1
        normTemplates.add(new QuestionTemplate("What is/are %%TROOP%%'s types?", "type", "troop"));
        //Normal #2
        normTemplates.add(new QuestionTemplate("Which troop has spell \"%%SPELL%%\"?", "spell", "troop"));
        //Normal #3
        normTemplates.add(new QuestionTemplate("Which troop has trait \"%%TRAIT%%\"", "trait", "troop")); //"trait" singular
        //Normal #4
        normTemplates.add(new QuestionTemplate("Which spell causes debuffs?", "debuff", "spell"));
        //Normal #5
        normTemplates.add(new QuestionTemplate("Which spell converts gems?", "convertgems", "spell"));
        //Normal #6
        normTemplates.add(new QuestionTemplate("Which spell deletes gems (without giving you mana)?", "removecolor", "spell"));
        //Normal #7
        normTemplates.add(new QuestionTemplate("Which spell gives stats?", "increasestat", "spell")); //"IncreaseArmor", "IncreaseAttack", "IncreaseHealth" or "IncreaseSpellPower"

        //Hard #0
        hardTemplates.add(new QuestionTemplate("What mana colors does %%TROOP%% use?", "manacolor", "troop")); //"manacolor" singular
        //Hard #1
        hardTemplates.add(new QuestionTemplate("Which of these is one of %%TROOP%%'s traits?", "traits", "troop")); //Note "traits" plural
        //Hard #2
        hardTemplates.add(new QuestionTemplate("What is the name of %%TROOP%%'s third trait?", "thirdtrait", "leg/mythic_troop"));
        //Hard #3
        hardTemplates.add(new QuestionTemplate("Which of these troops uses %%MANACOLOR1%% / %%MANACOLOR2%%?", "manacolors", "troop")); //Again, "manacolors" plural

        //Arcane traitstone
        //Hard #4
        hardTemplates.add(new QuestionTemplate("What is the name of the %%COL1%% / %%COL2%% traitstone?", "color", "traitstone")); //Singular
        //Hard #5
        hardTemplates.add(new QuestionTemplate("What color are arcane %%NAME%% traitstones?", "colors", "traitstone")); //Plural
        //Hard #6
        hardTemplates.add(new QuestionTemplate("Where are Arcane %%NAME%% traitstones be found?", "location", "traitstone"));

        //Kingdom
        //Easy #6
        easyTemplates.add(new QuestionTemplate("Which kingdom have bonuses \"Lord, Duke and King of %%TYPE%%\"?", "bonuses", "kingdom"));
        //Easy #7
        easyTemplates.add(new QuestionTemplate("Which of these troops is from %%NAME%%?", "troops", "kingdom"));
        //Easy #8
        easyTemplates.add(new QuestionTemplate("Which kingdom has the banner %%BANNERNAME%%?", "banner", "kingdom"));
        //Normal #8
        normTemplates.add(new QuestionTemplate("What stat bonus is unlocked from reaching level 10 in %%KINGDOMNAME%%?", "level10", "kingdom"));
    }

    public void initializeQuiz(IGuild srv, IUser sdr, IChannel source) throws Exception
    {
        EnumSet<Permissions> sendReceiveMessages = EnumSet.of(Permissions.SEND_MESSAGES, Permissions.READ_MESSAGES, Permissions.READ_MESSAGE_HISTORY);
        if (srv.getChannelsByName("Quiz").isEmpty())
        {
            quizChannel = srv.createChannel("Quiz");
            quizChannel.overrideRolePermissions(srv.getEveryoneRole(), null, sendReceiveMessages); //Disallow @everyone to use the channel
        } else
        {
            //If the quiz channel exists, assume that the quiz is running
            throw new IllegalStateException("Tried to initialize a quiz when one is already running!");
        }

        //quizChannel is now set
        if (srv.getRolesByName("Quiz").isEmpty())
        {
            quizRole = new RoleBuilder(srv)
                    .withName("Quiz")
                    .withColor(Color.blue)
                    .build();
        } else
        {
            //If the quiz role exists, assume that the quiz is running
            throw new IllegalStateException("Tried to initialize a quiz when one is already running!");
        }

        sdr.addRole(quizRole);

        quizChannel.overrideRolePermissions(quizRole, sendReceiveMessages, null); //Allow members with the "quiz" role to access the channel
        quizChannel.overrideRolePermissions(srv.getRoleByID(IDReference.MODROLE), sendReceiveMessages, null); //Allow members with the moderator role to access the channel

        new Thread(new QuizStartTimer(quizChannel), "Quiz start timer").start();
    }

    public Question generateQuestion(int difficulty)
    {
        Random r = new Random();

        switch (difficulty)
        {
            case 1:
                return handleTemplate(easyTemplates.get(r.nextInt(easyTemplates.size())), r);
            case 2:
                return handleTemplate(normTemplates.get(r.nextInt(normTemplates.size())), r);
            case 3:
                return handleTemplate(hardTemplates.get(r.nextInt(hardTemplates.size())), r);
        }
        return null; //Invalid difficulty.
    }

    public Question getSpecificQuestion(int difficulty, int index)
    {
        System.out.println("Getting Question! Diff: " + difficulty + " index: " + index);
        
        QuestionTemplate templ;
        switch (difficulty)
        {
            case 1:
                if(index >= easyTemplates.size())
                {
                    return null;
                }
                templ = easyTemplates.get(index);
                break;
            case 2:
                if(index >= normTemplates.size())
                {
                    return null;
                }
                templ = normTemplates.get(index);
                break;
            case 3:
                if(index >= hardTemplates.size())
                {
                    return null;
                }
                templ = hardTemplates.get(index);
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

        /*
        switch (type)
        {
            case "troop":
         */
        //TEMP: Continue looping until we get a troop question
        while (!type.equals("troop"))
        {
            templ = easyTemplates.get(r.nextInt(easyTemplates.size()));
            type = templ.searchIn;
        }
        return handleTroopTemplate(templ, r);
        /*
            case "spell":
                return handleSpellTemplate(templ, r);
            case "traitstone":
                return handleTraitstoneTemplate(templ, r);
            case "kingdom":
                return handleKingdomTemplate(templ, r);
        }

        //Leg/mythic troop
        //Only question is "Which of these troops uses %%MANACOLOR1%% / %%MANACOLOR2%%?"
        return null; //TODO
                
         */
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
                    if (!answers.contains(spl))
                    {
                        answers.add(spl);
                    }
                }

                result = new Question(questionText, answers, 0);
                break;
            case "trait":
                //Which troop has trait x?
                String trait = randomTroop.getJSONArray("Traits").getString(r.nextInt(3));
                questionText = temp.templateText.replace("%%TRAIT%%", trait);
                String correctTroop2 = randomTroop.getString("Name");
                answers = new ArrayList<>();
                answers.add(correctTroop2);

                while (answers.size() < 4)
                {
                    JSONObject anotherRandomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));

                    String trpnme = anotherRandomTroop.getString("Name");
                    if (anotherRandomTroop.getJSONArray("Traits").join(" , ").contains(trait)) //If the current troop ALSO has the trait we're looking for
                    {
                        continue; //Get another
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
                questionText = temp.templateText.replace("%%TROOP%%", randomTroop.getString("Name"));
                String correctColors = "";
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

                answers = new ArrayList<>();
                answers.add(correctColors);

                while (answers.size() < 4)
                {
                    JSONObject anotherRandomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));

                    String correctColors2 = "";
                    JSONObject manaColors2 = randomTroop.getJSONObject("ManaColors");

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
                String correctTrait = randomTroop.getJSONArray("Traits").getString(r.nextInt(3)); //Get a random trait from the troop
                answers = new ArrayList<>();
                answers.add(correctTrait);

                while (answers.size() < 4)
                {
                    JSONObject anotherRandomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));

                    String trait2 = anotherRandomTroop.getJSONArray("Traits").getString(r.nextInt(3));
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

                questionText = temp.templateText.replace("%%MANACOLOR1%%", manaCols.get(0)).replace("%%MANACOLOR2", manaCols.get(1));
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

        Question result = null;

        //TODO
        switch (temp.searchFor)
        {
            case "truedamge":
                //Which of these spells does true damage?
                boolean tdFound = false;
                String correctSpell;
                while (!tdFound)
                {
                    JSONObject randomSpell = GameData.arraySpells.getJSONObject(r.nextInt(GameData.arraySpells.length()));
                    JSONArray spellSteps = randomSpell.getJSONObject("Spell").getJSONArray("SpellSteps");
                    for (Object o : spellSteps)
                    {
                        JSONObject step = (JSONObject) o;
                        if (step.getBoolean("TrueDamage"))
                        {
                            tdFound = true;
                            correctSpell = randomSpell.getString("Name");
                        }
                    }
                }
                break;

        }
        return result;
    }

    private Question handleTraitstoneTemplate(QuestionTemplate temp, Random r)
    {
        return null; //TODO
    }

    private Question handleKingdomTemplate(QuestionTemplate temp, Random r)
    {
        return null; //TODO
    }
}
