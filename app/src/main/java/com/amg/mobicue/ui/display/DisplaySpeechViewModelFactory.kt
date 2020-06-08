package com.amg.mobicue.ui.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DisplaySpeechViewModelFactory(
    //arguments
    private val speechId: Int
) : ViewModelProvider.Factory {

    //initialize view model with arguments
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DisplaySpeechViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DisplaySpeechViewModel(speechId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}