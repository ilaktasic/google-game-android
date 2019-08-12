package hr.trends.ilaktasic.googleTrendsGame.result

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.databind.ObjectMapper
import hr.trends.ilaktasic.googleTrendsGame.MainActivity
import hr.trends.ilaktasic.googleTrendsGame.R
import hr.trends.ilaktasic.googleTrendsGame.model.Player
import hr.trends.ilaktasic.googleTrendsGame.model.TransferModel
import hr.trends.ilaktasic.googleTrendsGame.model.TrendsRequestDto
import hr.trends.ilaktasic.googleTrendsGame.model.TrendsResponseDto
import hr.trends.ilaktasic.googleTrendsGame.name.TRANSFER_MODEL_NAME
import hr.trends.ilaktasic.googleTrendsGame.word.WordEntryActivity
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.LineChartView
import lecho.lib.hellocharts.view.PieChartView
import org.json.JSONObject
import java.util.HashSet
import kotlin.Comparator


class ResultActivity : AppCompatActivity() {

    private val roundTextView: TextView by lazy { findViewById<TextView>(R.id.roundTextView) }
    private val winnerTextView: TextView by lazy { findViewById<TextView>(R.id.winnerBanner) }
    private val finishRoundButton: Button by lazy { findViewById<Button>(R.id.button) }
    private val chart: LineChartView by lazy { findViewById<LineChartView>(R.id.chart) }
    private val pieChart: PieChartView by lazy { findViewById<PieChartView>(R.id.pieChart) }
    private val loader: ProgressBar by lazy { findViewById<ProgressBar>(R.id.loader) }
    private val tableLayout: TableLayout by lazy { findViewById<TableLayout>(R.id.statsTable) }

    private var transferModel = TransferModel()
    private var showFinalResults = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        finishRoundButton.setOnClickListener { nextRound() }
        transferModel = intent.getParcelableExtra(TRANSFER_MODEL_NAME)
        calculatePoints(transferModel)
    }

    private fun calculatePoints(transferModel: TransferModel) {
        callTrendsApi(transferModel.players)
        Thread.sleep(2000)
    }

    private fun addPoints(transferModel: TransferModel): TransferModel {
        transferModel.players.forEach { player ->
            val lastPoint = player.latestResult()
            player.points = player.points + lastPoint
        }
        return transferModel
    }

    @SuppressLint("NewApi")
    private fun callTrendsApi(players: List<Player>) {
        isLoading()
        //extract points to list
        val trendsDto = TrendsRequestDto(mutableListOf())
        players.forEach { trendsDto.keywords.add(it.phraseToGoogle) }

        val queue = Volley.newRequestQueue(this)
        val url = "https://trendsapi-tvz.appspot.com/google/trends"

        val stringRequest = JsonObjectRequest(Request.Method.POST, url, JSONObject(mutableMapOf(Pair("keywords", trendsDto.keywords))),
                Response.Listener { response ->
                    //cast json
                    val trendsResponseDto = ObjectMapper().readValue(response.toString(), TrendsResponseDto::class.java)
                    //set points to transfer model
                    transferModel.players.forEachIndexed { index, player ->
                        val tempResultMap = mutableMapOf<Long, Int>()
                        trendsResponseDto.default?.timelineData?.sortedBy { it.time?.toLongOrNull() }
                                ?.takeLast(10)?.forEach {
                            tempResultMap[it.time?.toLongOrNull() ?: 0] = it.value?.get(index) ?: 0
                        }
                        player.roundPoints = tempResultMap
                    }

                    transferModel = addPoints(transferModel)
                    setTextViews(transferModel)

                    if (transferModel.currentRound == transferModel.rounds) finishRoundButton.text = "SHOW FINAL RESULTS"

                    setTable(transferModel)
                    setPieChart(transferModel)
                    setGraph(transferModel)
                    loaded()
                },
                Response.ErrorListener { error ->
                    println(error.message)
                }
        )
        queue.add(stringRequest)
    }

    private fun setGraph(transferModel: TransferModel) {
        val lines = transferModel.players.mapIndexed { i, player ->
            val values = player.roundPoints.entries.map {
                PointValue(it.key.toFloat(), it.value.toFloat())
            }
            Line(values).setColor(getColorFromList(i)).setCubic(true).setHasPoints(false)
        }
        val chartData = LineChartData()
        chartData.lines = lines
        chart.setBackgroundColor(Color.LTGRAY)
        chart.lineChartData = chartData
    }

    private fun nextRound() {
        //show final results
        if (transferModel.currentRound == transferModel.rounds && !showFinalResults) {
            showFinalResults = true
            setTextViews(transferModel)
            setGraph(transferModel)
            setPieChart(transferModel)
            setTable(transferModel)
        } else if (showFinalResults) {
            //start again
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            //next round
            val intent = Intent(this, WordEntryActivity::class.java)
            transferModel.currentRound++
            intent.putExtra(TRANSFER_MODEL_NAME, transferModel)
            startActivity(intent)
        }
    }

    private fun setTable(transferModel: TransferModel) {
        val header =  TableRow(this)
        listOf("Player", "Term", "Points").forEach { addToRow(it, header) }
        tableLayout.addView(header)
        transferModel.players.forEach{
            val row = TableRow(this)

            listOf(it.name, it.phraseToGoogle, it.latestResult().toString()).forEach {label ->
                addToRow(label, row)
            }

            tableLayout.addView(row)
        }
    }

    private fun addToRow(label: String?, row: TableRow) {
        val textView = TextView(this)
        positionColumn(textView)
        textView.text = label
        val layout = LinearLayout(this)
        val params = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f)
        layout.layoutParams = params
        layout.addView(textView)
        row.addView(layout)
    }

    private fun positionColumn(textView: TextView) {
       //  textView.width = LinearLayout.LayoutParams.MATCH_PARENT
        textView.setPadding(20,5,20,5)
        textView.setTextColor(Color.BLACK)
        textView.textSize = 18F
    }

    // up to 4 player colors
    private fun getColorFromList(iterator: Int): Int {
        val colors = arrayListOf(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN)
        return colors[iterator]
    }

    //dumb I know
    private fun setTextViews(transferModel: TransferModel) {
        val points = mutableListOf<Int>()
        transferModel.players.map { it.points }.toCollection(points)

        if (showFinalResults) {
            chart.visibility = GONE
            tableLayout.visibility = GONE
            winnerTextView.visibility = VISIBLE
            roundTextView.text = "FINAL RESULTS"
            winnerTextView.text = "${transferModel.players.maxWith(Comparator { a, b -> a.points.compareTo(b.points) })?.name}'s a WINNER! Congratulations"
            finishRoundButton.text = "PLAY AGAIN"
            if (verifyAllEqualUsingHashSet(points)) {
                winnerTextView.text = "Looks like nobody won."
            }
        } else {
            finishRoundButton.text = "NEXT ROUND"
            roundTextView.text = "ROUND ${transferModel.currentRound} RESULTS"

        }

    }

    private fun setPieChart(transferModel: TransferModel) {
        val data = transferModel.players.mapIndexed { i, player ->
            val value = SliceValue()
            value.value = player.points.toFloat()
            value.color = getColorFromList(i)
            value.setLabel("${player.name} ${player.points}")
        }
        val chartData = PieChartData(data)
        chartData.setHasLabels(true)
        chartData.setHasCenterCircle(true)
        chartData.centerText1 = "Total score"
        chartData.centerText1FontSize = 15
        pieChart.pieChartData = chartData
    }

    private fun verifyAllEqualUsingHashSet(list: MutableList<Int>): Boolean {
        return HashSet<Int>(list).size <= 1
    }

    private fun isLoading() {
        roundTextView.visibility = GONE
        finishRoundButton.visibility = GONE
        chart.visibility = GONE
        loader.visibility = VISIBLE
    }

    private fun loaded() {
        roundTextView.visibility = VISIBLE
        finishRoundButton.visibility = VISIBLE
        chart.visibility = VISIBLE
        loader.visibility = GONE
    }
}
