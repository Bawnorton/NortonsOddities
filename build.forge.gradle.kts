import integrationfixes.utils.*

plugins {
  kotlin("jvm")
  `maven-publish`
  id("net.neoforged.moddev.legacyforge")
  id("integrationfixes.common")
  id("me.modmuss50.mod-publish-plugin")
  id("dev.isxander.secrets") version "0.1.0"
}

repositories {
  fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
    forRepository { maven(url) { name = alias } }
    filter { groups.forEach(::includeGroup) }
  }

  maven("https://maven.minecraftforge.net")
  maven("https://maven.bawnorton.com/releases")
  maven("https://maven.parchmentmc.org")
  maven("https://maven.saps.dev/minecraft")
  maven("https://maven.architectury.dev")
  maven("https://maven.blamejared.com/")
  maven("https://maven.terraformersmc.com/")
  maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
  maven("https://maven.kosmx.dev/")
  maven("https://code.redspace.io/releases")

  strictMaven("https://www.cursemaven.com", "Curseforge", "curse.maven")
  strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")

}

val minecraft: String by project
val loader: String by project

base.archivesName = "${mod("id")}-${mod("version")}+$minecraft-$loader"

dependencies {
  implementation(annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.3")!!)
  jarJar(implementation("io.github.llamalad7:mixinextras-forge:0.5.3")!!)

  implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:0.3.7-beta.1")!!)
  jarJar(implementation("com.github.bawnorton.mixinsquared:mixinsquared-forge:0.3.7-beta.1")!!)

  annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

  modImplementation("curse.maven:eidolon-repraised-870250:5877358")
  modRuntimeOnly("curse.maven:curios-309927:6418456")

  modCompileOnly("dev.emi:emi-forge:${deps("emi")}:api")
  modImplementation("dev.emi:emi-forge:${deps("emi")}")

  modCompileOnly("mezz.jei:jei-${minecraft}-forge-api:${deps("jei")}")
  modCompileOnly("mezz.jei:jei-${minecraft}-common-api:${deps("jei")}")
  modRuntimeOnly("mezz.jei:jei-${minecraft}-forge:${deps("jei")}")

  modCompileOnly("io.redspace.ironsspellbooks:irons_spellbooks:${minecraft}-3.4.0.9")
  modCompileOnly("curse.maven:to-tweaks-irons-spells-1046916:6322784")
  modRuntimeOnly("software.bernie.geckolib:geckolib-forge-${minecraft}:${deps("geckolib")}")
  modRuntimeOnly("com.eliotlash.mclib:mclib:20")
  modRuntimeOnly("dev.kosmx.player-anim:player-animation-lib-forge:${deps("player_anim")}")

  modCompileOnly("curse.maven:item-obliterator-835861:5479898")
}

java {
  withSourcesJar()
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

legacyForge {
  version = deps("forge")

  validateAccessTransformers = true
  accessTransformers.from(rootProject.file("src/main/resources/$minecraft-accesstransformer.cfg"))

  mods {
    register(mod("id")!!) {
      sourceSet(sourceSets["main"])
    }
  }

  deps("parchment") {
    parchment {
      val (mc, version) = it.split(':')
      mappingsVersion = version
      minecraftVersion = mc
    }
  }

  runs {
    all {
      gameDirectory = rootProject.file("run")
    }

    register("client") {
      ideName = "Forge Client $minecraft"
      client()

      programArgument("--username=Bawnorton")
      programArgument("--uuid=17c06cab-bf05-4ade-a8d6-ed14aaf70545")

      systemProperty("terminal.ansi", "true")
    }

    register("server") {
      ideName = "Forge Server $minecraft"
      server()

      systemProperty("terminal.ansi", "true")
    }
  }

  afterEvaluate {
    runs.configureEach {
      applyMixinDebugSettings(::jvmArgument, ::systemProperty)
    }
  }
}

mixin {
  add(sourceSets.main.get(), "${mod("id")}.refmap.json")
  config("${mod("id")}.mixins.json")
}

sourceSets.main {
  resources.srcDirs(project.file("src/main/generated/server"), project.file("src/main/generated/client"))
  resources.exclude(".cache")
}

tasks {
  named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
  }

  register<Copy>("buildAndCollect") {
    group = "build"
    from(named<Jar>("reobfJar").map { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${mod("version")}"))
    dependsOn("build")
  }

  named<Jar>("jar") {
    manifest {
      attributes(
        "Specification-Title" to mod("name")!!,
        "Specification-Vendor" to "${mod("author")}",
        "Specification-Version" to "1",
        "Implementation-Title" to project.name,
        "Implementation-Version" to mod("version")!!,
        "Implementation-Vendor" to "${mod("author")}",
        "MixinConfigs" to listOf(
          "${mod("id")}.mixins.json"
        ).joinToString(", ")
      )
    }
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
      groupId = "${mod("group")}.${mod("id")}"
      artifactId = "${mod("id")}-$loader"
      version = "${mod("version")}+$minecraft"

      from(components["java"])
    }
  }
}

publishMods {
  val cfTokenProvider = onePassword["op://Private/Curseforge API Key/credential"]

  type = STABLE
  file = tasks.named<Jar>("reobfJar").map { it.archiveFile.get() }
  additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })

  displayName = "${mod("name")} Forge ${mod("version")} for $minecraft"
  version = mod("version")
  changelog = provider { rootProject.file("CHANGELOG.md").readText() }
  modLoaders.add(loader)

  val compatibleVersionString = mod("compatible_versions")!!
  val compatibleVersions = compatibleVersionString.split(",").map { it.trim() }

  curseforge {
    projectId = property("publishing.curseforge") as String
    accessToken = cfTokenProvider.get()
    minecraftVersions.addAll(compatibleVersions)
  }
}