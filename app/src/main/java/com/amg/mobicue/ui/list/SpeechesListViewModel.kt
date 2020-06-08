package com.amg.mobicue.ui.list

import android.app.Application
import androidx.lifecycle.*
import com.amg.mobicue.initialGlobalRepository
import com.amg.mobicue.room.Speech
import com.amg.mobicue.room.SpeechItem
import com.amg.mobicue.speechRepository

class SpeechesListViewModel(application: Application) : AndroidViewModel(application) {

    private val query = MutableLiveData("")
    var speechItems: LiveData<List<SpeechItem>>

    init {
        speechRepository = initialGlobalRepository(application)
        speechItems = speechRepository.speeches
        speechItems = Transformations.switchMap(query) {
            if (it.isNullOrBlank())
                speechRepository.speeches
            else
                speechRepository.getSpeechesByNameOrContent(it)
        }
    }

    fun insert(speech: Speech) {
        speechRepository.insert(speech)
    }

    fun getSpeechesByNameOrContent(sText: String) {
        query.value = sText
    }
}

