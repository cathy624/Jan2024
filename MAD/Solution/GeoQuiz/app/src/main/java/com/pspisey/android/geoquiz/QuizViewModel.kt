package com.pspisey.android.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"
class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val questionList = listOf(
        Question(R.string.mount, false),
        Question(R.string.asia1, false),
        Question(R.string.asia2, true),
        Question(R.string.america1, false),
        Question(R.string.america2, true),
        Question(R.string.europe, true),
        Question(R.string.africa1, false),
        Question(R.string.africa2, true),
        Question(R.string.oceania1, false),
        Question(R.string.oceania2, true)
    )

    var totalAnsweredQuestions  = 0
    var totalScore = 0
    var cheatAttempts = 0
    val answeredQuestions = BooleanArray(questionList.size)
    var currentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    val questionListSize: Int
        get() = questionList.size

    val currentQuestionAnswer: Boolean
        get() = questionList[currentIndex].answer

    val currentQuestionText: Int
        get() = questionList[currentIndex].textResId

    var isCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY) ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionList.size
    }
    fun backToPrev() {
        currentIndex = (currentIndex - 1) % questionList.size
        if (currentIndex < 0)
            currentIndex += questionList.size
    }
    fun reset() {
        currentIndex = 0
        isCheater = false
        totalScore = 0
        totalAnsweredQuestions = 0
        Cheat.countCheatToken = 3
        answeredQuestions.fill(false)

    }
}

