package com.pspisey.android.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pspisey.android.geoquiz.databinding.ActivityCheatBinding

const val EXTRA_ANSWER_SHOWN = "com.pspisey.android.geoquiz.answer_shown"
private const val EXTRA_ANSWER_IS_TRUE =
    "com.pspisey.android.geoquiz.answer_is_true"
private var answerIsTrue = false
private const val IS_CHEATER = "isCheater"
private const val TAG = "CheatActivity"
class CheatActivity : AppCompatActivity() {

    private var wasCheated = false
    private lateinit var binding: ActivityCheatBinding
    private val cheatViewModel: CheatViewModel by viewModels()
    private val quizViewModel: QuizViewModel by viewModels()
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_CHEATER,wasCheated)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wasCheated = savedInstanceState?.getBoolean(IS_CHEATER, false) ?: false
        setAnswerShownResult(wasCheated)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        binding.showAnswerButton.setOnClickListener {
            wasCheated = true
            setAnswerShownResult(true)
            fillTextIfCheated()
        }
        fillTextIfCheated()
    }

    private fun fillTextIfCheated() {
        if (wasCheated) {
            val answerText = when {
                answerIsTrue -> R.string.trueBtn
                else -> R.string.falseBtn
            }
            binding.answerTextView.setText(answerText)
        }
    }
        /*binding.showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.trueBtn
                else -> R.string.falseBtn
            }
            binding.answerTextView.setText(answerText)
            setAnswerShownResult(true)
            cheatViewModel.answerWasClicked = true
        }
        if (cheatViewModel.answerWasClicked) {
            binding.answerTextView.setText(R.string.trueBtn)
            setAnswerShownResult(true)
        }
        }*/

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        if (wasCheated) {
            val data = Intent().apply {
                putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
            }
            setResult(Activity.RESULT_OK, data)
        }
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}
