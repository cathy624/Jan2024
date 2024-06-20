package com.pspisey.android.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"
class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )
    //private var currentScore = 0
    //private var currentAnswers = 0
    var totalAnsweredQuestions  = 0
    var totalScore = 0
    var cheatAttempts = 0
    val answeredQuestions = BooleanArray(questionBank.size)
    var currentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    val questionBankSize: Int
        get() = questionBank.size

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    var isCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY) ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    val userAnswer
            = mutableMapOf<Question, Pair<Byte, Boolean>>()

    fun setUserAnswer(value: Byte, isCheat: Boolean) {
        if (!userAnswer.containsKey(questionBank[currentIndex])) {
            userAnswer += (questionBank[currentIndex]
                    to (value to isCheat))
        } else {
            userAnswer[questionBank[currentIndex]] = (value to isCheat)
        }
    }

    fun computeUserScoreInPercent(): Int =
        userAnswer.values.fold(0) { f: Int, n: Pair<Byte, Boolean> ->
            if (n.second) f + 1 else f
        } * (100 / questionBank.size)

    fun isCorrectAnswer(answer: Boolean): Int {

        val correctAnswer = questionBank[currentIndex].answer

        return when {
            Cheat.isCheater -> {
                setUserAnswer(1, true)
                Cheat.isCheater = false
                cheatAttempts++
                R.string.judgment_toast
            }

            userAnswer[questionBank[currentIndex]]?.second == true -> {
                setUserAnswer(1, true)
                R.string.judgment_toast
            }

            correctAnswer == answer -> {
                setUserAnswer(1, false)
                R.string.correct_toast
            }

            else -> {
                setUserAnswer(0, false)
                R.string.incorrect_toast
            }
        }
    }

   /*fun checkAnswer(userAnswer: Boolean){
       val correctAnswer = currentQuestionAnswer
       val messageRedId = when {
           isCheater -> "Cheating is wrong!" //will need to change
           userAnswer == correctAnswer -> "You are right! Well done~"
           else -> "Wrong Answer~"
       }
       Toast.makeText(this, messageRedId, Toast.LENGTH_SHORT).show()

       questionBank[currentIndex].userAnswer = userAnswer
       currentAnswers++
   }
    private fun showScore(context: Context) {
        if (currentAnswers == questionBank.size) {
            val score =
                currentScore.toFloat().div(currentAnswers).times(100).roundToInt()
            Toast.makeText(context, "Your score $score %", Toast.LENGTH_SHORT).show()
        }

    }*/

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }
    fun backToPrev() {
        currentIndex = (currentIndex - 1) % questionBank.size
        if (currentIndex < 0)
            currentIndex += questionBank.size
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
/*if (currentIndex == 0) //optional
            reset() ////optional
        currentIndex
        */

