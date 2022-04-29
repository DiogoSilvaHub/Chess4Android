package pt.isel.pdm.chess4android.lichessApi

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.http.GET


@Parcelize
data class ChallengeDTO(var puzzleInfo: PuzzleInfo, var date: String, var resolved: Boolean, val game: Game, val puzzle: Puzzle) : Parcelable

@Parcelize
data class PuzzleInfo(val game: Game, val puzzle: Puzzle) : Parcelable

@Parcelize
data class Game(val id: String, val pgn: String, var plays: String) : Parcelable

@Parcelize
data class Puzzle(val id: String) : Parcelable

interface DailyPuzzleService {
    @GET("puzzle/daily")
    fun getPuzzle(): Call<ChallengeDTO>
}

/**
 * Represents errors while accessing the remote API. Instead of tossing around Retrofit errors,
 * we can use this exception to wrap them up.
 */
class ServiceUnavailable(message: String = "", cause: Throwable? = null) : Exception(message, cause)