package pt.isel.pdm.chess4android.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import pt.isel.pdm.chess4android.gameUtils.GameLogic
import pt.isel.pdm.chess4android.gameUtils.GameState
import pt.isel.pdm.chess4android.lichessApi.*
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.Piece

const val APP_TAG = "ChallengeOfDay"

private const val VIEW_STATE = "DailyPuzzleActivity.ViewState"

private val coordinatesX = arrayOf("h", "g", "f", "e", "d", "c", "b", "a")
private val coordinatesY = arrayOf(1, 2, 3, 4, 5, 6, 7, 8)

class DailyPuzzleActivityViewModel(
    application: Application,
    private val savedState: SavedStateHandle
) : AndroidViewModel(application) {

    private var gameState = GameState()
    private val gameLogic = GameLogic()
    private var size = gameState.board.size

    var currentTurn = MutableLiveData(Army.WHITE)

    //boolean to store if a piece is already selected
    private var isPieceSelected : Boolean = false
    var pieceMoved: Boolean = false
    //coordinates of selected piece
    lateinit var clickToSelect : Pair<Int, Int>
    //coordinates of target tile
    private lateinit var clickToPlay : Pair<Int, Int>

    //Update the process of API request
    /**
     * The [LiveData] instance used to publish the result of [fetchChallenge].
     */
    val challengeOfDay: LiveData<ChallengeDTO> = savedState.getLiveData(VIEW_STATE)

    /**
     * The [LiveData] instance used to publish errors that occur while fetching the challenge of day
     */
    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable> = _error

    fun registerClick(position: Pair<Int, Int>): Pair<Army, Piece> {
        pieceMoved = false
        if (!isPieceSelected && gameState.board[position.first][position.second].first != gameState.turn
            || isGameOver()) {
            return Pair(Army.NONE, Piece.NONE)
        }

        if (isPieceSelected) {
            clickToPlay = position
            return if (gameLogic.validateMovement(gameState.board, clickToSelect, clickToPlay)) {
                registerPlay(clickToSelect, clickToPlay)
                //verify wining condition
                if (gameState.board[clickToPlay.first][clickToPlay.second].second == Piece.KING) {
                    gameState.winner = gameState.turn
                }
                gameState.board[clickToPlay.first][clickToPlay.second] = gameState.board[clickToSelect.first][clickToSelect.second]
                gameState.board[clickToSelect.first][clickToSelect.second] = Pair(Army.NONE, Piece.NONE)
                isPieceSelected = false
                pieceMoved = true
                if (gameState.winner == Army.NONE) {
                    swapTurn()
                }
                gameState.board[clickToPlay.first][clickToPlay.second]
            } else {
                isPieceSelected = false
                Pair(Army.NONE, Piece.NONE)
            }
        } else {
            isPieceSelected = true
            clickToSelect = position
            return gameState.board[clickToSelect.first][clickToSelect.second]
        }
    }

    private fun swapTurn() {
        gameState.turn = if (gameState.turn == Army.WHITE) Army.BLACK else Army.WHITE
        currentTurn.postValue(gameState.turn)
    }

    fun getBoard() : Array<Array<Pair<Army, Piece>>> {
        return gameState.board
    }

    private fun registerPlay(firstPos : Pair<Int, Int>, secondPos : Pair<Int, Int>) {
        gameState.plays += coordinatesX[firstPos.second] + coordinatesY[firstPos.first] +
            coordinatesX[secondPos.second] + coordinatesY[secondPos.first] + " "
    }

    fun getPlays(): String {
        return gameState.plays
    }

    fun getWinner(): Army {
        return gameState.winner
    }

    fun isGameOver(): Boolean {
        return gameState.winner != Army.NONE
    }

    fun registerPlaysPgnFormat(pgn: String) {
        gameLogic.initPuzzle(gameState, gameLogic, size, pgn)
    }

    fun registerPlaysByPlayer(plays: List<String>) {
        gameLogic.addMoves(gameState, plays)
    }

    fun fetchChallenge() {
        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Fetching ...")
        val app = getApplication<ChallengeOfDayApplication>()
        val repo = ChallengeRepository(app.challengeOfDayService, app.historyDB.getHistoryChallengeDao())
        repo.fetchChallengeOfDay { result ->
            result
                .onSuccess { savedState.set(VIEW_STATE, result.getOrThrow()) }
                .onFailure { _error.value = it }
        }
        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Returned from fetchChallenge")
    }

    fun saveCurrentStateToMemory() {
        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Saving ...")
        val app = getApplication<ChallengeOfDayApplication>()
        val repo = ChallengeRepository(app.challengeOfDayService, app.historyDB.getHistoryChallengeDao())
        challengeOfDay.value?.let {
            it.game.plays = gameState.plays
            it.puzzleInfo.game.plays = gameState.plays
            it.resolved = gameState.winner != Army.NONE
            repo.updateChallengeOfDay(it) { result ->
                result
                    .onSuccess { Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Returned from updateChallenge with response: $result") }
                    .onFailure { _error.value = it }
            }
        }
        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Returned from updateChallenge")
    }

    fun startPuzzle(pgn: String) {
        gameLogic.initPuzzle(gameState, gameLogic, size, pgn)
    }

}