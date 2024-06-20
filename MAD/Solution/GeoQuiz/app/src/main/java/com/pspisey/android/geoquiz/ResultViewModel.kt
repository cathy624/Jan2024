package com.pspisey.android.geoquiz


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ResultViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    // Backing properties to prevent external modification
    private val _totalQuestionsAnswered = MutableLiveData<Int>()
    private val _totalScore = MutableLiveData<Int>()
    private val _totalCheatAttempts = MutableLiveData<Int>()

    // Public LiveData properties
    val totalQuestionsAnswered: LiveData<Int> get() = _totalQuestionsAnswered
    val totalScore: LiveData<Int> get() = _totalScore
    val totalCheatAttempts: LiveData<Int> get() = _totalCheatAttempts

    init {
        // Initialize with default values
        _totalQuestionsAnswered.value = 0
        _totalScore.value = 0
        _totalCheatAttempts.value = 0
    }

    // Method to update total questions answered
    fun updateTotalQuestionsAnswered(questionsAnswered: Int) {
        _totalQuestionsAnswered.value = questionsAnswered
    }

    // Method to update total score
    fun updateTotalScore(score: Int) {
        _totalScore.value = score
    }

    // Method to update total cheat attempts
    fun updateTotalCheatAttempts(cheatAttempts: Int) {
        _totalCheatAttempts.value = cheatAttempts
    }

    // Method to reset all values
    fun reset() {
        _totalQuestionsAnswered.value = 0
        _totalScore.value = 0
        _totalCheatAttempts.value = 0
    }
}