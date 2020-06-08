package com.amg.mobicue.ui.display

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.amg.mobicue.room.Speech
import com.amg.mobicue.speechRepository

class DisplaySpeechViewModel(speechId: Int) : ViewModel() {

    val speechLiveData: LiveData<Speech> = speechRepository.getSpeechById(speechId)
    lateinit var speech: Speech

    fun update(speech: Speech) {
        speechRepository.update(speech)

    }

    fun delete(speech: Speech) {
        speechRepository.delete(speech)
    }
}
