package me.samboycoding.krystarabot.quiz;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.Random;
import org.apache.commons.dbutils.QueryRunner;

/**
 *
 * @author Emily Ash
 */
public enum QuizQuestionType
{
    TroopToKingdom("Which kingdom is this troop from?", 
        QuizQuestion.Difficulty.Easy),

    KingdomToTroop("Which troop is in this kingdom?", 
        QuizQuestion.Difficulty.Easy),

    TroopToSpell("Which spell does this troop have?", 
        QuizQuestion.Difficulty.Moderate),

    SpellToTroop("Which troop has this spell?", 
        QuizQuestion.Difficulty.Moderate),

    TroopToType("Which type does this troop have?", 
        QuizQuestion.Difficulty.Moderate),

    TypeToTroop("Which troop has this type?", 
        QuizQuestion.Difficulty.Moderate),

    TroopToColor("Which color(s) does this troop use?", 
        QuizQuestion.Difficulty.Hard),

    ColorToTroop("Which troop uses this/these color(s)?", 
        QuizQuestion.Difficulty.Hard),

    TroopToRarity("Which rarity is this troop?", 
        QuizQuestion.Difficulty.Easy),

    RarityToTroop("Which troop is of this rarity?", 
        QuizQuestion.Difficulty.Easy),

    TroopToTrait("Which trait does this troop have?", 
        QuizQuestion.Difficulty.Hard),

    TraitToTroop("Which troop has this trait?", 
        QuizQuestion.Difficulty.Hard),

    FlavorTextToTroop("Which troop has this flavour text?", 
        QuizQuestion.Difficulty.Easy),

    SpellArtToTroop("Which troop has the pictured spell?", 
        QuizQuestion.Difficulty.Easy),

    TrueDamageTroop("Which of these troops does true damage?", 
        QuizQuestion.Difficulty.Easy),

    CreateGemsTroop("Which of these troops creates gems?", 
        QuizQuestion.Difficulty.Easy),

    ConvertGemsTroop("Which of these troops converts gems?", 
        QuizQuestion.Difficulty.Easy),

    DestroyGemsTroop("Which of these troops destroys gems?", 
        QuizQuestion.Difficulty.Easy),

    IncreaseStatsTroop("Which of these troops increases the stats of itself or others?", 
        QuizQuestion.Difficulty.Moderate),

    DecreaseStatsTroop("Which of these troops decreases the stats of itself or others?", 
        QuizQuestion.Difficulty.Moderate),

    GiveResourcesTroop("Which of these troops gives resources?", 
        QuizQuestion.Difficulty.Easy),

    GiveExtraTurnTroop("Which of these troops gives an extra turn?", 
        QuizQuestion.Difficulty.Easy),

    SummonTransformTroop("Which of these troops summons or transforms a troop?", 
        QuizQuestion.Difficulty.Easy),

    DrainManaTroop("Which of these troops drains mana?", 
        QuizQuestion.Difficulty.Easy),

    EffectsTroop("Which of these troops gives effects?", 
        QuizQuestion.Difficulty.Moderate),


    KingdomToTraitstone("Which traitstone is found in this kingdom?", 
        QuizQuestion.Difficulty.Hard),

    TraitstoneToKingdom("Which kingdom contains this traitstone?", 
        QuizQuestion.Difficulty.Hard),

    KingdomToStat("Which stat is got from this kingdom?", 
        QuizQuestion.Difficulty.Hard),

    StatToKingdom("Which kingdom gives this stat?", 
        QuizQuestion.Difficulty.Hard),

    BannerArtToKingdom("Which kingdom has the pictured banner?", 
        QuizQuestion.Difficulty.Easy),

    ShieldArtToKingdom("Which kingdom has the pictured shield?", 
        QuizQuestion.Difficulty.Moderate),

    CardArtToTroop("Which troop is pictured?", 
        QuizQuestion.Difficulty.Unused),

    ClassToBonusColor("Which bonus color does this class give?", 
        QuizQuestion.Difficulty.Unused),

    BonusColorToClass("Which class gives this bonus color?", 
        QuizQuestion.Difficulty.Unused),

    ClassToTrait("Which trait does this class have?", 
        QuizQuestion.Difficulty.Unused),

    TraitToClass("Which class has this trait?", 
        QuizQuestion.Difficulty.Unused),

    ClassArtToClass("Which class is pictured?", 
        QuizQuestion.Difficulty.Unused);


    public final String description;
    public final QuizQuestion.Difficulty difficulty;
    
    private QuizQuestionType(String desc, QuizQuestion.Difficulty d)
    {
        description = desc;
        difficulty = d;
    }

    public static final int Count = values().length;

    public static QuizQuestionType fromInteger(int x)
    {
        return values()[x];
    }

    public static QuizQuestionType fromString(String s)
    {
        for (QuizQuestionType t : values())
        {
            if (t.name().equalsIgnoreCase(s))
            {
                return t;
            }
        }
        throw new InvalidParameterException();
    }
}
