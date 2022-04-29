package pt.isel.pdm.chess4android.lichessApi

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture

class DownloadDailyChallengeWorker (appContext: Context, workerParams: WorkerParameters)
    : ListenableWorker(appContext, workerParams) {

    override fun startWork(): ListenableFuture<Result> {
        val app : ChallengeOfDayApplication = applicationContext as ChallengeOfDayApplication
        val repo = ChallengeRepository(app.challengeOfDayService, app.historyDB.getHistoryChallengeDao())

        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Starting DownloadDailyChallengeWorker")

        return CallbackToFutureAdapter.getFuture { completer ->
            repo.fetchChallengeOfDay(mustSaveToDB = true) { result ->
                result
                    .onSuccess {
                        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: DownloadDailyChallengeWorker succeeded")
                        completer.set(Result.success())
                    }
                    .onFailure {
                        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: DownloadDailyChallengeWorker failed")
                        completer.setException(it)
                    }
            }
        }
    }
}