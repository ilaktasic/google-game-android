package hr.trends.ilaktasic.googleTrendsGame.result

import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Typeface
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.loopj.android.http.ResponseHandlerInterface
import hr.trends.ilaktasic.googleTrendsGame.MainActivity
import hr.trends.ilaktasic.googleTrendsGame.R
import hr.trends.ilaktasic.googleTrendsGame.model.Player
import hr.trends.ilaktasic.googleTrendsGame.model.TransferModel
import hr.trends.ilaktasic.googleTrendsGame.name.TRANSFER_MODEL_NAME
import hr.trends.ilaktasic.googleTrendsGame.word.WordEntryActivity
import okhttp3.internal.http2.Header
import org.json.JSONArray
import java.net.URL


class ResultActivity : AppCompatActivity() {

    private val resultTextView: TextView by lazy { findViewById<TextView>(R.id.textView) }
    private val roundTextView: TextView by lazy { findViewById<TextView>(R.id.roundTextView) }
    private val finishRoundButton: Button by lazy { findViewById<Button>(R.id.button) }
    private val barChart: BarChart by lazy { findViewById<BarChart>(R.id.chart) }

    private var transferModel = TransferModel()
    private var showFinalResults = false
    private val asyncHttpClient = AsyncHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        finishRoundButton.setOnClickListener { nextRound() }
        transferModel = intent.getParcelableExtra(TRANSFER_MODEL_NAME)
        transferModel = calculatePoints(transferModel)
        setTextViews(transferModel)
        if (transferModel.currentRound == transferModel.rounds) finishRoundButton.text = "SHOW FINAL RESULTS"
        setGraph(transferModel)
    }

    private fun setTextViews(transferModel: TransferModel) {
        if(showFinalResults) {
            roundTextView.text = "FINAL RESULTS"
            resultTextView.text = "${transferModel.players.maxWith(Comparator { a, b -> a.points.compareTo(b.points) })?.name}'s a WINNER! Congratulations"
            finishRoundButton.text = "PLAY AGAIN"
        }
        else {
            finishRoundButton.text = "NEXT ROUND"
            roundTextView.text = "ROUND ${transferModel.currentRound} RESULTS"
            resultTextView.text = "The term \"${transferModel.players.maxWith(Comparator { a, b -> a.roundPoints.compareTo(b.roundPoints) })?.phraseToGoogle}\" trends more!"
        }
    }

    private fun calculatePoints(transferModel: TransferModel): TransferModel {
        transferModel.players.forEach {
            it.roundPoints = callTrendsApi(it.phraseToGoogle)
            it.points = it.points + it.roundPoints
        }
        return transferModel
    }

    private fun callTrendsApi(players: List<Player>): Int {

        var phrases = mutableListOf<String?>()

        players.forEach{ phrases.add(it.phraseToGoogle) }

        val post = asyncHttpClient.post("localhost:3000/google/trends", RequestParams(Pair("keywords", phrases)), JsonHttpResponseHandler("utf-8"))
        post.

        return (0..100).shuffled().first()
    }

    private fun setGraph(transferModel: TransferModel) {
        var dataSetList = mutableListOf<BarDataSet>()

        transferModel.players.forEachIndexed { i, player ->
            val barDataSet = BarDataSet(mutableListOf(BarEntry(i.toFloat(), if (!showFinalResults) player.roundPoints.toFloat() else player.points.toFloat())), if (!showFinalResults) player.phraseToGoogle else player.name)
            barDataSet.color = getColorFromList(i)
            barDataSet.valueTextSize = 20f
            dataSetList.add(barDataSet)
        }

        barChart.data = BarData(*dataSetList.toTypedArray())

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

        barChart.invalidate()
    }

    private fun nextRound() {
        if (transferModel.currentRound == transferModel.rounds && !showFinalResults) {
            showFinalResults = true
            setTextViews(transferModel)
            setGraph(transferModel)
        } else if (showFinalResults) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, WordEntryActivity::class.java)
            transferModel.currentRound++
            intent.putExtra(TRANSFER_MODEL_NAME, transferModel)
            startActivity(intent)
        }
    }

    private fun getColorFromList(iterator: Int): Int {
        val colors = arrayListOf(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN)
        return colors[iterator]
    }

    companion object {
        private class ResponseHandler : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<out cz.msebera.android.httpclient.Header>?, response: JSONArray?) {

                val json = response.
                super.onSuccess(statusCode, headers, response)
            }

            override fun onFailure(statusCode: Int, headers: Array<out cz.msebera.android.httpclient.Header>?, responseString: String?, throwable: Throwable?) {
                super.onFailure(statusCode, headers, responseString, throwable)
            }
        }
    }

}
