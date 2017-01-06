/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.quiz;

import java.util.Random;

/**
 *
 * @author julians
 */
public interface QuizQuestionFactory
{
    public QuizQuestion getQuestion(Random r, QuizQuestionType type) throws Exception;
    public QuizQuestion getQuestion(Random r, QuizQuestion.Difficulty difficulty) throws Exception;
    public QuizQuestion getQuestion(Random r) throws Exception;
}
