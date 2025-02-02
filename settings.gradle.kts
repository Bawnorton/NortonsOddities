pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		maven("https://maven.architectury.dev")
		maven("https://maven.minecraftforge.net/")
		maven("https://maven.neoforged.net/releases/")
		maven("https://maven.kikugie.dev/releases/")
		mavenCentral()
		gradlePluginPortal()
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.5.1"
}

fun getProperty(key: String): String? {
	return settings.extra[key] as? String
}

fun getVersions(key: String): Set<String> {
	return getProperty(key)!!.split(',').map { it.trim() }.toSet()
}

val versions = mapOf(
	"forge" to getVersions("forge_versions"),
)

val sharedVersions = versions.map { entry ->
	val loader = entry.key
	entry.value.map { "$it-$loader" }
}.flatten().toSet()

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"

	create(rootProject) {
		versions(sharedVersions)
	}
}

rootProject.name = getProperty("mod_name")!!