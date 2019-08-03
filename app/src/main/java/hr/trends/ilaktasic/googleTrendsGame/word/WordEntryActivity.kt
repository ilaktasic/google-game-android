package hr.trends.ilaktasic.googleTrendsGame.word

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import hr.trends.ilaktasic.googleTrendsGame.R
import hr.trends.ilaktasic.googleTrendsGame.model.TransferModel
import hr.trends.ilaktasic.googleTrendsGame.name.TRANSFER_MODEL_NAME
import hr.trends.ilaktasic.googleTrendsGame.result.ResultActivity
import java.net.URL


class WordEntryActivity : AppCompatActivity() {

    private var randomWord: String? = null
    private val randomWordTextView: TextView by lazy { findViewById<TextView>(R.id.randomWord) }
    private val playerNameTextView: TextView by lazy { findViewById<TextView>(R.id.playerName) }
    private val roundTextView: TextView by lazy { findViewById<TextView>(R.id.roundTextView) }
    private val playerWordEditText: EditText by lazy { findViewById<EditText>(R.id.playerWord) }
    private val submitButton: Button by lazy { findViewById<Button>(R.id.submit) }

    private var currentPlayer = 0
    private var transferModel = TransferModel()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_entry)
        submitButton.setOnClickListener { nextPlayer() }
        transferModel = intent.getParcelableExtra(TRANSFER_MODEL_NAME)
        roundTextView.text = "ROUND ${transferModel.currentRound}"
        setCurrentPlayer(1)
        val fetchWord = FetchWord(this::updateRandomWord)
        fetchWord.execute()
    }

    private fun nextPlayer() {
        val chosenWord = playerWordEditText.text.toString()
        if (currentPlayer + 1 > transferModel.players.size) {
            if (chosenWord != "") {
                transferModel.players[currentPlayer - 1].phraseToGoogle = "$chosenWord $randomWord"
                //playerWords.add(Pair(transferModel[currentPlayer]!!, "$chosenWord $randomWord"))
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra(TRANSFER_MODEL_NAME, transferModel)
                startActivity(intent)
            }
        } else {
            if (chosenWord != "") {
                transferModel.players[currentPlayer - 1].phraseToGoogle = "$chosenWord $randomWord"
                //playerWords.add(Pair(transferModel[currentPlayer]!!, "$chosenWord $randomWord"))
                currentPlayer++
                setCurrentPlayer(currentPlayer)
            }
        }
    }

    private fun setCurrentPlayer(playerId: Int) {
        playerWordEditText.text.clear()
        val name = transferModel.players[playerId - 1].name
        currentPlayer = playerId
        playerNameTextView.text = "It's $name's turn"
    }

    private fun updateRandomWord(result: String) {
        randomWordTextView.text = result
        randomWord = result
    }

    companion object {
        private class FetchWord(val updateTextView: (m: String) -> Unit) : AsyncTask<String, Void, String>() {

            override fun onPostExecute(result: String?) {
                result?.let { updateTextView(it) }
            }

            override fun doInBackground(vararg params: String?): String {
                return URL("https://random-ize.com/noun/nou-f.php").readText()
            }
        }
    }
}
