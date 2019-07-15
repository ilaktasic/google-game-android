package hr.trends.ilaktasic.googleTrendsGame.name

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.LinearLayout
import hr.trends.ilaktasic.googleTrendsGame.PLAYER_NUMBER_KEY
import hr.trends.ilaktasic.googleTrendsGame.R

class NameEnterActivity : AppCompatActivity() {

    private val layout: LinearLayout by lazy { findViewById<LinearLayout>(R.id.nameLayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_enter)
        createTextViews()
    }

    private fun createTextViews() {
        val playerNumber = intent.getIntExtra(PLAYER_NUMBER_KEY, 2)
        (1 .. playerNumber).forEach { num ->
            val editText = EditText(this)
            editText.hint = "Player $num"
            layout.addView(editText)
        }
    }
}
