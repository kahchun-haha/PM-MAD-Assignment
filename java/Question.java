package com.example.horapp;

public class Question {

    public static String question[] ={
            "Does the perpetrator of violence have a history of using weapons or threatening to use them during conflicts?",
            "Has the frequency or severity of the violence escalated over time?",
            "Is the victim experiencing increased isolation from friends and family as a result of the perpetrator's actions?",
            "Does the perpetrator have a history of controlling behavior, such as monitoring the victim's activities or restricting their freedom?",
            "Has the perpetrator ever displayed signs of extreme jealousy or possessiveness?",
            "Is there a history of substance abuse by either the perpetrator or victim?",
            "Has the victim attempted to leave the relationship, and if so, how did the perpetrator respond?",
            "Are there children in the household who have witnessed the violence?",
            "Has the victim sought medical attention as a result of the violence?",
            "Has the perpetrator ever violated restraining or protective orders?"
    };

    public static String choices[][] = {
            {"Never","Rarely","Ocassionally","Frequently"},
            {"No escalation","Minor escalation","Moderate escalation","Severe escalation"},
            {"Not at all","Ocassionally","Often","Constantly"},
            {"No history","Rarely","Occasionally","Frequently"},
            {"Never","Rarely","Ocassionally","Frequently"},
            {"No history","Rarely","Occasionally","Frequently"},
            {"Not applicable","Peacefully","With verbal threats","With physical violence"},
            {"No children","Children present, but unaware","Children aware but not directly affected","Children directly affected"},
            {"Never","Rarely","Ocassionally","Frequently"},
            {"No history","Rarely","Occasionally","Frequently"}
    };

    public static String correctAnswers[] = {
            "Google",
            "Notepad",
            "Youtube",
            "Apple",
            "Google",
            "Notepad",
            "Youtube",
            "Apple",
            "Google",
            "Notepad",
    };

    public static int images[] = {
            R.drawable.image1,
            R.drawable.image1,
            R.drawable.image1,
            R.drawable.image1,
            R.drawable.image1,
            R.drawable.image1,
            R.drawable.image1,
            R.drawable.image1,
            R.drawable.image1,
            R.drawable.image1,
    };

}
