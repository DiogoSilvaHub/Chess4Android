package pt.isel.pdm.chess4android.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.databinding.ActivityCreateLobbyBinding

class CreateLobbyActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCreateLobbyBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonCancel.setOnClickListener {
            val intent = Intent(this, OnlineGameActivity::class.java)
            startActivity(intent)
        }
    }
}