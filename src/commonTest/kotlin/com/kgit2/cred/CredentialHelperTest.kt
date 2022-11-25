package com.kgit2.cred

import com.kgit2.config.tempConfig
import com.kgit2.credential.Credential
import com.kgit2.credential.CredentialHelper
import com.kgit2.kgitRunTest
import com.kgit2.utils.Posix
import com.kgit2.utils.openReadWrite
import com.kgit2.utils.withTempDir
import io.ktor.utils.io.core.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CredentialHelperTest {
    @Test
    fun credentialHelper1() = kgitRunTest {
        withTempDir {
            val config = tempConfig(it) {
                configs("credential.helper" to "!f() { echo username=a; echo password=b; }; f")
            }
            val helper = CredentialHelper("https://example.com/foo/bar").config(config)
            val (username, password) = helper.execute()!!
            assertEquals("a", username)
            assertEquals("b", password)
        }
    }

    @Test
    fun credentialHelper2() = kgitRunTest {
        withTempDir {
            val config = tempConfig(it)
            val helper = CredentialHelper("https://example.com/foo/bar").config(config)
            assertNull(helper.execute())
        }
    }

    @Test
    fun credentialHelper3() = kgitRunTest {
        withTempDir {
            val config = tempConfig(it) {
                configs(
                    "credential.https://example.com.helper" to "!f() { echo username=c; }; f",
                    "credential.helper" to "!f() { echo username=a; echo password=b; }; f"
                )
            }
            val helper = CredentialHelper("https://example.com/foo/bar").config(config)
            val (username, password) = helper.execute()!!
            assertEquals("c", username)
            assertEquals("b", password)
        }
    }

    @Test
    fun credentialHelper4() = kgitRunTest {
        withTempDir {
            val scriptPath = it / "script"
            val scriptFile = openReadWrite(scriptPath, mustCreate = true, mustExist = false)
            val byteString = "#!/bin/sh\necho username=c".toByteArray()
            scriptFile.write(0, byteString, 0, byteString.size)
            scriptFile.close()
            Posix.chmod(scriptPath.toString())

            val config = tempConfig(it) {
                configs(
                    "credential.https://example.com.helper" to scriptPath.toString(),
                    "credential.helper" to "!f() { echo username=a; echo password=b; }; f"
                )
            }
            val helper = CredentialHelper("https://example.com/foo/bar").config(config)
            val (username, password) = helper.execute()!!
            assertEquals("c", username)
            assertEquals("b", password)
        }
    }

    @Test
    fun credentialHelper5() = kgitRunTest {
        withTempDir {
            val scriptPath = it / "git-credential-script"
            val scriptFile = openReadWrite(scriptPath, mustCreate = true, mustExist = false)
            val byteString = "#!/bin/sh\necho username=c".toByteArray()
            scriptFile.write(0, byteString, 0, byteString.size)
            scriptFile.close()
            Posix.chmod(scriptPath.toString())

            val pathEnv = "${scriptPath.parent.toString()}:${Posix.getEnv("PATH")}"
            Posix.setEnv("PATH", pathEnv)

            val config = tempConfig(it) {
                configs(
                    "credential.https://example.com.helper" to "script",
                    "credential.helper" to "!f() { echo username=a; echo password=b; }; f"
                )
            }

            val helper = CredentialHelper("https://example.com/foo/bar").config(config)
            val (username, password) = helper.execute()!!
            assertEquals("c", username)
            assertEquals("b", password)
        }
    }

    @Test
    fun credentialHelper6() = kgitRunTest {
        withTempDir {
            val config = tempConfig(it) {
                configs("credential.helper" to "")
            }

            val helper = CredentialHelper("https://example.com/foo/bar").config(config)
            assertNull(helper.execute())
        }
    }

    @Test
    fun credentialHelper7() = kgitRunTest {
        withTempDir {
            val scriptPath = it / "script"
            val scriptFile = openReadWrite(scriptPath, mustCreate = true, mustExist = false)
            val byteString = "#!/bin/sh\necho username=$1\necho password=$2".toByteArray()
            scriptFile.write(0, byteString, 0, byteString.size)
            scriptFile.close()
            Posix.chmod(scriptPath.toString())

            val config = tempConfig(it) {
                configs("credential.helper" to "$scriptPath a b")
            }

            val helper = CredentialHelper("https://example.com/foo/bar").config(config)
            val (username, password) = helper.execute()!!
            assertEquals("a", username)
            assertEquals("b", password)
        }
    }

    @Test
    fun credentialHelper8() = kgitRunTest {
        withTempDir {
            val config = tempConfig(it) {
                configs("credential.useHttpPath" to "true")
            }
            config.setBool("credential.useHttpPath", true)
            val helper = CredentialHelper("https://example.com/foo/bar").config(config)
            assertEquals("foo/bar", helper.path)
        }
    }

    @Test
    fun credentialHelper9() = kgitRunTest {
        withTempDir {
            val config = tempConfig(it) {
                configs("credential.helper" to "!f() { while read line; do eval ${"$"}line; done; if [ \"${"$"}host\" = example.com:3000 ]; then echo username=a; echo password=b; fi; }; f")
            }

            val helper = CredentialHelper("https://example.com:3000/foo/bar").config(config)
            val (username, password) = helper.execute()!!
            assertEquals("a", username)
            assertEquals("b", password)
        }
    }

    @Test
    fun sshKeyFromMemory() = kgitRunTest {
        val credential = Credential(
            username = "test",
            publicKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDByAO8uj+kXicj6C2ODMspgmUoVyl5eaw8vR6a1yEnFuJFzevabNlN6Ut+CPT3TRnYk5BW73pyXBtnSL2X95BOnbjMDXc4YIkgs3YYHWnxbqsD4Pj/RoGqhf+gwhOBtL0poh8tT8WqXZYxdJQKLQC7oBqf3ykCEYulE4oeRUmNh4IzEE+skD/zDkaJ+S1HRD8D8YCiTO01qQnSmoDFdmIZTi8MS8Cw+O/Qhym1271ThMlhD6PubSYJXfE6rVbE7A9RzH73A6MmKBlzK8VTb4SlNSrr/DOk+L0uq+wPkv+pm+D9WtxoqQ9yl6FaK1cPawa3+7yRNle3m+72KCtyMkQv",
            privateKey = """
                -----BEGIN RSA PRIVATE KEY-----
                Proc-Type: 4,ENCRYPTED
                DEK-Info: AES-128-CBC,818C7722D3B01F2161C2ACF6A5BBAAE8

                3Cht4QB3PcoQ0I55j1B3m2ZzIC/mrh+K5nQeA1Vy2GBTMyM7yqGHqTOv7qLhJscd
                H+cB0Pm6yCr3lYuNrcKWOCUto+91P7ikyARruHVwyIxKdNx15uNulOzQJHQWNbA4
                RQHlhjON4atVo2FyJ6n+ujK6QiBg2PR5Vbbw/AtV6zBCFW3PhzDn+qqmHjpBFqj2
                vZUUe+MkDQcaF5J45XMHahhSdo/uKCDhfbylExp/+ACWkvxdPpsvcARM6X434ucD
                aPY+4i0/JyLkdbm0GFN9/q3i53qf4kCBhojFl4AYJdGI0AzAgbdTXZ7EJHbAGZHS
                os5K0oTwDVXMI0sSE2I/qHxaZZsDP1dOKq6di6SFPUp8liYimm7rNintRX88Gl2L
                g1ko9abp/NlgD0YY/3mad+NNAISDL/YfXq2fklH3En3/7ZrOVZFKfZXwQwas5g+p
                VQPKi3+ae74iOjLyuPDSc1ePmhUNYeP+9rLSc0wiaiHqls+2blPPDxAGMEo63kbz
                YPVjdmuVX4VWnyEsfTxxJdFDYGSNh6rlrrO1RFrex7kJvpg5gTX4M/FT8TfCd7Hn
                M6adXsLMqwu5tz8FuDmAtVdq8zdSrgZeAbpJ9D3EDOmZ70xz4XBL19ImxDp+Qqs2
                kQX7kobRzeeP2URfRoGr7XZikQWyQ2UASfPcQULY8R58QoZWWsQ4w51GZHg7TDnw
                1DRo/0OgkK7Gqf215nFmMpB4uyi58cq3WFwWQa1IqslkObpVgBQZcNZb/hKUYPGk
                g4zehfIgAfCdnQHwZvQ6Fdzhcs3SZeO+zVyuiZN3Gsi9HU0/1vpAKiuuOzcG02vF
                b6Y6hwsAA9yphF3atI+ARD4ZwXdDfzuGb3yJglMT3Fr/xuLwAvdchRo1spANKA0E
                tT5okLrK0H4wnHvf2SniVVWRhmJis0lQo9LjGGwRIdsPpVnJSDvaISIVF+fHT90r
                HvxN8zXI93x9jcPtwp7puQ1C7ehKJK10sZ71OLIZeuUgwt+5DRunqg6evPco9Go7
                UOGwcVhLY200KT+1k7zWzCS0yVQp2HRm6cxsZXAp4ClBSwIx15eIoLIrjZdJRjCq
                COp6pZx1fnvJ9ERIvl5hon+Ty+renMcFKz2HmchC7egpcqIxW9Dsv6zjhHle6pxb
                37GaEKHF2KA3RN+dSV/K8n+C9Yent5tx5Y9a/pMcgRGtgu+G+nyFmkPKn5Zt39yX
                qDpyM0LtbRVZPs+MgiqoGIwYc/ujoCq7GL38gezsBQoHaTt79yYBqCp6UR0LMuZ5
                f/7CtWqffgySfJ/0wjGidDAumDv8CK45AURpL/Z+tbFG3M9ar/LZz/Y6EyBcLtGY
                Wwb4zs8zXIA0qHrjNTnPqHDvezziArYfgPjxCIHMZzms9Yn8+N02p39uIytqg434
                BAlCqZ7GYdDFfTpWIwX+segTK9ux0KdBqcQv+9Fwwjkq9KySnRKqNl7ZJcefFZJq
                c6PA1iinZWBjuaO1HKx3PFulrl0bcpR9Kud1ZIyfnh5rwYN8UQkkcR/wZPla04TY
                8l5dq/LI/3G5sZXwUHKOcuQWTj7Saq7Q6gkKoMfqt0wC5bpZ1m17GHPoMz6GtX9O
                -----END RSA PRIVATE KEY-----
            """.trimIndent(),
            passphrase = "test123",
            fromMemory = true,
        )
        assertNotNull(credential)
    }
}
