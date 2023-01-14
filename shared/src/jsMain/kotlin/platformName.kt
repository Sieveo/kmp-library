import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

actual fun platformName(): String {
    return "JS"
}

actual fun httpGet(client: HttpClient): String {
    TODO("Not yet implemented")
}

fun <T> runBlocking(context: CoroutineContext, block: suspend CoroutineScope.() -> T):T = TODO("Not yet implemented")