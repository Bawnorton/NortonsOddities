pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.kikugie.dev/releases")
    maven("https://maven.kikugie.dev/snapshots")
    maven("https://maven.isxander.dev/releases")
  }
}

plugins {
  id("dev.kikugie.stonecutter") version "0.7.10"
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

stonecutter {
  create(rootProject) {
    fun mc(mcVersion: String, name: String = mcVersion, loaders: Iterable<String>) =
      loaders.forEach { version("$name-$it", mcVersion).buildscript = "build.$it.gradle.kts" }

    mc("1.20.1", loaders = listOf("forge"))

    vcsVersion = "1.20.1-forge"
  }
}

gradle.beforeProject {
  val gitDir = rootDir.resolve(".git")
  if (gitDir.exists() && gitDir.isDirectory) {
    val hooksDir = gitDir.resolve("hooks")
    val preCommitHook = hooksDir.resolve("pre-commit")

    if (!preCommitHook.exists()) {
      hooksDir.mkdirs()
      preCommitHook.writeText(
        """
                #!/bin/bash
                
                vcs_version=$(ggrep -oP 'vcsVersion\s*=\s*"\K[^"]+' settings.gradle.kts)
                active_version=$(ggrep -oP 'stonecutter\s+active\s+"\K[^"]+' stonecutter.gradle.kts)
                
                echo "Detected vcsVersion: ${'$'}vcs_version"
                echo "Detected active version: ${'$'}active_version"
                
                if [ "${'$'}vcs_version" != "${'$'}active_version" ]; then
                  echo "Please run './gradlew \"Reset active project\"' to set the stonecutter branch to the version control version."
                  exit 1
                else
                  echo "Versions match. No action needed."
                fi
                """.trimIndent()
      )
      preCommitHook.setExecutable(true)
      println("Git pre-commit hook installed.")
    }
  } else {
    println("Not a Git repository. Skipping hook installation.")
  }
}

rootProject.name = "IntegrationFixes"