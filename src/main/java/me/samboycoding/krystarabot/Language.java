package me.samboycoding.krystarabot;

import java.security.InvalidParameterException;

public enum Language
{
    ENGLISH("en", "en-US"),
    GERMAN("de", "de-DE"),
    FRENCH("fr", "fr-FR"),
    ITALIAN("it", "it-IT"),
    SPANISH("es", "es-ES");
    
    private final String shortCode;
    private final String code;
        
    Language(String shortCode, String code)
    {
        this.shortCode = shortCode;
        this.code = code;
    }
    
    public String getShortCode()
    {
        return this.shortCode;
    }

    public String getCode()
    {
        return this.code;
    }
    
    public static Language fromShortCode(String shortCode)
    {
        for (Language lang : values())
        {
            if (lang.getShortCode().equals(shortCode))
            {
                return lang;
            }
        }
        
        throw new InvalidParameterException("Unrecognized language \"" + shortCode + "\".");
    }
}
