import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("org.jetbrains.kotlin.jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.ksx
    `maven-publish`
    signing
}

architectury { common("fabric", "neoforge") }

loom {
    accessWidenerPath.set(file("src/main/resources/kambrik.accesswidener"))
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.terraformersmc.com/releases/") // Shedaniel
}

dependencies {
    // Add dependencies on the required Kotlin modules.
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    modImplementation(libs.bundles.mod.deps.common)
}

kotlin {
    jvmToolchain(17)
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("Kambrik") {
            groupId = "io.ejekta"
            artifactId = "kambrik-common"
            version = "${libs.versions.fullversion.get()}.SNAPSHOT.${SimpleDateFormat("YYYY.MMdd.HHmmss").format(Date())}"
            from(components.getByName("java"))
        }
    }
}