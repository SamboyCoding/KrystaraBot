package me.samboycoding.krystarabot.quiz;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import me.samboycoding.krystarabot.GameData;
import me.samboycoding.krystarabot.utilities.IDReference;
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
        easyTemplates.add(new QuestionTemplate("What kingdom is %%TROOP%% found in?", "kingdom", "troop"));
        easyTemplates.add(new QuestionTemplate("What is %%TROOP%%'s base rarity?", "rarity", "troop"));
        easyTemplates.add(new QuestionTemplate("Which of these spells does true damage?", "truedamage", "spell"));
        easyTemplates.add(new QuestionTemplate("Which of these spells creates gems?", "creategems", "spell"));
        easyTemplates.add(new QuestionTemplate("Which of these spells can be used to generate mana?", "generatemana", "spell")); //If any spellstep has "ConvertGems", "CreateGems", "GenerateMana", "ExplodeGem" or "DestroyGems". NOT "RemoveColor" - it doesn't give you mana
        easyTemplates.add(new QuestionTemplate("Which of these spells destroys gems?", "destroygems", "spell")); //If any spellstep has "DestroyGems" or "ExplodeGem"

        normTemplates.add(new QuestionTemplate("What is %%TROOP%'s spell's name?", "spellname", "troop"));
        normTemplates.add(new QuestionTemplate("What is/are %%TROOP%%'s types?", "type", "troop"));
        normTemplates.add(new QuestionTemplate("Which troop has spell \"%%SPELL%%\"?", "spell", "troop"));
        normTemplates.add(new QuestionTemplate("Which troop has trait \"%%TRAIT%%\"", "trait", "troop")); //"trait" singular
        normTemplates.add(new QuestionTemplate("Which spell causes debuffs?", "debuff", "spell"));
        normTemplates.add(new QuestionTemplate("Which spell converts gems?", "convertgems", "spell"));
        normTemplates.add(new QuestionTemplate("Which spell deletes gems (without giving you mana)?", "removecolor", "spell"));
        normTemplates.add(new QuestionTemplate("Which spell gives stats?", "increasestat", "spell")); //"IncreaseArmor", "IncreaseAttack", "IncreaseHealth" or "IncreaseSpellPower"

        hardTemplates.add(new QuestionTemplate("What mana colors does %%TROOP%% use?", "manacolor", "troop")); //"manacolor" singular
        hardTemplates.add(new QuestionTemplate("Which of these is one of %%TROOP%%'s traits?", "traits", "troop")); //Note "traits" plural
        hardTemplates.add(new QuestionTemplate("What is the name of %%TROOP%%'s third trait?", "thirdtrait", "leg/mythic_troop"));
        hardTemplates.add(new QuestionTemplate("Which of these troops uses %%MANACOLOR1%% / %%MANACOLOR2%%?", "manacolors", "troop")); //Again, "manacolors" plural

        //Arcane traitstone
        hardTemplates.add(new QuestionTemplate("What is the name of the %%COL1%% / %%COL2%% traitstone?", "color", "traitstone")); //Singular
        hardTemplates.add(new QuestionTemplate("What color are arcane %%NAME%% traitstones?", "colors", "traitstone")); //Plural
        hardTemplates.add(new QuestionTemplate("Where are Arcane %%NAME%% traitstones be found?", "location", "traitstone"));

        //Kingdom
        easyTemplates.add(new QuestionTemplate("Which kingdom have bonuses \"Lord, Duke and King of %%TYPE%%\"?", "bonuses", "kingdom"));
        easyTemplates.add(new QuestionTemplate("Which of these troops is from %%NAME%%?", "troops", "kingdom"));
        easyTemplates.add(new QuestionTemplate("Which kingdom has the banner %%BANNERNAME%%?", "banner", "kingdom"));
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
                return handleTemplate(normTemplates.get(r.nextInt(easyTemplates.size())), r);
            case 3:
                return handleTemplate(hardTemplates.get(r.nextInt(easyTemplates.size())), r);
        }
        return null; //Invalid difficulty.
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
            case "traitstone":
                return handleTraitstoneTemplate(templ, r);
            case "kingdom":
                return handleKingdomTemplate(templ, r);
        }

        //Leg/mythic troop
        //Only question is "Which of these troops uses %%MANACOLOR1%% / %%MANACOLOR2%%?"
        
        return null; //TODO
    }

    private Question handleTroopTemplate(QuestionTemplate temp, Random r)
    {
        String search = temp.searchFor;
        //Get a random troop JSONObject
        JSONObject randomTroop = GameData.arrayTroops.getJSONObject(r.nextInt(GameData.arrayTroops.length()));

        Question result = null;
        
        //"kingdom", "rarity", "spellname", "type", "spell", "trait", "manacolor", "traits", "manacolors"
        switch (search)
        {
            case "kingdom":
                //Which kingdom is x found in?
                String questionText = temp.templateText.replace("%%TROOP%%", randomTroop.getString("Name"));
                String correctKingdom = randomTroop.getString("Kingdom");
                
                ArrayList<String> answers = new ArrayList<>();
                answers.add(correctKingdom);
                while(answers.size() < 4)
                {
                    JSONObject randomKingdom = GameData.arrayKingdoms.getJSONObject(r.nextInt(GameData.arrayKingdoms.length()));
                    String name = randomKingdom.getString("Name");
                    if(!answers.contains(name))
                    {
                        answers.add(name);
                    }
                }
                
                result = new Question(questionText, answers, 0);
                break;
            case "rarity":
                //Get its rarity and 
                break;
        }
        
        return result;
    }

    private Question handleSpellTemplate(QuestionTemplate temp, Random r)
    {
        return null; //TODO
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
