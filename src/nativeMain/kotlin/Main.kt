import com.floater.git.KGit2
import com.floater.git.config.Config
import com.floater.git.repository.Repository

fun main() {
    println("Hello Kotlin/Native!")
    KGit2()
    // val repository = Repository("./test_repo")
    // repository.initRepository()
    // val config = repository.config()
    println(Config.findGlobal())
}
