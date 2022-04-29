package pt.isel.pdm.chess4android.gameUtils

import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.Piece

class GameState {
    var plays : String = ""
    var winner: Army = Army.NONE
    var turn: Army = Army.WHITE
    var board : Array<Array<Pair<Army, Piece>>> =
        arrayOf(
            arrayOf(
                Pair(Army.BLACK, Piece.ROOK),
                Pair(Army.BLACK, Piece.KNIGHT),
                Pair(Army.BLACK, Piece.BISHOP),
                Pair(Army.BLACK, Piece.QUEEN),
                Pair(Army.BLACK, Piece.KING),
                Pair(Army.BLACK, Piece.BISHOP),
                Pair(Army.BLACK, Piece.KNIGHT),
                Pair(Army.BLACK, Piece.ROOK)
            ),
            arrayOf(
                Pair(Army.BLACK, Piece.PAWN),
                Pair(Army.BLACK, Piece.PAWN),
                Pair(Army.BLACK, Piece.PAWN),
                Pair(Army.BLACK, Piece.PAWN),
                Pair(Army.BLACK, Piece.PAWN),
                Pair(Army.BLACK, Piece.PAWN),
                Pair(Army.BLACK, Piece.PAWN),
                Pair(Army.BLACK, Piece.PAWN)
            ),
            arrayOf(
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE)
            ),
            arrayOf(
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE)
            ),
            arrayOf(
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE)
            ),
            arrayOf(
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE),
                Pair(Army.NONE, Piece.NONE)
            ),
            arrayOf(
                Pair(Army.WHITE, Piece.PAWN),
                Pair(Army.WHITE, Piece.PAWN),
                Pair(Army.WHITE, Piece.PAWN),
                Pair(Army.WHITE, Piece.PAWN),
                Pair(Army.WHITE, Piece.PAWN),
                Pair(Army.WHITE, Piece.PAWN),
                Pair(Army.WHITE, Piece.PAWN),
                Pair(Army.WHITE, Piece.PAWN),
            ),
            arrayOf(
                Pair(Army.WHITE, Piece.ROOK),
                Pair(Army.WHITE, Piece.KNIGHT),
                Pair(Army.WHITE, Piece.BISHOP),
                Pair(Army.WHITE, Piece.QUEEN),
                Pair(Army.WHITE, Piece.KING),
                Pair(Army.WHITE, Piece.BISHOP),
                Pair(Army.WHITE, Piece.KNIGHT),
                Pair(Army.WHITE, Piece.ROOK)
            )
        )
}