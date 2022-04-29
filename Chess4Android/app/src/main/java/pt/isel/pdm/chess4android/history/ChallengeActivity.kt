package pt.isel.pdm.chess4android.history

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.activities.DailyPuzzleActivity
import pt.isel.pdm.chess4android.activities.HistoryActivity
import pt.isel.pdm.chess4android.databinding.ActivityChallengeBinding
import pt.isel.pdm.chess4android.lichessApi.ChallengeDTO
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.Piece
import pt.isel.pdm.chess4android.viewModels.DailyPuzzleActivityViewModel

private const val CHALLENGE_EXTRA = "CHALLENGE_EXTRA"

/**
 * The screen used to display the a challenge.
 */
class ChallengeActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityChallengeBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[DailyPuzzleActivityViewModel::class.java]
    }

    companion object {
        fun buildIntent(origin: Activity, challengeDTO: ChallengeDTO): Intent {
            val msg = Intent(origin, ChallengeActivity::class.java)
            msg.putExtra("CHALLENGE_EXTRA", challengeDTO)
            return msg
        }
    }

    /**
     * Sets up the screen look and behaviour
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenge)
        setContentView(binding.root)

        val challengeDTO = intent.getParcelableExtra<ChallengeDTO>(CHALLENGE_EXTRA)!!

        val pgn: String = challengeDTO.puzzleInfo.game.pgn

        if (!challengeDTO.resolved) {
            returnToDailyPuzzleActivity(pgn)
        }


        viewModel.registerPlaysPgnFormat(pgn)
        updateBoard(viewModel.getBoard())

        binding.buttonDailyPuzzleBack.setOnClickListener {
            returnToHistoryActivity()
        }

        binding.restartGameButton.setOnClickListener {
            returnToDailyPuzzleActivity(pgn)
        }

        binding.visualizeEndGameButton.setOnClickListener {
            showTheFinishedGame(challengeDTO.puzzleInfo.game.plays)
        }
    }

    private fun showTheFinishedGame(plays: String) {
        binding.restartGameButton.visibility = View.INVISIBLE
        binding.visualizeEndGameButton.visibility = View.INVISIBLE
        binding.message.text = resources.getText(R.string.end_game)
        viewModel.registerPlaysByPlayer(plays.split(" ").map { it.trim() })
        updateBoard(viewModel.getBoard())
    }

    private fun returnToDailyPuzzleActivity(pgn: String) {
        val intent = Intent(this, DailyPuzzleActivity::class.java)
        intent.putExtra("PGN_EXTRA", pgn)
        startActivity(intent)
    }

    private fun updateBoard(board : Array<Array<Pair<Army, Piece>>>) {
        binding.boardView.updateEntireBoard(board)
    }

    private fun returnToHistoryActivity() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }
}