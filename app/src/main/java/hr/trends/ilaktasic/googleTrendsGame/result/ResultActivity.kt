package hr.trends.ilaktasic.googleTrendsGame.result

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import hr.trends.ilaktasic.googleTrendsGame.MainActivity
import hr.trends.ilaktasic.googleTrendsGame.R
import hr.trends.ilaktasic.googleTrendsGame.model.Player
import hr.trends.ilaktasic.googleTrendsGame.model.TransferModel
import hr.trends.ilaktasic.googleTrendsGame.model.TrendsRequestDto
import hr.trends.ilaktasic.googleTrendsGame.model.TrendsResponseDto
import hr.trends.ilaktasic.googleTrendsGame.name.TRANSFER_MODEL_NAME
import hr.trends.ilaktasic.googleTrendsGame.word.WordEntryActivity
import org.json.JSONObject
import java.util.HashSet

class ResultActivity : AppCompatActivity() {

    private val resultTextView: TextView by lazy { findViewById<TextView>(R.id.textView) }
    private val roundTextView: TextView by lazy { findViewById<TextView>(R.id.roundTextView) }
    private val finishRoundButton: Button by lazy { findViewById<Button>(R.id.button) }
    private val barChart: BarChart by lazy { findViewById<BarChart>(R.id.chart) }

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
        transferModel.players.forEach {
            it.points = it.points + it.roundPoints
        }
        return transferModel
    }

    private fun callTrendsApi(players: List<Player>) {

        //extract points to list
        val trendsDto = TrendsRequestDto(mutableListOf())
        players.forEach { trendsDto.keywords.add(it.phraseToGoogle) }

        val queue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:3000/google/trends"

        val stringRequest = JsonObjectRequest(Request.Method.POST, url, JSONObject(mutableMapOf(Pair("keywords", trendsDto.keywords))),
                Response.Listener { response ->
                    //cast json
                    val trendsResponseDto = ObjectMapper().readValue(response.toString(), TrendsResponseDto::class.java)
                    //set points to transfer model
                    transferModel.players.forEachIndexed { index, player ->
                        player.roundPoints = trendsResponseDto.default?.averages?.get(index) ?: 0
                    }

                    transferModel = addPoints(transferModel)
                    setTextViews(transferModel)

                    if (transferModel.currentRound == transferModel.rounds) finishRoundButton.text = "SHOW FINAL RESULTS"

                    setGraph(transferModel)
                },
                Response.ErrorListener { error ->
                    println(error.message)
                }
        )
        queue.add(stringRequest)
    }

    private fun setGraph(transferModel: TransferModel) {
        val dataSetList = mutableListOf<BarDataSet>()

        //create bar data
        transferModel.players.forEachIndexed { i, player ->
            val barDataSet = BarDataSet(mutableListOf(BarEntry(i.toFloat(), if (!showFinalResults) player.roundPoints.toFloat() else player.points.toFloat())), if (!showFinalResults) player.phraseToGoogle else player.name)
            barDataSet.color = getColorFromList(i)
            barDataSet.valueTextSize = 20f
            dataSetList.add(barDataSet)
        }
        //add data to graph
        barChart.data = BarData(*dataSetList.toTypedArray())
        //dumb design crap
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("", ""))
        barChart.axisLeft.axisMinimum = 0f
        barChart.xAxis.isEnabled = false
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.legend.formSize = 12f
        barChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        barChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        barChart.legend.textSize = 20f
        barChart.legend.formToTextSpace = 5f
        barChart.legend.xEntrySpace = 21f
        barChart.legend.maxSizePercent = 0.9f
        barChart.legend.isWordWrapEnabled = true
        barChart.description.isEnabled = false
        //start the graph?
        barChart.invalidate()
    }

    private fun nextRound() {
        //show final results
        if (transferModel.currentRound == transferModel.rounds && !showFinalResults) {
            showFinalResults = true
            setTextViews(transferModel)
            setGraph(transferModel)
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
            roundTextView.text = "FINAL RESULTS"
            resultTextView.text = "${transferModel.players.maxWith(Comparator { a, b -> a.points.compareTo(b.points) })?.name}'s a WINNER! Congratulations"
            finishRoundButton.text = "PLAY AGAIN"
            if (verifyAllEqualUsingHashSet(points)) {
                resultTextView.text = "Looks like nobody won."
            }
        } else {
            finishRoundButton.text = "NEXT ROUND"
            roundTextView.text = "ROUND ${transferModel.currentRound} RESULTS"
            resultTextView.text = "The term \"${transferModel.players.maxWith(Comparator { a, b -> a.roundPoints.compareTo(b.roundPoints) })?.phraseToGoogle}\" trends more!"
            if (verifyAllEqualUsingHashSet(points)) {
                resultTextView.text = "Looks like nothing is trending."
            }
        }

    }

    private fun verifyAllEqualUsingHashSet(list: MutableList<Int>): Boolean {
        return HashSet<Int>(list).size <= 1
    }
}
