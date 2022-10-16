package com.floater.git.cred

import com.floater.git.common.error.errorCheck
import com.floater.git.config.Config
import com.floater.git.model.GitBase
import io.ktor.http.*
import kotlinx.cinterop.*
import libgit2.*

open class Cred(
    override var handler: CPointer<git_cred> = memScoped {
        val pointer = allocPointerTo<git_cred>()
        git_cred_default_new(pointer.ptr).errorCheck()
        pointer.value!!
    }
) : GitBase<CPointer<git_cred>> {
    override val arena: Arena = Arena()

    companion object {
        fun sshKeyFromAgent(username: String): Cred {
            val handler = memScoped {
                val pointer = allocPointerTo<git_cred>()
                git_cred_ssh_key_from_agent(pointer.ptr, username).errorCheck()
                pointer.value!!
            }
            return Cred(handler)
        }

        fun sshKey(username: String, publicKey: String?, privateKey: String, passphrase: String?): Cred {
            val handler = memScoped {
                val pointer = allocPointerTo<git_cred>()
                git_cred_ssh_key_new(pointer.ptr, username, publicKey, privateKey, passphrase).errorCheck()
                pointer.value!!
            }
            return Cred(handler)
        }

        fun sshKeyFromMemory(username: String, publicKey: String?, privateKey: String, passphrase: String?): Cred {
            val handler = memScoped {
                val pointer = allocPointerTo<git_cred>()
                git_cred_ssh_key_memory_new(pointer.ptr, username, publicKey, privateKey, passphrase).errorCheck()
                pointer.value!!
            }
            return Cred(handler)
        }

        fun userPassPlaintext(username: String, password: String): Cred {
            val handler = memScoped {
                val pointer = allocPointerTo<git_cred>()
                git_cred_userpass_plaintext_new(pointer.ptr, username, password).errorCheck()
                pointer.value!!
            }
            return Cred(handler)
        }

        fun credentialHelper(
            config: Config,
            url: String,
            username: String?
        ): Cred {
            return memScoped {
                val (u, p) = CredentialHelper(url)
                    .setUsername(username)
                    .execute()!!
                userPassPlaintext(u, p)
            }
        }
    }
}

public class CredentialHelper(
    val url: String
) {
    /// A public field representing the currently discovered username from
    /// configuration.
    var username: String? = null
    var protocol: String
    var host: String
    var port: Int
    var path: String? = null
    var commands = mutableListOf<String>()

    init {
        val urlBuilder = URLBuilder(url)
        protocol = urlBuilder.protocol.name
        host = urlBuilder.host
        port = urlBuilder.port
    }

    public fun setUsername(username: String?): CredentialHelper {
        this.username = username
        return this
    }

    public fun config(config: Config): CredentialHelper {
        if (username == null) {
            configUsername(config)
        }
        configHelper(config)
        configUseHTTPPath(config)
        return this
    }

    public fun configUsername(config: Config) {
        try {
            this.username = config.getString(exactKey("username") ?: "")
        } catch (e: Exception) {
            try {
                this.username = config.getString(urlKey("username") ?: "")
            } catch (e: Exception) {
                this.username = config.getString("credential.username")
            }
        }
    }

    public fun configHelper(config: Config) {
        val exact = config.getString(exactKey("helper") ?: "")
        addCommand(exact)
        val urlKey = config.getString(urlKey("helper") ?: "")
        addCommand(urlKey)
        val global = config.getString("credential.helper")
        addCommand(global)
    }

    public fun configUseHTTPPath(config: Config) {
        var useHttpPath = config.getBool(exactKey("useHttpPath") ?: "")
        if (useHttpPath == null) {
            useHttpPath = config.getBool(urlKey("useHttpPath") ?: "")
        }
        if (useHttpPath == null) {
            useHttpPath = config.getBool("credential.useHttpPath")
        }
        if (useHttpPath == true) {
            val urlBuilder = URLBuilder(url)
            this.path = urlBuilder.encodedPath.trimStart('/')
        }
    }

    private fun addCommand(cmd: String?) {
        if (cmd == null) {
            return
        }
        if (cmd.startsWith("!")) {
            commands.add(cmd)
        } else if (cmd.contains("/") || cmd.contains("\\")) {
            commands.add(cmd)
        } else {
            commands.add("git credential-${cmd}")
        }
    }

    private fun exactKey(name: String): String? {
        if (url.isEmpty()) {
            return null
        }
        return "credential.${url}.${name}"
    }

    private fun urlKey(name: String): String? {
        if (protocol.isEmpty() || host.isEmpty()) {
            return null
        }
        return "credential.${protocol}://${host}.${name}"
    }

    public fun execute(): Pair<String, String>? {
        var username: String? = this.username
        var password: String? = null
        for (cmd in commands) {
            val (u, p) = executeCommand(cmd, username)
            if (!u.isNullOrEmpty() && username == null) {
                username = u
            }
            if (!p.isNullOrEmpty()) {
                password = p
            }
            if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                return (username to password)
            }
        }
        return null
    }

    private fun executeCommand(cmd: String, username: String?): Pair<String?, String?> {
//        val (u, p) = self.execute_cmd(cmd, &username);
//        if u.is_some() && username.is_none() {
//            username = u;
//        }
//        if p.is_some() && password.is_none() {
//            password = p;
//        }
//        if username.is_some() && password.is_some() {
//            break;
//        }
        // TODO: implement
        return (null to null)
    }
}
