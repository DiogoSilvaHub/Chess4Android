package pt.isel.pdm.chess4android.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.databinding.ActivityMultiplayerBinding
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.viewModels.DailyPuzzleActivityViewModel
import pt.isel.pdm.chess4android.views.Tile

class MultiplayerActivity : AppCompatActivity()  {

    private val binding by lazy {
        ActivityMultiplayerBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[DailyPuzzleActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener {
            returnToMainActivity()
        }

        binding.buttonRestart.setOnClickListener {
            restartGame()
        }

        viewModel.currentTurn.observe(this, {
            updateTurnText(it)
        })

        binding.boardView.updateEntireBoard(viewModel.getBoard())

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
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveCurrentStateToMemory()
    }

    private fun restartGame() {
        val intent = Intent(this, MultiplayerActivity::class.java)
        startActivity(intent)
    }

    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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

    private fun displayGameOver() {
        binding.buttonRestart.visibility = View.VISIBLE
        if (viewModel.getWinner() == Army.WHITE) binding.finishMessage.text = resources.getText(R.string.white_won) else binding.finishMessage.text = resources.getText(R.string.black_won)
        binding.finishMessage.visibility = View.VISIBLE
    }
}
