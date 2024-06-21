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
private const val KEY_INDEX = "index"
private const val IS_CHEATER = "isCheater"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val quizViewModel: QuizViewModel by viewModels()
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
            Toast.makeText(this, "No more cheat tokens", Toast.LENGTH_SHORT).show()
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle any result data
                val data = result.data // Get the Intent that was returned
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX,0)?:0
        quizViewModel.currentIndex = currentIndex

        quizViewModel.isCheater = savedInstanceState?.getBoolean(IS_CHEATER,false)?:false


        //cheat button
        binding.cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        //reset button
        //serve the purpose which user can reset the selected answers/cheat attempts
        //BUT not after the quiz has been completed
        //to prevent cheating
        binding.resetBtn.setOnClickListener {
            quizViewModel.reset()
            updateQuestion()
            updateCheatTokenTextView()
            updateProgress()
        }

        //check result summary - to check total questions have been answered, correct answers, cheat attempts
        binding.resultBtn.setOnClickListener {
            val intent = Intent(this, ResultSummaryActivity::class.java)
            intent.putExtra("totalScore", quizViewModel.totalScore)
            intent.putExtra("totalAnsweredQuestions", quizViewModel.answeredQuestions.count { it })
            intent.putExtra("cheatAttempts", quizViewModel.cheatAttempts)
            resultLauncher.launch(intent)
        }

        //answer true to the question
        binding.trueBtn.setOnClickListener { view: View ->
            checkAnswer(true)
            disableAnswerButtons()
        }

        //answer false to the question
        binding.falseBtn.setOnClickListener { view: View ->
            checkAnswer(false)
            disableAnswerButtons()
        }

        //next question
        binding.nextBtn.setOnClickListener {
            quizViewModel.isCheater = false
            quizViewModel.moveToNext()
            updateQuestion()
            updateProgress()

        }

        //go back to prev question
        binding.prevBtn.setOnClickListener {
            quizViewModel.backToPrev()
            updateQuestion()
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
        countDownTimer = object : CountDownTimer(30000, 1000) { // 30 seconds timer
            override fun onTick(millisUntilFinished: Long) {

                // Update the UI with the remaining time
                binding.timer.text = "Time left: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                quizViewModel.answeredQuestions[quizViewModel.currentIndex] = true
                quizViewModel.totalAnsweredQuestions++
                // Time is up for the current question, show a message and move to the next question
                Toast.makeText(this@MainActivity, "Time's up for this question!", Toast.LENGTH_SHORT).show()
                //the question is marked as not correct - no score added and the next question will display
                quizViewModel.moveToNext()
                updateQuestion()
            }
        }.start()
    }

    //update the progress bar to align with the index of the current question
    fun updateProgress(){
        val totalQ = quizViewModel.questionListSize
        val currentQuestionIndex = quizViewModel.currentIndex
        val progress = (currentQuestionIndex.toDouble() / totalQ.toDouble())*100
        binding.progressBar.progress = progress.toInt()
    }

    fun checkAnswer(userAnswer: Boolean) {
        if (quizViewModel.answeredQuestions[quizViewModel.currentIndex]) {
            updateProgress()
            return // Question already answered correctly
        }
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> {
                quizViewModel.totalScore++
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }

        quizViewModel.totalAnsweredQuestions++
        quizViewModel.answeredQuestions[quizViewModel.currentIndex] = true
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        disableAnswerButtons()

        // Check if all questions have been answered
        if (quizViewModel.totalAnsweredQuestions == quizViewModel.questionList.size) {
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
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.d(TAG,"onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX,quizViewModel.currentIndex)
        savedInstanceState.putBoolean(IS_CHEATER,quizViewModel.isCheater)
    }

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