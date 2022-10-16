import kotlinx.cinterop.*
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.posix.uuid_generate
import platform.posix.uuid_string_t
import platform.posix.uuid_t
import platform.posix.uuid_unparse

@OptIn(ExperimentalUnsignedTypes::class)
fun main() {
    for (i in 0 until 1000) {
        lateinit var uuidString: String
        memScoped {
            val uuid = UByteArray(16)
            uuid.usePinned { uuidPin ->
                uuid_generate(uuidPin.addressOf(0))
                val uuidBuffer = ByteArray(36)
                uuidBuffer.usePinned { uuidBufferPin ->
                    uuid_unparse(uuidPin.addressOf(0), uuidBufferPin.addressOf(0))
                    uuidString = uuidBuffer.decodeToString()
                }
            }
        }
        println(uuidString)
        val path = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "kgit2" / uuidString
//        println("repo path: $path")
        if (!FileSystem.SYSTEM.exists(path)) {
            println("create $i")
//            FileSystem.SYSTEM.createDirectories(path, false)
        }
    }
}
