import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

expect fun platformName(): String
expect fun <T> runBlocking(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T

private val client = HttpClient()

fun echoWebsocketServer() {
    println("Starting WS Server")
    embeddedServer(CIO, port = 8080) {
        install(WebSockets)
        routing {
            webSocket("/echo") {
                send(Frame.Text("Welcome, you are connected"))

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> send(Frame.Text("Right back at you: ${frame.readText()}"))
                        else -> send(Frame.Text("Only text frames are supported currently"))
                    }
                }
            }
        }
    }.start(true)
}

fun printPlatformName() {
    println("Hi there ${platformName()}")

    val response = runBlocking(Dispatchers.Default) { client.get("https://ktor.io/docs/").bodyAsText() }
    println("Response from HTTP request to ktor.io: $response")
}