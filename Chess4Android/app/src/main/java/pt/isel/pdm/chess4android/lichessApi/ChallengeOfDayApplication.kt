package pt.isel.pdm.chess4android.lichessApi

import android.app.Application
import android.util.Log
import androidx.room.Room
import androidx.work.*
import pt.isel.pdm.chess4android.history.HistoryDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

const val APP_TAG = "ChallengeOfDay"
const val URL = "https://lichess.org/api/"

class ChallengeOfDayApplication : Application(), Configuration.Provider{
    init {
        Log.v(APP_TAG, "ChallengeOfDayApplication.init for ${hashCode()}")
    }

    val challengeOfDayService: DailyPuzzleService by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DailyPuzzleService::class.java)
    }

    /**
     * The database that contains the "challenges of day" fetched so far.
     */
    val historyDB: HistoryDatabase by lazy {
        Room
            .databaseBuilder(this, HistoryDatabase::class.java, "history_db")
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        //starts at 3AM
        val scheduledHour: Long = 3
        val currentHour = LocalDateTime.now().hour
        val delay = if (currentHour < scheduledHour) scheduledHour - currentHour else 24 - currentHour + scheduledHour

        val workRequest = PeriodicWorkRequestBuilder<DownloadDailyChallengeWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .setInitialDelay(delay, TimeUnit.HOURS)
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "DownloadDailyChallenge",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()
}
