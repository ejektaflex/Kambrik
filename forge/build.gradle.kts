import java.text.SimpleDateFormat
import java.util.*

plugins {
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
        mixinConfig("kambrik.mixins.json")
        accessWidenerPath.set(project(":common").file("src/main/resources/kambrik.accesswidener"))
        convertAccessWideners.set(true)
    }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) { skip() }


repositories {
    // Set up Kotlin for Forge's Maven repository.
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
    mavenCentral()
}

dependencies {
    // Add dependency on Forge. This is mainly used for generating the patched Minecraft jar with Forge classes.
    forge("net.minecraftforge:forge:1.20.1-47.0.19")

    implementation("thedarkcolour:kotlinforforge:4.3.0")

    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }

    bundle(project(path = ":common", configuration = "transformProductionForge")) {
        isTransitive = false
    }

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks {

    processResources {
        // Mark that this task depends on the project version,
        // and should reset when the project version changes.
        inputs.property("version", rootProject.version.toString())

        // Replace the $version template in mods.toml with the project version.
        filesMatching("META-INF/mods.toml") {
            expand("version" to rootProject.version.toString())
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
            version = rootProject.version.toString() + ".SNAPSHOT.${SimpleDateFormat("YYYY.MMdd.HHmmss").format(Date())}"
            from(components.getByName("java"))
        }
    }
}