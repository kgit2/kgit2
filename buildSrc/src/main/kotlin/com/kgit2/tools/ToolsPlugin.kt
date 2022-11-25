package com.kgit2.tools

import org.gradle.api.Plugin
import org.gradle.api.Project

class ToolsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("tools") {
            group = "kgit2"
            doLast {
                println("Hello from ${project.name}")
            }
        }
    }
}
