import kotlinx.cinterop.*
import libdemo.add
import com.floater.git.KGit2

fun main() {
    KGit2().features()
    // memScoped {
    //     val c = alloc<IntVar>()
    //     add(1, 1, c.ptr)
    //     println(c.value)
    // }
    // val curl = curl_easy_init()
    // if (curl != null) {
    //     curl_easy_setopt(curl, CURLOPT_URL, "https://example.com")
    //     curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L)
    //     val res = curl_easy_perform(curl)
    //     if (res != CURLE_OK) {
    //         println("curl_easy_perform() failed ${curl_easy_strerror(res)?.toKString()}")
    //     }
    //     curl_easy_cleanup(curl)
    // }
}
