package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentProfileDao {
    @Query("SELECT * FROM student_profile WHERE id = 'student_1' LIMIT 1")
    fun getProfileFlow(): Flow<StudentProfile?>

    @Query("SELECT * FROM student_profile WHERE id = 'student_1' LIMIT 1")
    suspend fun getProfile(): StudentProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: StudentProfile)
}

@Dao
interface RoadmapStepDao {
    @Query("SELECT * FROM roadmap_step ORDER BY week ASC, day ASC")
    fun getRoadmapFlow(): Flow<List<RoadmapStep>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<RoadmapStep>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: RoadmapStep)

    @Update
    suspend fun updateStep(step: RoadmapStep)

    @Query("DELETE FROM roadmap_step")
    suspend fun clearRoadmap()
}

@Dao
interface JobApplicationDao {
    @Query("SELECT * FROM job_application ORDER BY id DESC")
    fun getAllApplicationsFlow(): Flow<List<JobApplication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: JobApplication)

    @Delete
    suspend fun deleteApplication(app: JobApplication)
    
    @Update
    suspend fun updateApplication(app: JobApplication)
}

@Dao
interface PortfolioItemDao {
    @Query("SELECT * FROM portfolio_item ORDER BY category ASC, id DESC")
    fun getAllPortfolioItemsFlow(): Flow<List<PortfolioItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioItem(item: PortfolioItem)

    @Delete
    suspend fun deletePortfolioItem(item: PortfolioItem)
}

@Dao
interface RecruiterPrefsDao {
    @Query("SELECT * FROM recruiter_prefs WHERE id = 'recruiter_1' LIMIT 1")
    fun getPrefsFlow(): Flow<RecruiterPrefs?>

    @Query("SELECT * FROM recruiter_prefs WHERE id = 'recruiter_1' LIMIT 1")
    suspend fun getPrefs(): RecruiterPrefs?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePrefs(prefs: RecruiterPrefs)
}

@Dao
interface InterviewInvitationDao {
    @Query("SELECT * FROM interview_invitation ORDER BY id DESC")
    fun getAllInvitationsFlow(): Flow<List<InterviewInvitation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitation(invite: InterviewInvitation)

    @Update
    suspend fun updateInvitation(invite: InterviewInvitation)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_message WHERE type = :type ORDER BY timestamp ASC")
    fun getMessagesFlow(type: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_message WHERE type = :type")
    suspend fun clearMessages(type: String)
}

@Database(
    entities = [
        StudentProfile::class,
        RoadmapStep::class,
        JobApplication::class,
        PortfolioItem::class,
        RecruiterPrefs::class,
        InterviewInvitation::class,
        ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentProfileDao(): StudentProfileDao
    abstract fun roadmapStepDao(): RoadmapStepDao
    abstract fun jobApplicationDao(): JobApplicationDao
    abstract fun portfolioItemDao(): PortfolioItemDao
    abstract fun recruiterPrefsDao(): RecruiterPrefsDao
    abstract fun interviewInvitationDao(): InterviewInvitationDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "careeros_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
