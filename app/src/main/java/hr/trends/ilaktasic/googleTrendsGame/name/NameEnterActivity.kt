package hr.trends.ilaktasic.googleTrendsGame.name

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import hr.trends.ilaktasic.googleTrendsGame.PLAYER_NUMBER_KEY
import hr.trends.ilaktasic.googleTrendsGame.R
import hr.trends.ilaktasic.googleTrendsGame.model.Player
import hr.trends.ilaktasic.googleTrendsGame.model.TransferModel
import hr.trends.ilaktasic.googleTrendsGame.word.WordEntryActivity

const val TRANSFER_MODEL_NAME = "TRANSFER_MODEL"

class NameEnterActivity : AppCompatActivity() {
    private val transferModel = TransferModel(3)
    private val playerNames = mutableMapOf<Int, String>()
    private val startGameButton: Button by lazy { findViewById<Button>(R.id.startGame) }
    private val layout: LinearLayout by lazy { findViewById<LinearLayout>(R.id.nameLayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        val playerNumber = intent.getIntExtra(PLAYER_NUMBER_KEY, 2)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_enter)
        createExitTexts(playerNumber)
        startGameButton.setOnClickListener { startGame() }
        startGameButton.isEnabled = false
    }

    private fun createExitTexts(playerNumber: Int) {
        (1..playerNumber).forEach { num ->
            val editText = createEditText(num, playerNumber)
            editText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(editable: Editable?) {
                    playerNames[num] = editable.toString()
                    if (isValid(playerNumber)) {
                        startGameButton.isEnabled = true
                    }
                }
            })
            layout.addView(editText)
        }
    }

    private fun createEditText(num: Int, playerNumber: Int): EditText {
        val editText = EditText(this)
        editText.hint = "Player $num"
        editText.id = num
        editText.gravity = Gravity.CENTER
        editText.setSingleLine(true)
        editText.textSize = 18F
        editText.offsetTopAndBottom(35)
        if (num != playerNumber) {
            editText.imeOptions = EditorInfo.IME_ACTION_NEXT
            editText.nextFocusForwardId = num + 1
        } else {
            editText.imeOptions = EditorInfo.IME_ACTION_DONE
        }
        return editText
    }

    private fun startGame() {
        for((_, v) in playerNames) {
            transferModel.players.add(Player(v, 0, 0))
        }
        val intent = Intent(this, WordEntryActivity::class.java)
        intent.putExtra(TRANSFER_MODEL_NAME, transferModel)
        startActivity(intent)
    }

    private fun isValid(playerNumber: Int): Boolean {
        if (playerNames.size == playerNumber) {
            return !playerNames.entries.any { entry -> entry.value == "" }
        }
        return false
    }

}
