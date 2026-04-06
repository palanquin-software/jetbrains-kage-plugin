import org.jetbrains.grammarkit.tasks.GenerateLexerTask

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.grammarKit)
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(providers.gradleProperty("platformVersion"))
    }
}

intellijPlatform {
    pluginVerification {
        ides {
            recommended()
        }
    }

    pluginConfiguration {
        id = "com.palanquinsoftware.kage"
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")

        vendor {
            name = "Palanquin Software"
            url = "https://palanquinsoftware.com"
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
        }
    }
}

val generateKageLexer by tasks.registering(GenerateLexerTask::class) {
    sourceFile.set(layout.projectDirectory.file("src/main/flex/Kage.flex"))
    targetOutputDir.set(layout.buildDirectory.dir("generated/sources/flex/com/palanquinsoftware/kage"))
    purgeOldFiles.set(true)
}

tasks.named("compileKotlin") {
    dependsOn(generateKageLexer)
}

sourceSets.main {
    java.srcDir(layout.buildDirectory.dir("generated/sources/flex"))
}
