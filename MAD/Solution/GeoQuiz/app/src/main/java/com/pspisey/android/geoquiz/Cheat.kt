package com.pspisey.android.geoquiz

object Cheat {
    var countCheatToken = 3

    val canCheat: () -> Boolean = {
        if (countCheatToken > 0) true
        else false
    }
}