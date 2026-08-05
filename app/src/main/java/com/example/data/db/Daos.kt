package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY createdAt DESC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE id = :id")
    suspend fun getCollectionById(id: Long): CollectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Update
    suspend fun updateCollection(collection: CollectionEntity)

    @Delete
    suspend fun deleteCollection(collection: CollectionEntity)

    @Query("DELETE FROM collections WHERE id = :id")
    suspend fun deleteCollectionById(id: Long)
}

@Dao
interface SavedRequestDao {
    @Query("SELECT * FROM saved_requests ORDER BY updatedAt DESC")
    fun getAllSavedRequests(): Flow<List<SavedRequestEntity>>

    @Query("SELECT * FROM saved_requests WHERE collectionId = :collectionId ORDER BY updatedAt DESC")
    fun getRequestsByCollection(collectionId: Long): Flow<List<SavedRequestEntity>>

    @Query("SELECT * FROM saved_requests WHERE id = :id")
    suspend fun getRequestById(id: Long): SavedRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: SavedRequestEntity): Long

    @Update
    suspend fun updateRequest(request: SavedRequestEntity)

    @Query("DELETE FROM saved_requests WHERE id = :id")
    suspend fun deleteRequestById(id: Long)

    @Query("DELETE FROM saved_requests WHERE collectionId = :collectionId")
    suspend fun deleteRequestsByCollectionId(collectionId: Long)
}

@Dao
interface EnvironmentDao {
    @Query("SELECT * FROM environments ORDER BY createdAt ASC")
    fun getAllEnvironments(): Flow<List<EnvironmentEntity>>

    @Query("SELECT * FROM environments WHERE isActive = 1 LIMIT 1")
    fun getActiveEnvironmentFlow(): Flow<EnvironmentEntity?>

    @Query("SELECT * FROM environments WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveEnvironment(): EnvironmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnvironment(environment: EnvironmentEntity): Long

    @Update
    suspend fun updateEnvironment(environment: EnvironmentEntity)

    @Query("UPDATE environments SET isActive = 0")
    suspend fun deactivateAllEnvironments()

    @Query("UPDATE environments SET isActive = CASE WHEN id = :id THEN 1 ELSE 0 END")
    suspend fun setActiveEnvironment(id: Long)

    @Query("DELETE FROM environments WHERE id = :id")
    suspend fun deleteEnvironmentById(id: Long)
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT 100")
    fun getRecentHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity): Long

    @Query("DELETE FROM history")
    suspend fun clearAllHistory()

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)
}
