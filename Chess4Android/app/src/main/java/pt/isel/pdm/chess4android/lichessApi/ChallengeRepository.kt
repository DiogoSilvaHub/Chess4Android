package pt.isel.pdm.chess4android.lichessApi

import android.util.Log
import pt.isel.pdm.chess4android.history.ChallengeEntity
import pt.isel.pdm.chess4android.history.HistoryChallengeDao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate


/**
 * Extension function of [ChallengeEntity] to conveniently convert it to a [ChallengeDTO] instance.
 */
private fun ChallengeEntity.toChallengeDTO() = ChallengeDTO(
    puzzleInfo = PuzzleInfo(game = Game(this.gameID, this.pgn, this.plays), Puzzle(this.id)),
    date = this.date,
    resolved = this.resolved,
    game = Game(this.gameID, this.pgn, this.plays),
    puzzle = Puzzle(this.id)
)

/**
 * Repository for the challenge Of Day
 * It's role is the one described here: https://developer.android.com/jetpack/guide
 *
 * The repository operations include I/O (network and/or DB accesses) and are therefore asynchronous.
 * For now, and for teaching purposes, we use a callback style to define those operations. Later on
 * we will use Kotlin's way: suspending functions.
 */
class ChallengeRepository(
    private val challengeOfDayService: DailyPuzzleService,
    private val historyChallengeDao: HistoryChallengeDao
) {

    /**
     * Asynchronously gets the daily challenge from the local DB, if available.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */
    private fun asyncMaybeGetTodayChallengeFromDB(callback: (Result<ChallengeEntity?>) -> Unit) {
        callbackAfterAsync(callback) {
            historyChallengeDao.getLast(1).firstOrNull()
        }
    }

    /**
     * Asynchronously gets the daily challenge from the remote API.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */
    private fun asyncGetTodayChallengeFromAPI(callback: (Result<ChallengeDTO>) -> Unit) {
        challengeOfDayService.getPuzzle().enqueue(
            object: Callback<ChallengeDTO> {
                override fun onResponse(call: Call<ChallengeDTO>, response: Response<ChallengeDTO>) {
                    Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: onResponse ")
                    val dailyChallengeDTO: ChallengeDTO = response.body()!!
                    dailyChallengeDTO.puzzleInfo = PuzzleInfo(dailyChallengeDTO.game, dailyChallengeDTO.puzzle)
                    dailyChallengeDTO.date = LocalDate.now().toString()
                    dailyChallengeDTO.resolved = false
                    val result =
                        if (response.isSuccessful)
                            Result.success(dailyChallengeDTO)
                        else
                            Result.failure(ServiceUnavailable())
                    callback(result)
                }

                override fun onFailure(call: Call<ChallengeDTO>, error: Throwable) {
                    Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: onFailure ")
                    callback(Result.failure(ServiceUnavailable(cause = error)))
                }
            }
        )
    }

    /**
     * Asynchronously saves the daily challenge to the local DB.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */
    private fun asyncSaveToDB(dto: ChallengeDTO, callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback) {
            historyChallengeDao.insert(
                ChallengeEntity(
                    id = dto.puzzleInfo.puzzle.id,
                    date = dto.date,
                    resolved = dto.resolved,
                    gameID = dto.puzzleInfo.game.id,
                    pgn = dto.puzzleInfo.game.pgn,
                    plays = if (dto.puzzleInfo.game.plays == null) " " else dto.puzzleInfo.game.plays
                )
            )
        }
    }

    /**
     * Asynchronously updates the daily challenge on the local DB.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */
    private fun asyncUpdateOnDB(dto: ChallengeDTO, callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback) {
            Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: ${dto}")
            historyChallengeDao.update(
                ChallengeEntity(
                    id = dto.puzzleInfo.puzzle.id,
                    date = dto.date,
                    resolved = dto.resolved,
                    gameID = dto.puzzleInfo.game.id,
                    pgn = dto.puzzleInfo.game.pgn,
                    plays = if (dto.puzzleInfo.game.plays == null) " " else dto.puzzleInfo.game.plays
                )
            )
        }
    }

    /**
     * Asynchronously gets the challenge of day, either from the local DB, if available, or from
     * the remote API.
     *
     * @param mustSaveToDB  indicates if the operation is only considered successful if all its
     * steps, including saving to the local DB, succeed. If false, the operation is considered
     * successful regardless of the success of saving the challenge in the local DB (the last step).
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD
     *
     * Using a boolean to distinguish between both options is a questionable design decision.
     */
    fun fetchChallengeOfDay(mustSaveToDB: Boolean = false, callback: (Result<ChallengeDTO>) -> Unit) {
        asyncMaybeGetTodayChallengeFromDB { maybeEntity ->
            val maybeChallenge = maybeEntity.getOrNull()
            if (maybeChallenge != null && maybeChallenge.isTodayChallenge()) {
                Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Got daily challenge from local DB")
                callback(Result.success(maybeChallenge.toChallengeDTO()))
            }
            else {
                asyncGetTodayChallengeFromAPI { apiResult ->
                    apiResult.onSuccess { challengeDTO ->
                        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Got daily challenge from API")
                        asyncSaveToDB(challengeDTO) { saveToDBResult ->
                            saveToDBResult.onSuccess {
                                Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Saved daily challenge to local DB")
                                callback(Result.success(challengeDTO))
                            }
                                .onFailure {
                                    Log.e(APP_TAG, "Thread ${Thread.currentThread().name}: Failed to save daily challenge to local DB", it)
                                    callback(if(mustSaveToDB) Result.failure(it) else Result.success(challengeDTO))
                                }
                        }
                    }
                    callback(apiResult)
                }
            }
        }
    }

    /**
     * Asynchronously updates the challenge, in the local DB
     *
     * @param challengeDTO  state of the challenge to update
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD
     */
    fun updateChallengeOfDay(challengeDTO: ChallengeDTO, callback: (Result<Boolean>) -> Unit) {
        asyncUpdateOnDB(challengeDTO) { updateOnDBResult ->
            updateOnDBResult
                .onSuccess {
                    Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Saved daily challenge to local DB")
                    callback(Result.success(true))
                }
                .onFailure {
                    Log.e(APP_TAG, "Thread ${Thread.currentThread().name}: Failed to save daily challenge to local DB", it)
                    callback(Result.failure(it))
                }
        }
    }
}