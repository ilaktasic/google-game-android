package hr.trends.ilaktasic.googleTrendsGame.word

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import hr.trends.ilaktasic.googleTrendsGame.R
import hr.trends.ilaktasic.googleTrendsGame.name.NAMES_KEY
import java.net.URL

class WordEntryActivity : AppCompatActivity() {

    private var randomWord: String? = null
    private val randomWordTextView: TextView by lazy { findViewById<TextView>(R.id.randomWord) }
    private val playerNameTextView: TextView by lazy { findViewById<TextView>(R.id.playerName) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_entry)

        val playerNames = intent.getSerializableExtra(NAMES_KEY) as Map<Int, String>
        setCurrentPlayer(playerNames[1])
        val fetchWord = FetchWord(this::updateRandomWord)
        fetchWord.execute()
    }

    private fun setCurrentPlayer(name: String?) {
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
