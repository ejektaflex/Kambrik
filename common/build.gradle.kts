import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    kotlin("plugin.serialization") version "1.6.0"
    `maven-publish`
    signing
}

architectury { common("fabric", "forge") }

loom { accessWidenerPath.set(file("src/main/resources/kambrik.accesswidener")) }

repositories {
    mavenCentral()
}

dependencies {
    // Add dependencies on the required Kotlin modules.
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    modImplementation("net.fabricmc:fabric-loader:0.14.21")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

kotlin {
    jvmToolchain(17)
}


publishing {
    publications {
        create<MavenPublication>("Kambrik") {
            groupId = "io.ejekta"
            artifactId = "kambrik-common"
            version = rootProject.version.toString() + ".SNAPSHOT.${SimpleDateFormat("YYYY.MMdd.HHmmss").format(Date())}"
            from(components.getByName("java"))
        }
    }
}