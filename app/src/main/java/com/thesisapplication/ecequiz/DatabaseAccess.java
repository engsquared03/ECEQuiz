package com.thesisapplication.ecequiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseAccess {
    //Database Info QuestionsTable
    private static final int QUESTION_ID = 0;
    private static final int SUBJECT = 1;
    private static final int SOURCE = 2;
    private static final int CATEGORY = 3;
    private static final int QUESTION = 4;
    private static final int OPTION_1 = 5;
    private static final int OPTION_2 = 6;
    private static final int OPTION_3 = 7;
    private static final int OPTION_4 = 8;
    private static final int ANSWER_NUMBER = 9;
    private static final int ANSWER_STRING = 10;


    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public ArrayList<QuestionsInfo> getSubjectList() {
        ArrayList<QuestionsInfo> listOfSubjects = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT DISTINCT subject FROM questionsTable ORDER BY subject ASC", null);
        if (c.moveToFirst()) {
            do {
                QuestionsInfo questionsInfo = new QuestionsInfo();
                questionsInfo.setSubject(c.getString(0));
                questionsInfo.setNumberOfQuestions(getQuestionCount(1, questionsInfo.getSubject(), null, null));
                questionsInfo.setNumberOfQuestionsUsed(getUsedQuestionsCount(1, questionsInfo.getSubject(), null, null));
                questionsInfo.setStats(getSubjectStat(questionsInfo.getSubject()));
                listOfSubjects.add(questionsInfo);
            } while (c.moveToNext());
        }
        return listOfSubjects;
    }

    public ArrayList<QuestionsInfo> getSourceList() {
        ArrayList<QuestionsInfo> listOfSources = new ArrayList<>();
        int quizID = getCurrentQuizID();
        Cursor cursorGetSubject = database.rawQuery("SELECT * FROM quizInfoTable WHERE quizID = ?", new String[]{String.valueOf(quizID)});
        cursorGetSubject.moveToFirst();
        String subject = cursorGetSubject.getString(1);
        Cursor c = database.rawQuery("SELECT DISTINCT source FROM questionsTable WHERE subject = ? ORDER BY source ASC", new String[]{subject});
        if (c.moveToFirst()) {
            do {
                QuestionsInfo questionsInfo = new QuestionsInfo();
                questionsInfo.setSource(c.getString(0));
                questionsInfo.setNumberOfQuestions(getQuestionCount(2, subject, questionsInfo.getSource(), null));
                questionsInfo.setNumberOfQuestionsUsed(getUsedQuestionsCount(2, subject, questionsInfo.getSource(), null));
                questionsInfo.setStats(getSourceStat(subject, questionsInfo.getSource()));
                listOfSources.add(questionsInfo);
            } while (c.moveToNext());
        }
        return listOfSources;
    }

    public ArrayList<QuestionsInfo> getCategoryList() {
        ArrayList<QuestionsInfo> listOfCategory = new ArrayList<>();
        int quizID = getCurrentQuizID();
        Cursor cursorGetSource = database.rawQuery("SELECT * FROM quizInfoTable WHERE quizID = ?", new String[]{String.valueOf(quizID)});
        cursorGetSource.moveToFirst();
        String subject = cursorGetSource.getString(1);
        String source = cursorGetSource.getString(2);
        Cursor c = database.rawQuery("SELECT DISTINCT category FROM questionsTable WHERE subject = ? AND source = ? ORDER BY category ASC", new String[]{subject, source});
        if (c.moveToFirst()) {
            do {
                QuestionsInfo questionsInfo = new QuestionsInfo();
                questionsInfo.setCategory(c.getString(0));
                questionsInfo.setNumberOfQuestions(getQuestionCount(3, subject, source, questionsInfo.getCategory()));
                questionsInfo.setNumberOfQuestionsUsed(getUsedQuestionsCount(3, subject, source, questionsInfo.getCategory()));
                questionsInfo.setStats(getCategoryStat(subject, source, questionsInfo.getCategory()));
                listOfCategory.add(questionsInfo);
            } while (c.moveToNext());
        }
        return listOfCategory;
    }

    /*
        Request Code
        SelectSubjectActivity = 1
        SelectSourceActivity = 2
        SelectCategoryActivity = 3
    */
    public int getQuestionCount(int requestCode, String subject, String source, String category) {
        int numberOfQuestions = 0;
        switch (requestCode) {
            case 1:
                numberOfQuestions = database.rawQuery("SELECT * FROM questionsTable WHERE subject = ?", new String[]{subject}).getCount();
                break;
            case 2:
                numberOfQuestions = database.rawQuery("SELECT * FROM questionsTable WHERE subject = ? AND source = ?", new String[]{subject, source}).getCount();
                break;
            case 3:
                numberOfQuestions = database.rawQuery("SELECT * FROM questionsTable WHERE subject = ? AND source = ? AND category = ?", new String[]{subject, source, category}).getCount();
                break;

        }
        return numberOfQuestions;
    }

    private int getUsedQuestionsCount(int requestCode, String subject, String source, String category) {
        int usedQuestionCount = 0;
        switch (requestCode) {
            case 1:
                usedQuestionCount = database.rawQuery("SELECT * FROM questionsTable WHERE subject = ? AND useOfQuestionCount != '0'", new String[]{subject}).getCount();
                break;
            case 2:
                usedQuestionCount = database.rawQuery("SELECT * FROM questionsTable WHERE subject = ? AND source = ? AND useOfQuestionCount != '0'", new String[]{subject, source}).getCount();
                break;
            case 3:
                usedQuestionCount = database.rawQuery("SELECT * FROM questionsTable WHERE subject = ? AND source = ? AND category = ? AND useOfQuestionCount != '0'", new String[]{subject, source, category}).getCount();
                break;

        }
        return usedQuestionCount;
    }

    public int getQuizID() {
        int quizID;
        Cursor c = database.rawQuery("SELECT * FROM quizInfoTable ORDER BY quizID DESC", null);
        if (c.moveToFirst()) {
            quizID = c.getInt(0) + 1;
        } else {
            quizID = 1;
        }
        return quizID;
    }

    public void deleteUnusedQuizInfo() {
        Cursor c = database.rawQuery("SELECT * FROM quizInfoTable ORDER BY quizID DESC", null);
        if (c.moveToFirst()) {
            int quizID = c.getInt(0);
            if ((c.getString(2) == null) || (c.getString(3) == null)) {
                database.execSQL("DELETE FROM quizInfoTable WHERE quizID = ?", new String[]{String.valueOf(quizID)});
            }
            Cursor d = database.rawQuery("SELECT * FROM historyTable WHERE quizID = ?", new String[]{String.valueOf(quizID)});
            if (!d.moveToFirst()) {
                database.execSQL("DELETE FROM quizInfoTable WHERE quizID = ?", new String[]{String.valueOf(quizID)});
            }
        }

    }

    public void setSubject(int quizID, String subject) {
        ContentValues cv = new ContentValues();
        if (database.rawQuery("SELECT * FROM quizInfoTable WHERE quizID = ?", new String[]{String.valueOf(quizID)}).moveToFirst()) {
            cv.put("subject", subject);
            database.update("quizInfoTable", cv, "quizID = ?", new String[]{String.valueOf(quizID)});
        } else {
            cv.put("quizID", quizID);
            cv.put("subject", subject);
            database.insert("quizInfoTable", null, cv);
        }
    }

    public int getCurrentQuizID() {
        int quizID;
        Cursor c = database.rawQuery("SELECT * FROM quizInfoTable ORDER BY quizID DESC", null);
        c.moveToFirst();
        quizID = c.getInt(0);
        return quizID;
    }

    public void setSource(int quizID, String source) {
        ContentValues cv = new ContentValues();
        cv.put("source", source);
        database.update("quizInfoTable", cv, "quizID = ?", new String[]{String.valueOf(quizID)});
    }

    public void finalQuizSetup(int quizID, String category, int numberOfQuestions, int random, String date) {
        ContentValues cv = new ContentValues();
        cv.put("category", category);
        cv.put("numberOfQuestions", numberOfQuestions);
        cv.put("random", random);
        cv.put("score", 0);
        cv.put("date", date);
        database.update("quizInfoTable", cv, "quizID = ?", new String[]{String.valueOf(quizID)});
    }

    public void playAgainSetup() {
        QuizInfo quizInfo = getQuizInfo();
        ContentValues cv = new ContentValues();
        cv.put("quizID", (quizInfo.getQuizID() + 1));
        cv.put("subject", quizInfo.getSubject());
        cv.put("source", quizInfo.getSource());
        cv.put("category", quizInfo.getCategory());
        cv.put("numberOfQuestions", quizInfo.getNumberOfQuestions());
        cv.put("random", quizInfo.getRandom());
        cv.put("score", 0);
        cv.put("date", getDate());
        database.insert("quizInfoTable", null, cv);
    }

    private String getDate() {
        String date = new SimpleDateFormat("EEE, d MMM yyyy  HH:mm aaa", Locale.getDefault()).format(new Date());
        return date;
    }


    public QuizInfo getQuizInfo() {
        QuizInfo quizInfo = new QuizInfo();
        int currentQuizID = getCurrentQuizID();
        Cursor c = database.rawQuery("SELECT * FROM quizInfoTable WHERE quizID = ?", new String[]{String.valueOf(currentQuizID)});
        c.moveToFirst();
        quizInfo.setQuizID(c.getInt(0));
        quizInfo.setSubject(c.getString(1));
        quizInfo.setSource(c.getString(2));
        quizInfo.setCategory(c.getString(3));
        quizInfo.setNumberOfQuestions(c.getInt(4));
        quizInfo.setRandom(c.getInt(5));
        quizInfo.setScore(c.getInt(6));
        return quizInfo;
    }

    public ArrayList<Question> getQuestion(String subject, String source, String category, int numberOfQuestions) {
        ArrayList<Question> questionList = new ArrayList<>();
        List<Integer> idList = getIDList(subject, source, category, numberOfQuestions);
        for (int n = 0; n < numberOfQuestions; n++) {
            int id = idList.get(n);
            Question question = getQuestionFixedChoices(id);
            questionList.add(question);
        }
        return questionList;
    }

    public ArrayList<Question> getQuestionRandom(String subject, String source, String category, int numberOfQuestions) {
        ArrayList<Question> questionList = new ArrayList<>();
        List<Integer> idList = getIDList(subject, source, category, numberOfQuestions);
        for (int n = 0; n < numberOfQuestions; n++) {
            int id = idList.get(n);
            Cursor c = database.rawQuery("SELECT * FROM questionsTable WHERE _id = ?", new String[]{String.valueOf(idList.get(n))});
            c.moveToFirst();
            int random = c.getInt(11);
            if (random == 1) {
                Question question = getQuestionRandomChoices(id);
                questionList.add(question);
            }
            if (random == 0) {
                Question question = getQuestionFixedChoices(id);
                questionList.add(question);
            }

        }
        return questionList;
    }

    public Question getQuestionFixedChoices(int id) {
        Cursor c = database.rawQuery("SELECT * FROM questionsTable WHERE _id = ?", new String[]{String.valueOf(id)});
        c.moveToFirst();
        Question question = new Question();
        question.setId(c.getInt(0));
        question.setSubject(c.getString(1));
        question.setSource(c.getString(2));
        question.setCategory(c.getString(3));
        question.setQuestion(c.getString(4));
        question.setOption1(c.getString(5));
        question.setOption2(c.getString(6));
        question.setOption3(c.getString(7));
        question.setOption4(c.getString(8));
        question.setAnswer(c.getString(9));
        question.setAnswerNumber(c.getInt(10));
        question.setUserAnswer(0);
        question.setUserAnswerString("Not Answered");
        question.setAnswered(false);
        question.setCorrectAnswer(0);
        question.setCorrect(c.getInt(14));
        question.setNote(c.getString(15));
        return question;
    }

    private Question getQuestionRandomChoices(int id) {
        Cursor c = database.rawQuery("SELECT * FROM questionsTable WHERE _id = ?", new String[]{String.valueOf(id)});
        c.moveToFirst();
        Question question = new Question();
        question.setId(c.getInt(0));
        question.setSubject(c.getString(1));
        question.setSource(c.getString(2));
        question.setCategory(c.getString(3));
        question.setQuestion(c.getString(4));
        question.setAnswer(c.getString(9));
        question.setUserAnswer(0);
        question.setAnswered(false);
        question.setCorrectAnswer(0);
        int answerNumber = c.getInt(10);
        question.setCorrect(c.getInt(14));
        question.setNote(c.getString(15));
        List<Integer> optionList = getOptionList();
        for (int i = 0; i < optionList.size(); i++) {
            int number = optionList.get(i);
            if (i == 0) {
                if (answerNumber == number) {
                    question.setAnswerNumber(1);
                }
                switch (number) {
                    case 1:
                        question.setOption1(c.getString(5));
                        break;
                    case 2:
                        question.setOption1(c.getString(6));
                        break;
                    case 3:
                        question.setOption1(c.getString(7));
                        break;
                    case 4:
                        question.setOption1(c.getString(8));
                        break;
                }
            }
            if (i == 1) {
                if (answerNumber == number) {
                    question.setAnswerNumber(2);
                }
                switch (number) {
                    case 1:
                        question.setOption2(c.getString(5));
                        break;
                    case 2:
                        question.setOption2(c.getString(6));
                        break;
                    case 3:
                        question.setOption2(c.getString(7));
                        break;
                    case 4:
                        question.setOption2(c.getString(8));
                        break;
                }
            }
            if (i == 2) {
                if (answerNumber == number) {
                    question.setAnswerNumber(3);
                }
                switch (number) {
                    case 1:
                        question.setOption3(c.getString(5));
                        break;
                    case 2:
                        question.setOption3(c.getString(6));
                        break;
                    case 3:
                        question.setOption3(c.getString(7));
                        break;
                    case 4:
                        question.setOption3(c.getString(8));
                        break;
                }
            }
            if (i == 3) {
                if (answerNumber == number) {
                    question.setAnswerNumber(4);
                }
                switch (number) {
                    case 1:
                        question.setOption4(c.getString(5));
                        break;
                    case 2:
                        question.setOption4(c.getString(6));
                        break;
                    case 3:
                        question.setOption4(c.getString(7));
                        break;
                    case 4:
                        question.setOption4(c.getString(8));
                        break;
                }
            }
        }
        return question;
    }

    private List<Integer> getOptionList() {
        List<Integer> list = new ArrayList<>();
        list.add(new Integer(1));
        list.add(new Integer(2));
        list.add(new Integer(3));
        list.add(new Integer(4));
        Collections.shuffle(list);
        return list;
    }

    public List<Integer> getIDList(String subject, String source, String category, int numberOfQ) {
        List<Integer> list = new ArrayList<>();
        int useOfQuestionCount = 0;

        while (list.size() < numberOfQ) {
            Cursor c = database.rawQuery("SELECT * FROM questionsTable WHERE subject = ? AND source = ? AND category = ? AND useOfQuestionCount = ? ORDER BY RANDOM()",
                    new String[]{subject, source, category, String.valueOf(useOfQuestionCount)});
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(0);
                    list.add(new Integer(id));
                } while (list.size() != numberOfQ && c.moveToNext());
            }
            useOfQuestionCount++;// the questions id selected by the application is bias towards to lowest use of question count.
        }//if the number of questions needed by the user is not satisfied the code inside the loop will continue to execute

        return list;
    }// get List of ID in order to start gathering all the requested questions

    public void putQuestionListInHistory(ArrayList<Question> list, int quizID) {
        ArrayList<Question> questionList = list;
        Question currentQuestion;
        for (int i = 0; i < questionList.size(); i++) {
            currentQuestion = questionList.get(i);
            ContentValues cv = new ContentValues();
            cv.put("quizID", quizID);
            cv.put("_id", currentQuestion.getId());
            cv.put("subject", currentQuestion.getSubject());
            cv.put("source", currentQuestion.getSource());
            cv.put("category", currentQuestion.getCategory());
            cv.put("question", currentQuestion.getQuestion());
            cv.put("option1", currentQuestion.getOption1());
            cv.put("option2", currentQuestion.getOption2());
            cv.put("option3", currentQuestion.getOption3());
            cv.put("option4", currentQuestion.getOption4());
            cv.put("answer", currentQuestion.getAnswerNumber());
            cv.put("answerString", currentQuestion.getAnswer());
            cv.put("userAnswer", currentQuestion.getUserAnswer());
            cv.put("userAnswerString", "Not Answered");
            cv.put("correctAnswer", currentQuestion.getCorrectAnswer());
            database.insert("historyTable", null, cv);
        }
    }

    public void updateUseOfQuestionCount(int id) {
        int count;
        Cursor c = database.rawQuery("SELECT * FROM questionsTable WHERE _id = ?", new String[]{String.valueOf(id)});
        c.moveToFirst();
        count = c.getInt(12);
        count++;
        database.execSQL("UPDATE questionsTable SET useOfQuestionCount = ? WHERE _id = ?", new String[]{String.valueOf(count), String.valueOf(id)});
    }

    public void updateHistoryTable(int quizID, int _id, int userAnswer, int correctAnswer, String userAnswerString) {
        ContentValues cv = new ContentValues();
        cv.put("userAnswer", userAnswer);
        cv.put("userAnswerString", userAnswerString);
        cv.put("correctAnswer", correctAnswer);
        database.update("historyTable", cv, "quizID = ? AND _id = ?", new String[]{String.valueOf(quizID), String.valueOf(_id)});
        if (correctAnswer == 1) {
            Cursor c = database.rawQuery("SELECT * FROM quizInfoTable WHERE quizID = ?", new String[]{String.valueOf(quizID)});
            c.moveToFirst();
            int score = c.getInt(6);
            score++;
            database.execSQL("UPDATE quizInfoTable SET score = ? WHERE quizID = ?", new String[]{String.valueOf(score), String.valueOf(quizID)});
        }
    }

    public ArrayList<Question> getHistoryQuestions(int quizID) {
        ArrayList<Question> questionList = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT * FROM historyTable WHERE quizID = ?", new String[]{String.valueOf(quizID)});
        c.moveToFirst();
        do {
            Question question = new Question();
            question.setId(c.getInt(1));
            question.setSubject(c.getString(2));
            question.setSource(c.getString(3));
            question.setCategory(c.getString(4));
            question.setQuestion(c.getString(5));
            question.setOption1(c.getString(6));
            question.setOption2(c.getString(7));
            question.setOption3(c.getString(8));
            question.setOption4(c.getString(9));
            question.setAnswerNumber(c.getInt(10));
            question.setAnswer(c.getString(11));
            question.setUserAnswer(c.getInt(12));
            question.setUserAnswerString(c.getString(13));
            question.setCorrectAnswer(c.getInt(14));
            questionList.add(question);
        } while (c.moveToNext());
        return questionList;
    }

    public ArrayList<QuizInfo> getHistoryList() {
        ArrayList<QuizInfo> quizInfoList = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT DISTINCT quizID FROM historyTable", null);
        if (c.moveToFirst()) {
            do {
                QuizInfo quizInfo = new QuizInfo();
                quizInfo.setQuizID(c.getInt(0));
                Cursor d = database.rawQuery("SELECT * FROM historyTable WHERE quizID = ?", new String[]{String.valueOf(quizInfo.getQuizID())});
                d.moveToFirst();
                quizInfo.setSubject(d.getString(2));
                quizInfo.setSource(d.getString(3));
                quizInfo.setCategory(d.getString(4));
                quizInfo.setFirstQuestion(d.getString(5));
                Cursor e = database.rawQuery("SELECT * FROM quizInfoTable WHERE quizID = ?", new String[]{String.valueOf(quizInfo.getQuizID())});
                e.moveToFirst();
                quizInfo.setNumberOfQuestions(e.getInt(4));
                quizInfo.setRandom(e.getInt(5));
                quizInfo.setScore(e.getInt(6));
                quizInfo.setDate(e.getString(7));
                quizInfoList.add(quizInfo);
            } while (c.moveToNext());
        }

        return quizInfoList;
    }

    public void deleteQuizHistory(int quizID) {
        database.execSQL("DELETE FROM quizInfoTable WHERE quizID = ?", new String[]{String.valueOf(quizID)});
        database.execSQL("DELETE FROM historyTable WHERE quizID = ?", new String[]{String.valueOf(quizID)});
    }

    public void deleteCustomHistory(int customQuizID) {
        database.execSQL("DELETE FROM customQuizHistoryTable WHERE customQuizID = ?", new String[]{String.valueOf(customQuizID)});
    }

    public void updateNotes(int _id, String notes) {
        ContentValues cv = new ContentValues();
        cv.put("notes", notes);
        database.update("questionsTable", cv, "_id = ?", new String[]{String.valueOf(_id)});
    }

    public void updateRecordCorrect(int id, int correct) {
        ContentValues cv = new ContentValues();
        cv.put("correct", correct);
        database.update("questionsTable", cv, "_id = ?", new String[]{String.valueOf(id)});
    }

    public int getSubjectStat(String subject) {
        int numberOfQuestionsUsed = getUsedQuestionsCount(1, subject, null, null);
        if (numberOfQuestionsUsed == 0) {
            return 0;
        } else {
            int numberOfQuestionsCorrect = database.rawQuery("SELECT * FROM questionsTable WHERE subject = ? AND useOfQuestionCount != '0' AND correct = '1'", new String[]{subject}).getCount();
            return (100 * numberOfQuestionsCorrect) / (numberOfQuestionsUsed);
        }
    }

    public int getSourceStat(String subject, String source) {
        int numberOfQuestionsUsed = getUsedQuestionsCount(2, subject, source, null);
        if (numberOfQuestionsUsed == 0) {
            return 0;
        } else {
            int numberOfQuestionsCorrect = database.rawQuery("SELECT * FROM questionsTable WHERE subject = ? AND source = ? AND useOfQuestionCount != '0' AND correct = '1'", new String[]{subject, source}).getCount();
            return (100 * numberOfQuestionsCorrect) / (numberOfQuestionsUsed);
        }
    }

    public int getCategoryStat(String subject, String source, String category) {
        int numberOfQuestionsUsed = getUsedQuestionsCount(3, subject, source, category);
        if (numberOfQuestionsUsed == 0) {
            return 0;
        } else {
            int numberOfQuestionsCorrect = database.rawQuery("SELECT * FROM questionsTable WHERE subject = ? AND source = ? AND category = ? AND useOfQuestionCount != '0' AND correct = '1'", new String[]{subject, source, category}).getCount();
            return (100 * numberOfQuestionsCorrect) / (numberOfQuestionsUsed);
        }
    }

    public int getQuestionsStats(int requestCode, String subject, String source, String category) {
        int questionCount = getQuestionCount(requestCode, subject, source, category);
        int usedQuestionCount = getUsedQuestionsCount(requestCode, subject, source, category);
        return (100 * usedQuestionCount) / questionCount;
    }

    public List<String> getListOfSubject() {
        List<String> list = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT DISTINCT subject FROM questionsTable ORDER BY subject ASC", null);
        if (c.moveToFirst()) {
            do {
                list.add(c.getString(0));
            } while (c.moveToNext());
        }
        return list;
    }

    public List<String> getListOfSource(String subject) {
        List<String> list = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT DISTINCT source FROM questionsTable WHERE subject = ? ORDER BY source ASC", new String[]{subject});
        if (c.moveToFirst()) {
            do {
                list.add(c.getString(0));
            } while (c.moveToNext());
        }
        return list;
    }

    public List<String> getListOfCategory(String subject, String source) {
        List<String> list = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT DISTINCT category FROM questionsTable WHERE subject = ? AND source = ? ORDER BY category ASC", new String[]{subject, source});
        if (c.moveToFirst()) {
            do {
                list.add(c.getString(0));
            } while (c.moveToNext());
        }
        return list;
    }

    private int getCustomQuizID() {
        int customQuizID;
        Cursor c = database.rawQuery("SELECT * FROM customQuizInfoTable ORDER BY customQuizID DESC", null);
        if (c.moveToFirst()) {
            customQuizID = c.getInt(0) + 1;
        } else {
            customQuizID = 1;
        }
        return customQuizID;
    }

    public void createCustomQuiz(ArrayList<QuizInfo> quizInfoList, String quizTitle, int totalNOQ) {
        ArrayList<QuizInfo> list = quizInfoList;
        QuizInfo quizInfo;
        int quizID = getCustomQuizID();

        for (int i = 0; i < list.size(); i++) {
            quizInfo = list.get(i);
            ContentValues cv = new ContentValues();
            cv.put("customQuizID", quizID);
            cv.put("quizTitle", quizTitle);
            cv.put("subject", quizInfo.getSubject());
            cv.put("source", quizInfo.getSource());
            cv.put("category", quizInfo.getCategory());
            cv.put("numberOfQuestions", quizInfo.getNumberOfQuestions());
            cv.put("totalNOQ", totalNOQ);
            database.insert("customQuizInfoTable", null, cv);
        }
    }

    public ArrayList<QuizInfo> getCustomQuizList() {
        ArrayList<QuizInfo> quizInfoList = new ArrayList<>();
        List<Integer> idList = new ArrayList<>();
        Cursor d = database.rawQuery("SELECT DISTINCT customQuizID FROM customQuizInfoTable", null);
        if (d.moveToFirst()) {
            do {
                idList.add(new Integer(d.getInt(0)));
            } while (d.moveToNext());
        }

        for (int i = 0; i < idList.size(); i++) {
            Cursor c = database.rawQuery("SELECT * FROM customQuizInfoTable WHERE customQuizID = ?", new String[]{String.valueOf(idList.get(i))});
            c.moveToFirst();
            QuizInfo quizInfo = new QuizInfo();
            quizInfo.setQuizID(c.getInt(0));
            quizInfo.setQuizTitle(c.getString(1));
            quizInfo.setSubject(c.getString(2));
            quizInfo.setSource(c.getString(3));
            quizInfo.setCategory(c.getString(4));
            quizInfo.setTotalNOQ(c.getInt(6));
            quizInfoList.add(quizInfo);
        }
        return quizInfoList;
    }

    public ArrayList<Question> getCustomQuizQuestions(int customQuizID) {
        ArrayList<Question> questionList = new ArrayList<>();
        ArrayList<QuizInfo> quizInfoList = getCustomQuizInfo(customQuizID);
        List<Integer> idList;

        for (int i = 0; i < quizInfoList.size(); i++) {
            QuizInfo quizInfo = quizInfoList.get(i);
            ArrayList<Question> questions = getQuestion(quizInfo.getSubject(), quizInfo.getSource(), quizInfo.getCategory(), quizInfo.getNumberOfQuestions());
            questionList.addAll(questions);
        }

        Collections.shuffle(questionList);
        return questionList;
    }

    public ArrayList<QuizInfo> getCustomQuizInfo(int customQuizID) {
        ArrayList<QuizInfo> quizInfoList = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM customQuizInfoTable WHERE customQuizID = ? ORDER BY source ASC", new String[]{String.valueOf(customQuizID)});
        if (c.moveToFirst()) {
            do {
                QuizInfo quizInfo = new QuizInfo();
                quizInfo.setQuizID(c.getInt(0));
                quizInfo.setQuizTitle(c.getString(1));
                quizInfo.setSubject(c.getString(2));
                quizInfo.setSource(c.getString(3));
                quizInfo.setCategory(c.getString(4));
                quizInfo.setNumberOfQuestions(c.getInt(5));
                quizInfo.setTotalNOQ(c.getInt(6));
                quizInfoList.add(quizInfo);
            } while (c.moveToNext());
        }

        return quizInfoList;
    }

    public void deleteCustomQuiz(int quizID) {
        database.execSQL("DELETE FROM customQuizInfoTable WHERE customQuizID = ?", new String[]{String.valueOf(quizID)});
    }

    public boolean putQuestionInCustomHistory(Question question) {
        Question q = question;
        ContentValues cv = new ContentValues();
        cv.put("_id", q.getId());
        database.insert("customQuizHistoryTable", null, cv);
        boolean result = database.rawQuery("SELECT * FROM customQuizHistoryTable", null).moveToFirst();
        return result;
    }

    public void putCustomQuizInHistory(ArrayList<Question> list, int quizID, String quizTitle) {
        ArrayList<Question> questionList = list;
        Question currentQuestion;
        for (int i = 0; i < questionList.size(); i++) {
            currentQuestion = questionList.get(i);
            ContentValues cv = new ContentValues();
            cv.put("customQuizID", quizID);
            cv.put("_id", currentQuestion.getId());
            cv.put("quizTitle", quizTitle);
            cv.put("subject", currentQuestion.getSubject());
            cv.put("source", currentQuestion.getSource());
            cv.put("category", currentQuestion.getCategory());
            cv.put("question", currentQuestion.getQuestion());
            cv.put("option1", currentQuestion.getOption1());
            cv.put("option2", currentQuestion.getOption2());
            cv.put("option3", currentQuestion.getOption3());
            cv.put("option4", currentQuestion.getOption4());
            cv.put("answer", currentQuestion.getAnswerNumber());
            cv.put("answerString", currentQuestion.getAnswer());
            cv.put("userAnswer", currentQuestion.getUserAnswer());
            cv.put("userAnswerString", currentQuestion.getUserAnswerString());
            cv.put("correctAnswer", currentQuestion.getCorrectAnswer());
            cv.put("date", getDate());
            database.insert("customQuizHistoryTable", null, cv);
        }
    }

    public ArrayList<QuizInfo> getHistoryListCustom() {
        ArrayList<QuizInfo> quizInfoList = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT DISTINCT customQuizID FROM customQuizHistoryTable", null);
        if (c.moveToFirst()) {
            do {
                QuizInfo quizInfo = new QuizInfo();
                quizInfo.setQuizID(c.getInt(0));
                Cursor d = database.rawQuery("SELECT * FROM customQuizHistoryTable WHERE customQuizID = ?", new String[]{String.valueOf(quizInfo.getQuizID())});
                d.moveToFirst();
                quizInfo.setQuizTitle(d.getString(2));
                quizInfo.setSubject(d.getString(3));
                quizInfo.setFirstQuestion(d.getString(6));
                quizInfo.setDate(d.getString(16));
                quizInfo.setNumberOfQuestions(d.getCount());
                Cursor e = database.rawQuery("SELECT * FROM customQuizHistoryTable WHERE customQuizID = ? AND correctAnswer = '1'", new String[]{String.valueOf(quizInfo.getQuizID())});
                quizInfo.setScore(e.getCount());
                quizInfoList.add(quizInfo);
            } while (c.moveToNext());
        }
        return quizInfoList;
    }

    public ArrayList<Question> getHistoryCustomQuestions(int id) {
        ArrayList<Question> questionList = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT * FROM customQuizHistoryTable WHERE customQuizID = ?", new String[]{String.valueOf(id)});
        c.moveToFirst();
        do {
            Question question = new Question();
            question.setId(c.getInt(1));
            question.setSubject(c.getString(3));
            question.setSource(c.getString(4));
            question.setCategory(c.getString(5));
            question.setQuestion(c.getString(6));
            question.setOption1(c.getString(7));
            question.setOption2(c.getString(8));
            question.setOption3(c.getString(9));
            question.setOption4(c.getString(10));
            question.setAnswerNumber(c.getInt(11));
            question.setAnswer(c.getString(12));
            question.setUserAnswer(c.getInt(13));
            question.setUserAnswerString(c.getString(14));
            question.setCorrectAnswer(c.getInt(15));
            questionList.add(question);
        } while (c.moveToNext());
        return questionList;
    }

    public void setDatabaseAnswer() {
        Cursor c = database.rawQuery("SELECT * FROM questionsTable", null);
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                ContentValues cv = new ContentValues();
                cv.put("answer", getAnswer(id));
                database.update("questionsTable", cv, "_id = ?", new String[]{String.valueOf(id)});
            } while (c.moveToNext());
        }
    }

    private String getAnswer(int id) {
        Cursor c = database.rawQuery("SELECT * FROM questionsTable WHERE _id = ?", new String[]{String.valueOf(id)});
        c.moveToFirst();
        String option1 = c.getString(5);
        String option2 = c.getString(6);
        String option3 = c.getString(7);
        String option4 = c.getString(8);
        int answerNumber = c.getInt(10);

//        String answer = null;
//        return answer;
        switch (answerNumber) {
            case 1:
                return option1;
            case 2:
                return option2;
            case 3:
                return option3;
            case 4:
                return option4;
            default:
                return null;
        }
    }

    public String getNote(int questionID){
        Cursor c = database.rawQuery("SELECT * FROM questionsTable WHERE _id = ?", new String[]{String.valueOf(questionID)});
        c.moveToFirst();
        String note = c.getString(15);
        if(note == null){
            note = "";
        }
        return note;
    }

    public void applyEditQuestions(int id, Question question, Question uneditedQuestion){
        ContentValues cv = new ContentValues();
        cv.put("questions", question.getQuestion());
        cv.put("option1", question.getOption1());
        cv.put("option2", question.getOption2());
        cv.put("option3", question.getOption3());
        cv.put("option4", question.getOption4());
        database.update("questionsTable", cv, "_id = ?", new String[]{String.valueOf(id)});
    }

}
