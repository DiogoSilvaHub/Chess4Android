package pt.isel.pdm.chess4android.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import pt.isel.pdm.chess4android.databinding.ActivityLobbyBinding
import pt.isel.pdm.chess4android.viewModels.LobbyActivityViewModel

class LobbyActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLobbyBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[LobbyActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



        binding.buttonLobbyBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        //TODO: destroy lobby before going back to LobbiesActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}