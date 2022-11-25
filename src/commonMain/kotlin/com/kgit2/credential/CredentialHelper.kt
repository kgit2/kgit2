package com.kgit2.credential

import com.kgit2.config.Config
import com.kgit2.process.Command
import com.kgit2.process.Stdio
import io.github.aakira.napier.Napier
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath

class CredentialHelper(val url: String) {
    var username: String? = null
    var protocol: String?
    var host: String?
    var port: Int?
    var path: String? = null
    var commands = mutableListOf<String>()

    init {
        val urlBuilder = runCatching { URLBuilder(url) }.getOrNull()
        protocol = urlBuilder?.protocol?.name
        host = urlBuilder?.host
        port = urlBuilder?.port
    }

    fun setUsername(username: String?): CredentialHelper {
        this.username = username
        return this
    }

    fun config(config: Config): CredentialHelper {
        if (username == null) {
            configUsername(config)
        }
        configHelper(config)
        configUseHTTPPath(config)
        return this
    }

    fun configUsername(config: Config) {
        this.username = runCatching {
            config.getEntry(exactKey("username") ?: "").value
        }.runCatching {
            getOrElse {
                config.getEntry(urlKey("username") ?: "").value
            }
        }.runCatching {
            getOrElse {
                config.getEntry("credential.username").value
            }
        }.getOrNull()
    }

    fun configHelper(config: Config) {
        runCatching {
            config.getEntry(exactKey("helper") ?: "").value
        }.onSuccess {
            addCommand(it)
        }
        runCatching {
            config.getEntry(urlKey("helper") ?: "").value
        }.onSuccess {
            addCommand(it)
        }
        runCatching {
            config.getEntry("credential.helper").value
        }.onSuccess {
            addCommand(it)
        }
    }

    fun configUseHTTPPath(config: Config) {
        val useHttpPath = runCatching {
            config.getBool(exactKey("useHttpPath") ?: throw Exception())
        }.recoverCatching {
            config.getBool(urlKey("useHttpPath") ?: throw Exception())
        }.recoverCatching {
            config.getBool("credential.useHttpPath")
        }.getOrDefault(false)

        if (useHttpPath) {
            val urlBuilder = URLBuilder(url)
            this.path = urlBuilder.encodedPath.trimStart('/')
        }
    }

    private fun addCommand(cmd: String?) {
        if (cmd == null) {
            return
        }
        if (cmd.startsWith("!")) {
            commands.add(cmd.trimStart('!'))
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
        if (protocol?.isEmpty() == true || host?.isEmpty() == true) {
            return null
        }
        return "credential.${protocol}://${host}.${name}"
    }

    fun execute(): Pair<String, String>? {
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
        var command = Command("sh")
            .args("-c", cmd, "get")
            .stdin(Stdio.Pipe)
            .stdout(Stdio.Pipe)
            .stderr(Stdio.Pipe)
        Napier.d("executing credential helper: ${command.prompt()}")
        val child = runCatching {
            command.spawn()
        }.runCatching {
            getOrElse {
                Napier.d("`sh` failed to spawn: $it")
                val parts = cmd.split(Regex("\\s+"))
                command = Command(parts[0])
                parts.subList(1, parts.size).forEach(command::arg)
                command.arg("get")
                    .stdin(Stdio.Pipe)
                    .stdout(Stdio.Pipe)
                    .stderr(Stdio.Pipe)
                Napier.d("executing credential helper: ${command.prompt()}")
                command.spawn()
            }
        }.onFailure { e ->
            Napier.d("fallback of $cmd failed with $e")
        }.getOrNull() ?: return (null to null)

        val stdinWriter = child.getChildStdin()!!
        if (!protocol.isNullOrEmpty()) {
            stdinWriter.appendLine("protocol=${protocol}")
        }
        if (!host.isNullOrEmpty()) {
            if (port != null) {
                stdinWriter.appendLine("host=${host}:${port}")
            } else {
                stdinWriter.appendLine("host=${host}")
            }
        }
        if (!path.isNullOrEmpty()) {
            stdinWriter.appendLine("path=${path}")
        }
        if (!username.isNullOrEmpty()) {
            stdinWriter.appendLine("username=${username}")
        }
        // stdinWriter.close()
        val stdoutReader = child.getChildStdout()!!
        val stderrReader = child.getChildStderr()!!
        val output = child.waitWithOutput()
        if (output.isNullOrEmpty()) {
            Napier.d("credential helper failed:\nstdout ---\n${stdoutReader.readText()}\nstderr ---\n${stderrReader.readText()}")
            return (null to null)
        }
        return parseOutput(output)
    }

    fun parseOutput(output: String): Pair<String?, String?> {
        var username: String? = null
        var password: String? = null
        for (line in output.split(Regex("([\\r\\n]+)"))) {
            val parts = line.split('=').take(2)
            if (parts.size != 2) {
                Napier.d("ignoring output line: $line")
                continue
            }
            val key = parts[0]
            val value = parts[1]
            when (key) {
                "username" -> username = value
                "password" -> password = value
                else -> Napier.d("ignoring output key: $key")
            }
        }
        return (username to password)
    }
}
