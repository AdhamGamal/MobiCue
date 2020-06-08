package com.amg.mobicue.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddSpeechViewModelFactory( //arguments
    private val speechId: Int
) : ViewModelProvider.Factory {

    //initialize view model with arguments
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddSpeechViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddSpeechViewModel(speechId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}