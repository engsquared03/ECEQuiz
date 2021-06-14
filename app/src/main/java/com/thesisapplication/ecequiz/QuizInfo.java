package com.thesisapplication.ecequiz;

public class QuizInfo {
     private int quizID, numberOfQuestions, random, score, totalNOQ;
     private String subject, source, category, firstQuestion, date, quizTitle;

    public QuizInfo() {}

    public QuizInfo(int numberOfQuestions, String subject, String source, String category) {
        this.numberOfQuestions = numberOfQuestions;
        this.subject = subject;
        this.source = source;
        this.category = category;
    }

    public int getQuizID() {
        return quizID;
    }

    public void setQuizID(int quizID) {
        this.quizID = quizID;
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

    public int getRandom() {
        return random;
    }

    public void setRandom(int random) {
        this.random = random;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFirstQuestion() {
        return firstQuestion;
    }

    public void setFirstQuestion(String firstQuestion) {
        this.firstQuestion = firstQuestion;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public int getTotalNOQ() {
        return totalNOQ;
    }

    public void setTotalNOQ(int totalNOQ) {
        this.totalNOQ = totalNOQ;
    }
}
