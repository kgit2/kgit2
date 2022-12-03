import com.kgit2.model.StrArray
import kotlinx.cinterop.get
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString

fun main(args: Array<String>) {
    val strArray = StrArray()
    strArray.add("Hello0")
    strArray.add("Hello1")
    strArray.add("Hello2")
    strArray.add("Hello3")
    strArray.add("Hello4")
    strArray.add("Hello5")
    for (i in 0 until strArray.size) {
        println(strArray.raw.handler.pointed.strings!![i]!!.toKString())
    }
    strArray.clear()
    strArray.add("Hello0")
    strArray.add("Hello1")
    strArray.add("Hello2")
    strArray.add("Hello3")
    strArray.add("Hello4")
    strArray.add("Hello5")
    for (i in 0 until strArray.size) {
        println(strArray.raw.handler.pointed.strings!![i]!!.toKString())
    }
}
