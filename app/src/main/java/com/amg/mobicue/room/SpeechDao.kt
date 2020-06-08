package com.amg.mobicue.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SpeechDao {

    @Transaction
    @Query("SELECT * From speechitem")
    fun getSpeeches(): LiveData<List<SpeechItem>>

    @Transaction
    @Query("SELECT * From speechitem WHERE sName LIKE '%' || :sText || '%' OR sContent LIKE '%' || :sText || '%'")
    fun getSpeechesByNameOrContent(sText: String): LiveData<List<SpeechItem>>

    @Transaction
    @Query("SELECT * From speech_table WHERE rowid = :id")
    fun getSpeechById(id: Int): LiveData<Speech>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(speech: Speech)

    @Update
    fun update(speech: Speech)

    @Delete
    fun delete(speech: Speech)

}