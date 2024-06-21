package com.pspisey.android.geoquiz

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pspisey.android.geoquiz.databinding.ActivityResultBinding

class ResultSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    val quizViewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the values from the ViewModel
        val totalQuestionsAnswered = intent.getIntExtra("totalAnsweredQuestions", 0)
        val totalScore = intent.getIntExtra("totalScore", 0)
        val cheatAttempts = (3 - Cheat.countCheatToken)

        // Update the TextViews
        binding.totalQuestionsAnsweredTextView.setText("Total Answered Questions: ${totalQuestionsAnswered}/10")
        binding.totalScoreTextView.setText("Score: ${totalScore}")
        binding.totalCheatAttemptsTextView.setText("Cheat Attempt(s): ${cheatAttempts}")

        binding.okayBtn.setOnClickListener {
            finish()
            quizViewModel.reset()
        }
    }
}