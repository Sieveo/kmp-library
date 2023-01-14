import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

actual fun platformName(): String {
    return "JVM"
}

actual fun <T> runBlocking(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T =runBlocking(context, block)

//actual fun httpGet(client: HttpClient): String = runBlocking {
//    client.get("https://ktor.io/docs/").bodyAsText()
//}