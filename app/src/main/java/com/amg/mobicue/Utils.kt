package com.amg.mobicue

import android.app.Application
import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.amg.mobicue.room.SpeechDatabase
import com.amg.mobicue.room.SpeechRepository

lateinit var speechRepository: SpeechRepository

fun initialGlobalRepository(application: Application) =
    SpeechRepository(SpeechDatabase.getInstance(application).getSpeechDao())

fun getColorResource(context: Context, Color: Int): Int {
    return ResourcesCompat.getColor(
        context.resources,
        when (Color) {
            0 -> R.color.black
            1 -> R.color.blue
            2 -> R.color.brown
            3 -> R.color.green
            4 -> R.color.orange
            5 -> R.color.purple
            6 -> R.color.red
            else -> R.color.white
        }, null
    )
}
