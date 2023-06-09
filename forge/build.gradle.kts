plugins {
    `maven-publish`
    signing
}

object Versions {
    val Mod = "6.0.1"
    val MC = "1.20"
    val Yarn = "1.20+build.1"
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
    forge("net.minecraftforge:forge:${Versions.MC}-46.0.1")

    implementation("thedarkcolour:kotlinforforge:4.1.0")

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
            version = rootProject.version.toString()
            from(components.getByName("java"))
        }
    }
}