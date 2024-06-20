package com.bignerdranch.android.geoquiz

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bignerdranch.android.geoquiz.ResultViewModel
import com.bignerdranch.android.geoquiz.R
import com.bignerdranch.android.geoquiz.databinding.ActivityResultBinding

class ResultSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    private val resultViewModel: ResultViewModel by viewModels()
    private val quizViewModel: QuizViewModel by viewModels()

    private var totalQuestionsAnswered: Int = quizViewModel.totalAnsweredQuestions
    private var totalScore: Int = quizViewModel.totalAnsweredQuestions
    private var cheatAttempts: Int = (3 - Cheat.countCheatToken)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.okayBtn.setOnClickListener {
            finish()
        }
    }
    private fun updateTotalQuestionsAnswered() {
        binding.totalQuestionsAnsweredTextView.setText(totalQuestionsAnswered)
    }
    private fun updateTotalCheatAttempts() {
        binding.totalCheatAttemptsTextView.setText(cheatAttempts)
    }
    private fun updateTotalScore() {
        binding.totalScoreTextView.setText(totalScore)
    }



}

/*        // Retrieve data from intent and display it
        val totalQuestionsAnswered = intent.getIntExtra("totalQuestionsAnswered", 0)
        val totalScore = intent.getIntExtra("totalScore", 0)
        val totalCheatAttempts = intent.getIntExtra("totalCheatAttempts", 0)

        binding.totalQuestionsAnsweredTextView.text = totalQuestionsAnswered.toString()
        binding.totalScoreTextView.text = totalScore.toString()
        binding.totalCheatAttemptsTextView.text = totalCheatAttempts.toString()
    }
}
         Observe the LiveData properties
        viewModel.totalQuestionsAnswered.observe(this, Observer { totalQuestions ->
            // Update the UI with the total questions answered
            findViewById<TextView>(R.id.totalQuestionsAnsweredTextView).text = totalQuestions.toString()
        })

        viewModel.totalScore.observe(this, Observer { totalScore ->
            // Update the UI with the total score
            findViewById<TextView>(R.id.totalScoreTextView).text = totalScore.toString()
        })

        viewModel.totalCheatAttempts.observe(this, Observer { totalCheatAttempts ->
            // Update the UI with the total cheat attempts
            findViewById<TextView>(R.id.totalCheatAttemptsTextView).text = totalCheatAttempts.toString()
        })

        // Example of updating the values
        viewModel.updateTotalQuestionsAnswered(20)
        viewModel.updateTotalScore(15)
        viewModel.updateTotalCheatAttempts(2)
    }
}*/
