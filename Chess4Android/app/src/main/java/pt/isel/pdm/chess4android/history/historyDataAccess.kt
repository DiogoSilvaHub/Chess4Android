package pt.isel.pdm.chess4android.history


import androidx.room.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Entity(tableName = "history_challenge")
data class ChallengeEntity(
    @PrimaryKey val id: String,
    val date: String,
    val resolved: Boolean,
    val gameID: String,
    val pgn: String,
    val plays: String,
    val timestamp: Date = Date.from(Instant.now().truncatedTo(ChronoUnit.DAYS))
) {
    fun isTodayChallenge(): Boolean =
        timestamp.toInstant().compareTo(Instant.now().truncatedTo(ChronoUnit.DAYS)) == 0
}

/**
 * Contains converters used by the ROOM ORM to map between Kotlin types and MySQL types
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long) = Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date) = date.time
}

@Dao
interface HistoryChallengeDao {
    @Insert
    fun insert(quote: ChallengeEntity)

    @Update
    fun update(quote: ChallengeEntity)

    @Delete
    fun delete(quote: ChallengeEntity)

    @Query("SELECT * FROM history_challenge ORDER BY date DESC LIMIT 100")
    fun getAll(): List<ChallengeEntity>

    @Query("SELECT * FROM history_challenge ORDER BY date DESC LIMIT :count")
    fun getLast(count: Int): List<ChallengeEntity>

    @Query("SELECT * FROM history_challenge WHERE date = :date")
    fun getByDate(date: String): List<ChallengeEntity>

}

@Database(entities = [ChallengeEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class HistoryDatabase: RoomDatabase() {
    abstract fun getHistoryChallengeDao(): HistoryChallengeDao
}

