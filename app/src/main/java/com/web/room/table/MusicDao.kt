/*package com.web.room.table

import androidx.room.*
import com.web.data.Music

@Dao
interface MusicDao {
    @Update
    fun update(music: Music)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrInsert(music: Music)

    @Query("select * from music where :args")
    fun query(args: String): List<Music>
}*/
