package com.example.learnigapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabDao {
    @Insert
    suspend fun insert(vocab: Vocab): Long

    @Insert
    suspend fun insertAll(vocabs: List<Vocab>)

    @Update
    suspend fun update(vocab: Vocab)

    @Delete
    suspend fun delete(vocab: Vocab)

    @Query("SELECT * FROM Vocab ORDER BY word")
    fun getAll(): Flow<List<Vocab>>

    // New words: repetitions == 0
    @Query("SELECT * FROM Vocab WHERE repetitions = 0 ORDER BY word")
    fun getNewVocabs(): Flow<List<Vocab>>

    // Revise words: repetitions between 1 and 6, due on or before today
    @Query("SELECT * FROM Vocab WHERE repetitions > 0 AND repetitions < 7 AND dueDay <= :today ORDER BY dueDay")
    fun getReviseVocabs(today: Long): Flow<List<Vocab>>

    // Learned words: repetitions >= 7
    @Query("SELECT * FROM Vocab WHERE repetitions >= 7 ORDER BY word")
    fun getLearnedVocabs(): Flow<List<Vocab>>
}
