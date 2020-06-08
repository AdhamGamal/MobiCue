package com.amg.mobicue.room

import androidx.room.DatabaseView

@DatabaseView("SELECT rowid AS id, speech_name AS sName, speech_content AS sContent, speech_created AS sCreated, speech_color AS tColor, speech_background AS sBackground From speech_table")
class SpeechItem(
    var id: Int,
    val sName: String,
    val sContent: String,
    val sCreated: Boolean,
    val tColor: Int,
    val sBackground: Int
)