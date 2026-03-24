plugins {
  kotlin("jvm") version "2.3.0" apply false
  id("dev.kikugie.stonecutter")
  id("net.neoforged.moddev.legacyforge") version "2.0.91" apply false
  id("me.modmuss50.mod-publish-plugin") version "0.8.+" apply false
}

stonecutter active "1.20.1-forge"

stonecutter tasks {
  order("publishModrinth")
  order("publishCurseforge")
}


for (version in stonecutter.versions.map { it.version }.distinct()) tasks.register("publish$version") {
  group = "publishing"
  dependsOn(stonecutter.tasks.named("publishMods") { metadata.version == version })
}