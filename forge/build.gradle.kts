import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("com.github.johnrengelman.shadow") version libs.versions.shadow
    `maven-publish`
    signing
}

architectury {
    // Create the IDE launch configurations for this subproject.
    platformSetupLoomIde()
    // Set up Architectury for Forge.
    forge()
}

loom {
    forge {
        //mixinConfig("bountiful.mixins.json")
        accessWidenerPath.set(project(":common").file("src/main/resources/kambrik.accesswidener"))
        convertAccessWideners.set(true)
    }
}

repositories {
    // Set up Kotlin for Forge's Maven repository.
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
    maven("https://maven.shedaniel.me/") // Cloth Config
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Add dependency on Forge. This is mainly used for generating the patched Minecraft jar with Forge classes.
    forge(libs.forge)

    // Add Kotlin for Forge.
    // Based on their own instructions: https://github.com/thedarkcolour/KotlinForForge/blob/70385f5/thedarkcolour/kotlinforforge/gradle/kff-3.0.0.gradle
    implementation(libs.forge.adapter)

    "developmentForge"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }

    // Bundle the transformed version of the common project in the mod.
    // The transformed version includes things like fixed refmaps.
    shadowCommon(project(path = ":common", configuration = "transformProductionForge")) { isTransitive = false }

    //implementation("org.ow2.asm:asm-tree:9.4")

    modImplementation(libs.mod.dep.cloth.config.forge)
}

tasks {

    processResources {
        // Mark that this task depends on the project version,
        // and should reset when the project version changes.
        inputs.property("version", libs.bundles.mod.toString())

        // Replace the $version template in mods.toml with the project version.
        filesMatching("META-INF/mods.toml") {
            expand("version" to libs.bundles.mod.toString())
        }
    }
}
kotlin {
    jvmToolchain(17)
}



publishing {
    publications {
        create<MavenPublication>("mavenForge") {
            groupId = "io.ejekta"
            artifactId = "kambrik-forge"
            version = libs.bundles.mod.toString() + ".SNAPSHOT.${SimpleDateFormat("YYYY.MMdd.HHmmss").format(Date())}"
            from(components.getByName("java"))
        }
    }
}