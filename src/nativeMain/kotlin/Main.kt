import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef

fun main(args: Array<String>) {
    val sb = StringBuilder("Hello, ")
    val stableRef = StableRef.create(sb)
    val ptr = stableRef.asCPointer()
    sb.append("World!")
    println(ptr.asStableRef<StringBuilder>().get().toString())
}
