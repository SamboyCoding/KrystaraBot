/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.gemdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Traitable extends Nameable
{
    public static class TraitCost extends Traitstone.Summary
    {
        private int count = 0;
        
        public int getCount()
        {
            return count;
        }
    }
    
    public static class TraitSummary extends Trait.Summary
    {
        private ArrayList<TraitCost> costs = null;
        private String description = null;
        
        public List<TraitCost> getCosts()
        {
            return Collections.unmodifiableList(this.costs);
        }

        public String getDescription()
        {
            return description;
        }
    }
    
    public List<TraitSummary> getTraits();
    
    public String getImageUrl();
    public String getPageUrl();
}
