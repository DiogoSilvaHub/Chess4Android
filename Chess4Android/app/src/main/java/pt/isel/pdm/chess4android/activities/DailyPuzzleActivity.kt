package pt.isel.pdm.chess4android.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.databinding.ActivityDailyPuzzleBinding
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.Piece
import pt.isel.pdm.chess4android.viewModels.DailyPuzzleActivityViewModel
import pt.isel.pdm.chess4android.views.Tile
import java.lang.Thread.sleep


class DailyPuzzleActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityDailyPuzzleBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[DailyPuzzleActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //use this date to check pgn in DB
        val pgn = intent.getStringExtra("PGN_EXTRA")

        if (pgn == null){
            viewModel.challengeOfDay.observe(this) {
                viewModel.startPuzzle(it.puzzleInfo.game.pgn)
                updateBoard(viewModel.getBoard())
            }
            viewModel.error.observe(this) { displayErrorMessage() }
        }else{
            viewModel.startPuzzle(pgn)
            updateBoard(viewModel.getBoard())
        }

        viewModel.currentTurn.observe(this, {
            updateTurnText(it)
        })

        binding.boardView.onTileClickedListener = { _: Tile, row: Int, column: Int ->
            val piece = viewModel.registerClick(Pair(row, column))
            if (piece.first != Army.NONE && viewModel.pieceMoved) {
                //puts selected tile on target tile
                binding.boardView.setTile(piece, row, column)

                //clears the tile that should now be empty
                binding.boardView.clearTile(viewModel.clickToSelect.first, viewModel.clickToSelect.second)

                //verifies winning condition and shows message indicating the winner
                if (viewModel.isGameOver()) {
                    displayGameOver()
                }
            }
        }

        binding.buttonDailyPuzzleBack.setOnClickListener {
            viewModel.saveCurrentStateToMemory()
            returnToMainActivity()
        }

        viewModel.fetchChallenge()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveCurrentStateToMemory()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.saveCurrentStateToMemory()
    }

    private fun displayErrorMessage() {
        binding.errorMessage.text = resources.getText(R.string.error_message)
        binding.errorMessage.visibility = View.VISIBLE
        sleep(2000)
        binding.errorMessage.visibility = View.INVISIBLE
    }

    private fun displayGameOver() {
        if (viewModel.getWinner() == Army.WHITE) binding.finishMessage.text = resources.getText(R.string.white_won) else binding.finishMessage.text = resources.getText(R.string.black_won)
        binding.finishMessage.visibility = View.VISIBLE
    }

    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun updateBoard(board : Array<Array<Pair<Army, Piece>>>) {
        binding.boardView.updateEntireBoard(board)
    }

    private fun updateTurnText(turn: Army) {
        if (turn == Army.WHITE) {
            binding.textViewDailyChallengeTurn.text = resources.getText(R.string.white_turn)
            binding.textViewDailyChallengeTurn.setTextColor(Color.WHITE)
        } else {
            binding.textViewDailyChallengeTurn.text = resources.getText(R.string.black_turn)
            binding.textViewDailyChallengeTurn.setTextColor(Color.BLACK)
        }
    }
}