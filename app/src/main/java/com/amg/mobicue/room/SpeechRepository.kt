package com.amg.mobicue.room

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SpeechRepository(private var speechDao: SpeechDao) {

    var speeches = speechDao.getSpeeches()

    fun getSpeechesByNameOrContent(sText: String) = speechDao.getSpeechesByNameOrContent(sText)

    fun getSpeechById(id: Int) = speechDao.getSpeechById(id)

    fun insert(speech: Speech) {
        GlobalScope.launch {
            speechDao.insert(speech)
        }
    }

    fun update(speech: Speech) {
        GlobalScope.launch {
            speechDao.update(speech)
        }
    }

    fun delete(speech: Speech) {
        GlobalScope.launch {
            speechDao.delete(speech)
        }
    }

}

