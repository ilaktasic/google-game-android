package hr.trends.ilaktasic.googleTrendsGame

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import okhttp3.*

class MainActivity : AppCompatActivity() {
    private val joinGameButton: Button by lazy { findViewById<Button>(R.id.joinButton) }
    private val output: TextView by lazy { findViewById<TextView>(R.id.output) }
    private val client : OkHttpClient by lazy { OkHttpClient() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        joinGameButton.setOnClickListener{start()}
    }

    private fun start() {
        val req = Request.Builder().url("ws://echo.websocket.org").build()
        val listener = WSListener(this::printOutput)
        client.newWebSocket(req, listener)
        client.dispatcher().executorService().shutdown()
    }

    private fun printOutput(text: String) {
        output.append(text)
    }

}

private class  WSListener(
        val callback: (text: String) -> Unit
): WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("Bok!")
        webSocket.send("Bok!")
        webSocket.close(1000, "Aj Bok!")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println(text)
        callback(text)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println(reason)
        callback(reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println(t)
        callback(t.message.toString())
    }


}

