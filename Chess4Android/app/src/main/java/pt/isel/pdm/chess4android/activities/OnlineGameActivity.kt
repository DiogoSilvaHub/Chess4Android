package pt.isel.pdm.chess4android.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.databinding.ActivityLobbiesBinding

class OnlineGameActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLobbiesBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonBackLobbie.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonNewLobbie.setOnClickListener{
            val intent = Intent(this, CreateLobbyActivity::class.java)
            startActivity(intent)
        }
    }
}