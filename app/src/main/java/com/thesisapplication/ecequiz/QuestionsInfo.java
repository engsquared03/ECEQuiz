package com.thesisapplication.ecequiz;

public class QuestionsInfo {
    private String subject, source, category;
    private int numberOfQuestions, numberOfQuestionsUsed, stats;

    public QuestionsInfo() {}

    public QuestionsInfo(String subject, int numberOfQuestions, int numberOfQuestionsUsed){
        this.subject = subject;
        this.numberOfQuestions = numberOfQuestions;
        this.numberOfQuestionsUsed = numberOfQuestionsUsed;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getNumberOfQuestionsUsed() {
        return numberOfQuestionsUsed;
    }

    public void setNumberOfQuestionsUsed(int numberOfQuestionsUsed) {
        this.numberOfQuestionsUsed = numberOfQuestionsUsed;
    }

    public int getStats() {
        return stats;
    }

    public void setStats(int stats) {
        this.stats = stats;
    }
}
