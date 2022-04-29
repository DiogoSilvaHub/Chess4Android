package pt.isel.pdm.chess4android.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.isel.pdm.chess4android.history.HistoryChallengeDao
import pt.isel.pdm.chess4android.lichessApi.*

class HistoryActivityViewModel(application: Application): AndroidViewModel(application) {

    var history: LiveData<List<ChallengeDTO>>? = null
        private set

    private val historyDao: HistoryChallengeDao by lazy {
        getApplication<ChallengeOfDayApplication>().historyDB.getHistoryChallengeDao()
    }

    /**
     * Gets the challenges list (history) from the DB.
     */
    fun loadHistory(): LiveData<List<ChallengeDTO>> {
        val publish = MutableLiveData<List<ChallengeDTO>>()
        history = publish
        callbackAfterAsync(
            asyncAction = {
                  historyDao.getAll().map {
                      ChallengeDTO(
                          puzzleInfo = PuzzleInfo(Game(it.gameID, it.pgn, it.plays), Puzzle(it.id)),
                          date = it.date,
                          resolved = it.resolved,
                          game = Game(it.gameID, it.pgn, it.plays),
                          puzzle = Puzzle(it.id)
                      )
                  }
            },
            callback = {result ->
                result.onSuccess { publish.value = it }
                result.onFailure { publish.value = emptyList() }
            }
        )
        return publish
    }
}
