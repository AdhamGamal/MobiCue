package com.amg.mobicue.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "speech_table", indices = [Index(value = ["speech_name", "speech_content"])])
data class Speech(
    @ColumnInfo(name = "speech_name")
    var sName: String,
    @ColumnInfo(name = "speech_content")
    var sContent: String,
    @ColumnInfo(name = "speech_created")
    val sCreated: Boolean,
    @ColumnInfo(name = "speech_size")
    var tSize: Int,
    @ColumnInfo(name = "speech_style")
    var tStyle: Int,
    @ColumnInfo(name = "speech_speed")
    var sSpeed: Long,
    @ColumnInfo(name = "speech_background")
    var sBackground: Int,
    @ColumnInfo(name = "speech_color")
    var sColor: Int
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    var id: Int = 0
}