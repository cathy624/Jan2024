package com.pspisey.android.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pspisey.android.geoquiz.databinding.ActivityMainBinding
import kotlin.math.roundToInt

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()
    //private val resultViewModel: ResultViewModel by viewModels()
    private lateinit var countDownTimer: CountDownTimer

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
        if (quizViewModel.isCheater) {
            --Cheat.countCheatToken
            updateCheatTokenTextView()
        }

        if (!Cheat.canCheat()) {
            binding.cheatButton.isEnabled = false
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle any result data
                val data = result.data // Get the Intent that was returned
                val updatedQuestionsAnswered =
                    data?.getIntExtra("updatedTotalQuestionsAnswered", 0) ?: 0
                val updatedScore = data?.getIntExtra("updatedTotalScore", 0) ?: 0
                val updatedCheatAttempts = data?.getIntExtra("updatedTotalCheatAttempts", 0) ?: 0

                /* Update your ViewModel or UI with the updated data
                resultViewModel.updateTotalQuestionsAnswered(updatedQuestionsAnswered)
                resultViewModel.updateTotalScore(updatedScore)
                resultViewModel.updateTotalCheatAttempts(updatedCheatAttempts)*/
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trueBtn.setOnClickListener { view: View ->
            checkAnswer(true)
            disableAnswerButtons()
        }

        binding.falseBtn.setOnClickListener { view: View ->
            checkAnswer(false)
            disableAnswerButtons()
        }

        binding.nextBtn.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            //quizViewModel.isCheater = false
            //enableAnswerButtons()
        }

        binding.prevBtn.setOnClickListener {
            quizViewModel.backToPrev()
            updateQuestion()
        }

        binding.cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        binding.resetBtn.setOnClickListener {
            quizViewModel.reset()
            updateQuestion()
            updateCheatTokenTextView()
        }

        binding.resultBtn.setOnClickListener {
            val intent = Intent(this, ResultSummaryActivity::class.java)
            intent.putExtra("totalScore", quizViewModel.totalScore)
            intent.putExtra("totalAnsweredQuestions", quizViewModel.answeredQuestions.count { it })
            intent.putExtra("cheatAttempts", quizViewModel.cheatAttempts)
            resultLauncher.launch(intent)
        }
        updateCheatTokenTextView()
        updateQuestion()
    }
    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)

        if (quizViewModel.answeredQuestions[quizViewModel.currentIndex]) {
            disableAnswerButtons()
        } else {
            enableAnswerButtons()
        }
        // Cancel the current timer (if it's running)
        if(this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        // Start a new timer for the current question
        countDownTimer = object : CountDownTimer(10000, 1000) { // 30 seconds timer
            override fun onTick(millisUntilFinished: Long) {
                // Update the UI with the remaining time
                binding.timer.text = "Time left: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                quizViewModel.answeredQuestions[quizViewModel.currentIndex] = true
                quizViewModel.totalAnsweredQuestions++
                // Time is up for the current question, show a message and move to the next question
                Toast.makeText(this@MainActivity, "Time's up for this question!", Toast.LENGTH_SHORT).show()
                quizViewModel.moveToNext()
                updateQuestion()
            }
        }.start()
    }
    fun checkAnswer(userAnswer: Boolean) {
        if (quizViewModel.answeredQuestions[quizViewModel.currentIndex]) {
            return // Question already answered correctly
        }
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = when {
            userAnswer == correctAnswer -> {
                quizViewModel.totalScore++
                if (userAnswer == quizViewModel.isCheater) {
                    R.string.judgment_toast
                } else {
                    R.string.correct_toast
                }
            }
            else -> R.string.incorrect_toast
        }
            /*quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> {
                quizViewModel.totalScore++
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }*/
        quizViewModel.totalAnsweredQuestions++
        quizViewModel.answeredQuestions[quizViewModel.currentIndex] = true
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        disableAnswerButtons()

        // Check if all questions have been answered
        if (quizViewModel.totalAnsweredQuestions == quizViewModel.questionBank.size) {
            // All questions have been answered, launch ResultSummaryActivity
            val intent = Intent(this, ResultSummaryActivity::class.java)
            intent.putExtra("totalAnsweredQuestions", quizViewModel.totalAnsweredQuestions)
            intent.putExtra("totalScore", quizViewModel.totalScore)
            intent.putExtra("cheatAttempts", quizViewModel.cheatAttempts)
            resultLauncher.launch(intent)
            val score = quizViewModel.totalScore.toFloat().div(quizViewModel.totalAnsweredQuestions).times(100).roundToInt()
            Toast.makeText(this, "Your score $score %", Toast.LENGTH_LONG).show()
        }
    }
    private fun disableAnswerButtons() {
        binding.trueBtn.isEnabled = false
        binding.falseBtn.isEnabled = false
    }

    private fun enableAnswerButtons() {
        binding.trueBtn.isEnabled = true
        binding.falseBtn.isEnabled = true
    }

    private fun updateCheatTokenTextView() {
        binding.cheatTokenTextView.setText("Your cheat token(s): ${Cheat.countCheatToken}")
    }
        /* Mark the question as answered and disable the buttons

        //disableAnswerButtons()


        //if (quizViewModel.answeredQuestions[quizViewModel.currentIndex]) {
        //    disableAnswerButtons()
        //   return // Question already answered, no further action needed
        //}

        // Mark the question as answered
        quizViewModel.totalQuestionsAnswered += 1
        quizViewModel.answeredQuestions[quizViewModel.currentIndex] = true
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        disableAnswerButtons()
        if (quizViewModel.totalQuestionsAnswered == quizViewModel.questionBankSize){
            // Calculate the percentage score
            val percentageScore = calcPercentageScore()
            Toast.makeText(this, "Quiz Completed! Your score: $percentageScore%", Toast.LENGTH_LONG).show()

            // Show the result
            //val intent = ResultSummaryActivity.newIntent(this, quizViewModel.totalQuestionsAnswered, quizViewModel.totalScore, quizViewModel.totalCheatAttempts)
            //resultLauncher.launch(intent)
        }
        if (quizViewModel.answeredQuestions[quizViewModel.currentIndex]) {
            disableAnswerButtons()
            return // Question already answered correctly
        }
    }
    // Function to calculate the percentage score
    private fun calcPercentageScore() {
        val score =
            quizViewModel.totalScore.toFloat().div(quizViewModel.totalQuestionsAnswered).times(100).roundToInt()
        //val messageResId = "Your score: $score%"
        //Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }*/



    /*fun checkAnswer(userAnswer: Boolean, context: Context){
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageRedId = when {
            quizViewModel.isCheater -> "Cheating is wrong!" //will need to change
            userAnswer == correctAnswer -> "Well done!"
            else -> "Sorry, Wrong Answer"
        }
        Toast.makeText(context, messageRedId, Toast.LENGTH_SHORT).show()
        //quizViewModel.questionBank[quizViewModel.currentIndex].userAnswer = userAnswer
        if (quizViewModel.answeredQuestions[quizViewModel.currentIndex]) {
            disableAnswerButtons()
            return // Question already answered
        }
        quizViewModel.totalQuestionsAnswered +=1
        quizViewModel.answeredQuestions[quizViewModel.currentIndex] = true
        if (userAnswer == correctAnswer) {
            quizViewModel.totalScore++
        }

        /*quizViewModel.currentAnswers++
        if (userAnswer == correctAnswer) {
            quizViewModel.currentScore++
        }*/
        quizViewModel.showScore(context)
    }*/

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}
    /*private fun showResult() {
        // Update ResultViewModel with final data
        resultViewModel.updateTotalQuestionsAnswered(quizViewModel.totalQuestionsAnswered)
        resultViewModel.updateTotalScore(quizViewModel.totalScore)
        resultViewModel.updateTotalCheatAttempts(Cheat.countCheatToken)
        resultLauncher.launch(intent)
        // Show result to the user (implement this method according to your app's UI design)
        //Toast.makeText(this, "Quiz Complete! Score: ${quizViewModel.totalScore}", Toast.LENGTH_LONG).show()

        // You can also navigate to a new Activity or Fragment to show detailed results
        // val intent = Intent(this, ResultActivity::class.java)
        // startActivity(intent)
    }*/
/*private fun checkAnswer(userAnswer: Boolean) {
       val correctAnswer = quizViewModel.currentQuestionAnswer
       val messageResId = when {
           quizViewModel.isCheater -> R.string.judgment_toast
           userAnswer == correctAnswer -> {
               quizViewModel.totalScore =+ 1
               R.string.correct_toast
           }
           else -> R.string.incorrect_toast
       }

       if (quizViewModel.answeredQuestions[quizViewModel.currentIndex]) {
           disableAnswerButtons()
           return // Question already answered correctly
       }
       quizViewModel.totalQuestionsAnswered +=1
       quizViewModel.answeredQuestions[quizViewModel.currentIndex] = true
       Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

       if (quizViewModel.totalQuestionsAnswered == quizViewModel.questionBankSize){
           //show the result
           resultLauncher.launch(intent)
       }
   }*/