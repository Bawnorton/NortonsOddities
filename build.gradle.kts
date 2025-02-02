@file:Suppress("UnstableApiUsage")

plugins {
    `maven-publish`
    java
    kotlin("jvm") version "2.1.0"
    id("dev.architectury.loom") version "1.7-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.5.+"
}

val mod = ModData(project)
val loader = LoaderData(project, loom.platform.get().name.lowercase())
val minecraftVersion = stonecutter.current.version.substringBeforeLast("-")

version = "${mod.version}-$loader+$minecraftVersion"
group = mod.group
base.archivesName.set(mod.name)

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.bawnorton.com/releases/")
    maven("https://maven.shedaniel.me")
    maven("https://jitpack.io")
    maven("https://cursemaven.com")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
}

loom {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }

    runConfigs["client"].apply {
        programArgs("--username=Bawnorton", "--uuid=17c06cab-bf05-4ade-a8d6-ed14aaf70545")
    }

    sourceSets {
        main {
            resources {
                srcDir(project.file("src/main/generated"))
            }
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.release = 17
    }

    processResources {
        val modMetadata = mapOf(
            "description" to mod.description,
            "version" to mod.version,
            "minecraft_dependency" to mod.minecraftDependency,
            "minecraft_version" to minecraftVersion,
            "loader_version" to loader.getVersion()
        )

        inputs.properties(modMetadata)
        filesMatching("META-INF/mods.toml") { expand(modMetadata) }
    }

    withType<AbstractCopyTask> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    clean {
        delete(file(rootProject.file("build")))
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

loader.forge {
    dependencies {
        mappings(loom.layered {
            mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
        })
        forge("net.minecraftforge:forge:$minecraftVersion-${loader.getVersion()}")
        "io.github.llamalad7:mixinextras-forge:0.4.1".let { implementation(it); include(it) }
        "io.github.llamalad7:mixinextras-common:0.4.1".let { compileOnly(it); annotationProcessor(it) }

        modImplementation("curse.maven:tetra-289712:4941337")
        modImplementation("curse.maven:apotheosis-313970:5180227")
    }

    loom {
        forge.mixinConfigs("tetra-apotheosis-fix.mixins.json")
    }
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "bawnorton"
            url = uri("https://maven.bawnorton.com/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "${mod.group}.${mod.id}"
            artifactId = "${mod.id}-$loader"
            version = "${mod.version}+$minecraftVersion"

            from(components["java"])
        }
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    val tag = "$loader-${mod.version}+$minecraftVersion"
    val branch = "main"
    changelog = "[Changelog](https://github.com/Bawnorton/${mod.name}/blob/$branch/CHANGELOG.md)"
    displayName = "${mod.name} ${loader.toString().replaceFirstChar { it.uppercase() }} ${mod.version} for $minecraftVersion"
    type = STABLE
    modLoaders.add(loader.toString())

    github {
        accessToken = providers.gradleProperty("GITHUB_TOKEN")
        repository = "Bawnorton/${mod.name}"
        commitish = branch
        tagName = tag
    }

    modrinth {
        accessToken = providers.gradleProperty("MODRINTH_TOKEN")
        projectId = mod.modrinthProjId
        minecraftVersions.addAll(mod.supportedVersions)
    }

    curseforge {
        accessToken = providers.gradleProperty("CURSEFORGE_TOKEN")
        projectId = mod.curseforgeProjId
        minecraftVersions.addAll(mod.supportedVersions)
    }
}