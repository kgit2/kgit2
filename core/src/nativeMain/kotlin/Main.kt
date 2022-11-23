import com.kgit2.KGit2
import com.kgit2.config.Config
import com.kgit2.utils.withTempDir
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun main(args: Array<String>) {
    KGit2
    withTempDir {
        var config = openConfig(it, "foo")
        runCatching {
            config.getBool("foo.bar")
        }.onFailure {
            assert(true)
        }.onSuccess {
            assert(false)
        }

        config.setBool("foo.k1", true)
        config.setInt32("foo.k2", 1)
        config.setInt64("foo.k3", 2)
        config.setString("foo.k4", "bar")
        config.snapshot()
        // config.free()

        config = Config((it / "foo").toString()).snapshot()
        assertTrue(config.getBool("foo.k1"))
        assertEquals(1, config.getInt32("foo.k2"))
        assertEquals(2L, config.getInt64("foo.k3"))
        assertEquals("bar", config.getStringBuf("foo.k4"))

        val entries = config.getEntries()
        assertEquals(4, entries.list.size)

        val snapshot = config.snapshot()
        assertEquals("bar", snapshot.getString("foo.k4"))
        // config.free()
    }
}
