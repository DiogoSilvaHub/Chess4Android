package pt.isel.pdm.chess4android.gameUtils

import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.Piece
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class GameLogic {
    //√((1−0)^2+(3−0)^2)
    private val knightStep = sqrt((1-0).toDouble().pow(2) + (2-0).toDouble().pow(2))

    fun validateMovement(board: Array<Array<Pair<Army, Piece>>>, curPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
        try {
            if (checkIfNotMove(curPos, nextPos)) {
                return false
            }

            return when (board[curPos.first][curPos.second].second) {
                Piece.PAWN -> validatePawnMovement(board, curPos, nextPos)
                Piece.BISHOP -> validateBishopMovement(board, curPos, nextPos)
                Piece.KING -> validateKingMovement(board, curPos, nextPos)
                Piece.QUEEN -> validateQueenMovement(board, curPos, nextPos)
                Piece.ROOK -> validateRookMovement(board, curPos, nextPos)
                Piece.KNIGHT -> validateKnightMovement(board, curPos, nextPos)
                Piece.NONE -> return false
            }
        } catch (e: Exception) {
            return false
        }

    }

    private fun validateKill(board : Array<Array<Pair<Army, Piece>>>, curPos: Pair<Int, Int>, nextPos : Pair<Int, Int>) : Boolean {
        return board[curPos.first][curPos.second].first != board[nextPos.first][nextPos.second].first
    }

    private fun validatePawnMovement(board: Array<Array<Pair<Army, Piece>>>, curPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
        val army: Army = board[curPos.first][curPos.second].first
        //if not attacking else validate killing blow
        if (board[nextPos.first][nextPos.second] == Pair(Army.NONE, Piece.NONE)) {
            //can only move vertically
            if (curPos.second == nextPos.second) {
                //Black - move down | White - move up
                if (army == Army.BLACK) {
                    //allows to move 2 spaces from starting position
                    return nextPos.first == curPos.first + 1 || (nextPos.first == curPos.first + 2 && curPos.first == 1)
                } else if (army == Army.WHITE) {
                    //allows to move 2 spaces from starting position
                    return nextPos.first == curPos.first - 1 || (nextPos.first == curPos.first - 2 && curPos.first == 6)
                }
            }
        } else {
            //can only kill diagonally
            //maintains the same moving logic: Black - move down | White - move up
            if (army == Army.BLACK) {
                return (nextPos.first == curPos.first + 1 &&
                        (nextPos.second == curPos.second + 1 || nextPos.second == curPos.second - 1))
                        && validateKill(board, curPos, nextPos)
            } else if (army == Army.WHITE) {
                return (nextPos.first == curPos.first - 1 &&
                        (nextPos.second == curPos.second + 1 || nextPos.second == curPos.second - 1))
                        && validateKill(board, curPos, nextPos)
            }
        }

        return false
    }

    private fun validateBishopMovement(board: Array<Array<Pair<Army, Piece>>>, curPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
        if (abs(curPos.first - nextPos.first) == abs(curPos.second - nextPos.second)) {
            if(curPos.first - nextPos.first > 0) { // x para a esquerda
                if (curPos.second - nextPos.second > 0) { // y para cima
                    var j = nextPos.second + 1
                    for (i in nextPos.first + 1 until curPos.first) {
                        if (board[i][j] != Pair(Army.NONE, Piece.NONE)) return false
                        j++
                    }
                }
                if (curPos.second - nextPos.second < 0) { // y para baixo
                    var j = nextPos.second - 1
                    for (i in nextPos.first + 1 until curPos.first){
                        if (board[i][j] != Pair(Army.NONE, Piece.NONE)) return false
                        j--
                    }
                }
            }
            if(curPos.first - nextPos.first < 0) { // x para a direita
                if (curPos.second - nextPos.second > 0) { // y para cima
                    var j = curPos.second - 1
                    for (i in curPos.first + 1 until nextPos.first) {
                        if (board[i][j] != Pair(Army.NONE, Piece.NONE)) return false
                        j--
                    }
                }
                if (curPos.second - nextPos.second < 0) { // y para baixo
                    var j = curPos.second + 1
                    for (i in curPos.first + 1 until nextPos.first) {
                        if (board[i][j] != Pair(Army.NONE, Piece.NONE)) return false
                        j++
                    }
                }
            }
            if (board[nextPos.first][nextPos.second] != Pair(Army.NONE, Piece.NONE)) {
                return validateKill(board, curPos, nextPos)
            }
            return true
        }
        return false
    }

    private fun validateKingMovement(board: Array<Array<Pair<Army, Piece>>>, curPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
        return (curPos.first - nextPos.first >= -1 && curPos.first - nextPos.first <= 1
            && curPos.second - nextPos.second >= -1 && curPos.second - nextPos.second <= 1
            && (board[nextPos.first][nextPos.second] == Pair(Army.NONE, Piece.NONE)
            || validateKill(board, curPos, nextPos)))
    }

    private fun validateQueenMovement(board: Array<Array<Pair<Army, Piece>>>, curPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
        return validateBishopMovement(board, curPos, nextPos) || validateRookMovement(board, curPos, nextPos)
    }

    private fun validateRookMovement(board: Array<Array<Pair<Army, Piece>>>, curPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
        // if: vertical movement | else: horizontal movement
        if (curPos.first == nextPos.first && curPos.second != nextPos.second) {
            //if: up | else: down
            if ((nextPos.second - curPos.second) > 0) {
                //-1 on the nextPos so it doesn't check that position yet.
                for (i in curPos.second + 1 until nextPos.second) {
                    if (board[curPos.first][i] != Pair(Army.NONE, Piece.NONE))
                        return false
                }
            } else {
                for (i in curPos.second - 1 downTo nextPos.second + 1) {
                    if (board[curPos.first][i] != Pair(Army.NONE, Piece.NONE))
                        return false
                }
            }
        } else if (curPos.first != nextPos.first && curPos.second == nextPos.second) {
            //if: right | else: left
            if ((nextPos.first - curPos.first) > 0) {
                for (i in curPos.first + 1 until nextPos.first) {
                    if (board[i][curPos.second] != Pair(Army.NONE, Piece.NONE))
                        return false
                }
            } else {
                for (i in curPos.first - 1 downTo nextPos.first + 1) {
                    if (board[i][curPos.second] != Pair(Army.NONE, Piece.NONE))
                        return false
                }
            }
        } else {
            return false
        }

        return (
            validateKill(board, curPos, nextPos)
            || (board[nextPos.first][nextPos.second] == Pair(Army.NONE, Piece.NONE))
        )
    }

    private fun validateKnightMovement(board: Array<Array<Pair<Army, Piece>>>, curPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
        //if it's the right position and the position is empty or is a valid kill
        if (calculateDistanceBetweenTwoPoints(curPos, nextPos) == knightStep
            && (board[nextPos.first][nextPos.second] == Pair(Army.NONE, Piece.NONE) || validateKill(board, curPos, nextPos))) {
            return true
        }

        return false
    }

    private fun checkIfNotMove(curPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
        return curPos.first == nextPos.first && curPos.second == nextPos.second
    }

    private fun calculateDistanceBetweenTwoPoints(curPos: Pair<Int, Int>, nextPos: Pair<Int, Int>) : Double {
        return sqrt((((nextPos.first - curPos.first).toDouble()).pow(2)) + ((nextPos.second - curPos.second).toDouble().pow(2)) )
    }

    /**
     * Make moves from user
     * @param plays list of moves
     * similar to converterPieces method
     */
    fun addMoves(gameState: GameState, plays: List<String>) {
        for (play in plays) {
            val curPos: Pair<Int, Int> = Pair('8' - play[1], play[0] - 'a')
            val nextPos: Pair<Int, Int> = Pair('8' - play[3], play[2] - 'a')
            val piece: Pair<Army, Piece> = gameState.board[curPos.first][curPos.second]
            if (validateMovement(gameState.board, curPos, nextPos)) {
                gameState.board[curPos.first][curPos.second] = Pair(Army.NONE, Piece.NONE)
                gameState.board[nextPos.first][nextPos.second] = piece
            }
            changeTurn(gameState)
        }
    }

    fun initPuzzle(gameState: GameState, gameLogic: GameLogic, size: Int, puzzleMoves : String) {
        converterPieces(gameState, gameLogic, size, puzzleMoves.split(" "))
    }

    private fun converterPieces(gameState: GameState, gameLogic: GameLogic, size: Int, list: List<String>) {
        val iter = list.iterator()
        while (iter.hasNext()) {
            decodeMove(gameState, gameLogic, size, iter.next())
        }
    }

    private fun decodeMove(gameState: GameState, gameLogic: GameLogic, size: Int, play: String) {
        //Assuming that pgn is correct
        if (play == "") return
        val firstLetter = play[0]
        // Kill piece
        if (play[1] == 'x') {
            if (firstLetter in 'a'..'h') {
                putPieceIfPossible(gameState, gameLogic, size, play[3] - '0', play[2] - 'a', Piece.PAWN, play[0] - 'a')
            } else {
                when (firstLetter) {
                    'R' -> putPieceIfPossible(gameState, gameLogic, size, play[3] - '0', play[2] - 'a', Piece.ROOK, null)
                    'K' -> putPieceIfPossible(gameState, gameLogic, size, play[3] - '0', play[2] - 'a', Piece.KING, null)
                    'Q' -> putPieceIfPossible(gameState, gameLogic, size, play[3] - '0', play[2] - 'a', Piece.QUEEN, null)
                    'B' -> putPieceIfPossible(gameState, gameLogic, size, play[3] - '0', play[2] - 'a', Piece.BISHOP, null)
                    'N' -> putPieceIfPossible(gameState, gameLogic, size, play[3] - '0', play[2] - 'a', Piece.KNIGHT, null)
                }
            }
        } else {
            // Castling
            if (play == "O-O") {
                swapKingWithRookRight(gameState, gameState.turn)
            } else if (play == "O-O-O") {
                swapKingWithRookLeft(gameState, gameState.turn)
            }
            // Don't kill
            if (firstLetter in 'a'..'h') {
                putPieceIfPossible(gameState, gameLogic, size, play[1] - '0', play[0] - 'a', Piece.PAWN, play[0] - 'a')
            } else {
                when (firstLetter) {
                    'R' -> putPieceIfPossible(gameState, gameLogic, size, play[2] - '0', play[1] - 'a', Piece.ROOK, null)
                    'K' -> putPieceIfPossible(gameState, gameLogic, size, play[2] - '0', play[1] - 'a', Piece.KING, null)
                    'Q' -> putPieceIfPossible(gameState, gameLogic, size, play[2] - '0', play[1] - 'a', Piece.QUEEN, null)
                    'B' -> putPieceIfPossible(gameState, gameLogic, size, play[2] - '0', play[1] - 'a', Piece.BISHOP, null)
                    'N' -> putPieceIfPossible(gameState, gameLogic, size, play[2] - '0', play[1] - 'a', Piece.KNIGHT, null)
                }
            }
        }
        changeTurn(gameState)
    }

    private fun changeTurn(gameState: GameState) {
        gameState.turn = if (gameState.turn == Army.WHITE) Army.BLACK else Army.WHITE
    }

    private fun swapKingWithRookLeft(gameState: GameState, army: Army) {
        val line: Int = if (army == Army.WHITE) 7 else 0
        gameState.board[line][0] = Pair(Army.NONE, Piece.NONE)
        gameState.board[line][1] = Pair(Army.NONE, Piece.NONE)
        gameState.board[line][2] = Pair(army, Piece.KING)
        gameState.board[line][3] = Pair(army, Piece.ROOK)
        gameState.board[line][4] = Pair(Army.NONE, Piece.NONE)
    }

    private fun swapKingWithRookRight(gameState: GameState, army: Army) {
        val line: Int = if (army == Army.WHITE) 7 else 0
        gameState.board[line][4] = Pair(Army.NONE, Piece.NONE)
        gameState.board[line][5] = Pair(army, Piece.ROOK)
        gameState.board[line][6] = Pair(army, Piece.KING)
        gameState.board[line][7] = Pair(Army.NONE, Piece.NONE)
    }

    private fun putPieceIfPossible(gameState: GameState, gameLogic: GameLogic, size: Int, num: Int, ch: Int, piece: Piece, prevCol: Int?) {
        var currArmy: Army
        var currPiece: Piece
        for (line in 0 until size) {
            for (col in 0 until size) {
                currArmy = gameState.board[line][col].first
                currPiece = gameState.board[line][col].second
                if (currArmy != Army.NONE && currPiece != Piece.NONE) {
                    if (currArmy == gameState.turn && currPiece == piece) {
                        //Log.v(TAG, "${currArmy}  ${currPiece}  ${piece} (${line},${col}) (${num},${ch})")
                        if (gameLogic.validateMovement(gameState.board, Pair(line, prevCol ?: col), Pair(size - num, ch))) {
                            //Log.v(TAG, "validated movement")
                            gameState.board[line][prevCol ?: col] = Pair(Army.NONE, Piece.NONE)
                            gameState.board[size - num][ch] = Pair(gameState.turn, piece)
                            return
                        }
                    }
                }
            }
        }
    }
}