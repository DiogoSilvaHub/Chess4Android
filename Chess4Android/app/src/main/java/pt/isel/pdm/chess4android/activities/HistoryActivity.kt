package pt.isel.pdm.chess4android.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chess4android.databinding.ActivityHistoryBinding
import pt.isel.pdm.chess4android.history.HistoryAdapter
import pt.isel.pdm.chess4android.history.ChallengeActivity.Companion.buildIntent
import pt.isel.pdm.chess4android.viewModels.HistoryActivityViewModel


class HistoryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<HistoryActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.dailyChallengeList.layoutManager = LinearLayoutManager(this)

        binding.historyBackButton.setOnClickListener {
            returnToMainActivity()
        }

        // Get the list of quotes, if we haven't fetched it yet
        val dataSource = viewModel.history
        if (dataSource == null) {
            viewModel.loadHistory().observe(this) {
                binding.dailyChallengeList.adapter = HistoryAdapter(it) { challengeDTO ->
                    startActivity(buildIntent(this, challengeDTO))
                }
            }
        } else {
            dataSource.observe(this) {
                binding.dailyChallengeList.adapter = HistoryAdapter(it) { challengeDTO ->
                    startActivity(buildIntent(this, challengeDTO))
                }
            }
        }
        /*
        (viewModel.history ?: viewModel.loadHistory()).observe(this) {
            binding.quoteList.adapter = HistoryAdapter(it)
        }
        */
        viewModel.loadHistory()
    }

    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}