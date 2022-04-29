package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.GridLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.Piece
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.views.Tile.Type


typealias TileTouchListener = (tile: Tile, row: Int, column: Int) -> Unit

/**
 * Custom view that implements a chess board.
 */
@SuppressLint("ClickableViewAccessibility")
class BoardView(private val ctx: Context, attrs: AttributeSet?) : GridLayout(ctx, attrs) {


    private val side = 8
    private var tiles: Array<Array<Tile?>?> =
        arrayOf(
            arrayOfNulls(side),
            arrayOfNulls(side),
            arrayOfNulls(side),
            arrayOfNulls(side),
            arrayOfNulls(side),
            arrayOfNulls(side),
            arrayOfNulls(side),
            arrayOfNulls(side)
        )

        private val brush = Paint().apply {
        ctx.resources.getColor(R.color.chess_board_black, null)
        style = Paint.Style.STROKE
        strokeWidth = 10F
    }

    private fun createImageEntry(army: Army, piece: Piece, imageId: Int) =
        Pair(Pair(army, piece), VectorDrawableCompat.create(ctx.resources, imageId, null))

    private val piecesImages = mapOf(
        createImageEntry(Army.WHITE, Piece.PAWN, R.drawable.ic_white_pawn),
        createImageEntry(Army.WHITE, Piece.KNIGHT, R.drawable.ic_white_knight),
        createImageEntry(Army.WHITE, Piece.BISHOP, R.drawable.ic_white_bishop),
        createImageEntry(Army.WHITE, Piece.ROOK, R.drawable.ic_white_rook),
        createImageEntry(Army.WHITE, Piece.QUEEN, R.drawable.ic_white_queen),
        createImageEntry(Army.WHITE, Piece.KING, R.drawable.ic_white_king),
        createImageEntry(Army.BLACK, Piece.PAWN, R.drawable.ic_black_pawn),
        createImageEntry(Army.BLACK, Piece.KNIGHT, R.drawable.ic_black_knight),
        createImageEntry(Army.BLACK, Piece.BISHOP, R.drawable.ic_black_bishop),
        createImageEntry(Army.BLACK, Piece.ROOK, R.drawable.ic_black_rook),
        createImageEntry(Army.BLACK, Piece.QUEEN, R.drawable.ic_black_queen),
        createImageEntry(Army.BLACK, Piece.KING, R.drawable.ic_black_king),
    )

    init {
        //initTilesArray()
        rowCount = side
        columnCount = side
        repeat(side * side) {
            val row = it / side
            val column = it % side
            val tile = Tile(
                ctx,
                if((row + column) % 2 == 0) Type.WHITE else Type.BLACK,
                side,
                piecesImages
            )
            tile.setOnClickListener { onTileClickedListener?.invoke(tile, row, column) }

            //initial render board
            if (row == 1) tile.piece = Pair(Army.BLACK, Piece.PAWN)
            if (row == 6) tile.piece = Pair(Army.WHITE, Piece.PAWN)
            if (row == 0 && (column == 0 || column == 7)) tile.piece = Pair(Army.BLACK, Piece.ROOK)
            if (row == 7 && (column == 0 || column == 7)) tile.piece = Pair(Army.WHITE, Piece.ROOK)
            if (row == 0 && (column == 1 || column == 6)) tile.piece = Pair(Army.BLACK, Piece.KNIGHT)
            if (row == 7 && (column == 1 || column == 6)) tile.piece = Pair(Army.WHITE, Piece.KNIGHT)
            if (row == 0 && (column == 2 || column == 5)) tile.piece = Pair(Army.BLACK, Piece.BISHOP)
            if (row == 7 && (column == 2 || column == 5)) tile.piece = Pair(Army.WHITE, Piece.BISHOP)
            if (row == 0 && column == 3) tile.piece = Pair(Army.BLACK, Piece.QUEEN)
            if (row == 7 && column == 3) tile.piece = Pair(Army.WHITE, Piece.QUEEN)
            if (row == 0 && column == 4) tile.piece = Pair(Army.BLACK, Piece.KING)
            if (row == 7 && column == 4) tile.piece = Pair(Army.WHITE, Piece.KING)
            addView(tile)
            tiles[row]?.set(column, tile)
        }
    }

    var onTileClickedListener: TileTouchListener? = null

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, brush)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), brush)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), brush)
        canvas.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), brush)
    }

    fun updateEntireBoard(board: Array<Array<Pair<Army, Piece>>>) {
        for (row in 0 until side) {
            for (column in 0 until side) {
                tiles[row]?.get(column)?.piece = board[row][column]
            }
        }
    }

    fun setTile(piece: Pair<Army, Piece>, row: Int, column: Int) {
        this.tiles[row]?.get(column)?.piece = piece
    }

    fun clearTile(row: Int, column: Int) {
        this.tiles[row]?.get(column)?.piece = null
    }
}