package hr.trends.ilaktasic.googleTrendsGame

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.RadioGroup
import hr.trends.ilaktasic.googleTrendsGame.name.NameEnterActivity
import kotlinx.android.synthetic.main.activity_main.*

const val PLAYER_NUMBER_KEY = "playerNumber"

class MainActivity : AppCompatActivity() {

    private val joinGameButton: Button by lazy { findViewById<Button>(R.id.goToNames) }
    private val playerNumberRadioGroup: RadioGroup by lazy { findViewById<RadioGroup>(R.id.playerNumber) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        joinGameButton.setOnClickListener{start()}
    }

    private fun start() {
        val chosenPlayerNumber = when (playerNumber.checkedRadioButtonId) {
            R.id.twoPlayers -> 2
            R.id.threePlayers -> 3
            R.id.fourPlayers -> 4
            else ->  {
                throw Exception("Ups, this shouldn't happen")
            }
        }
        val intent = Intent(this, NameEnterActivity::class.java)
        intent.putExtra(PLAYER_NUMBER_KEY, chosenPlayerNumber)
        startActivity(intent)
    }


}

