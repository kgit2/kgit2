import cnames.structs.git_repository
import com.kgit2.common.error.errorCheck
import kotlinx.cinterop.*
import libgit2.*

object CloneSample {
    fun clone(vararg args: String) {
        val url = "https://github.com/BppleMan/floater_test_repo.git"
        val path = "/Users/bppleman/floater-test-repo"

        memScoped {
            val cloneOptions = alloc<git_clone_options>()
            git_clone_init_options(cloneOptions.ptr, GIT_CLONE_OPTIONS_VERSION)
            cloneOptions.bare = 0
            cloneOptions.checkout_opts.checkout_strategy = GIT_CHECKOUT_SAFE
            cloneOptions.checkout_opts.progress_cb = staticCFunction { path, cur, total, payload ->
                val user = payload?.asStableRef<User>()?.get()
                user?.checkoutProgress(path?.toKString(), cur.convert(), total.convert())
            }
            val user = User("bppleman")
            val nativeUser = StableRef.create(user)
            cloneOptions.checkout_opts.progress_payload = nativeUser.asCPointer()
            val repo = allocPointerTo<git_repository>()
            git_clone(repo.ptr, url, path, cloneOptions.ptr).errorCheck()
        }
    }
}

data class User(
    val username: String,
) {
    fun checkoutProgress(path: String?, cur: Int, total: Int) {
        println("path: $path, cur: $cur, total: $total")
    }
}
