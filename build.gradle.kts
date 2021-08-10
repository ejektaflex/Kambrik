import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import net.fabricmc.loom.task.RemapSourcesJarTask
import java.text.SimpleDateFormat
import java.util.Date

plugins {
	//id 'com.github.johnrengelman.shadow' version '6.1.0'
	kotlin("jvm") version "1.5.21"
	// if IR backend error occurs, for some reason toggling the KSX plugin fixes it. -_-
	kotlin("plugin.serialization") version "1.5.20"
	id("fabric-loom") version "0.8-SNAPSHOT"
	`maven-publish`
	signing
}

java {
	sourceCompatibility = JavaVersion.VERSION_16
	targetCompatibility = JavaVersion.VERSION_16
	withSourcesJar()
	withJavadocJar()
}

val modId: String by project
val modVersion: String by project
val group: String by project
val minecraftVersion: String by project
val fabricVersion: String by project
val kotlinVersion: String by project
val loaderVersion: String by project
val yarnMappings: String by project
val pkgName: String by project
val pkgDesc: String by project
val pkgAuthor: String by project
val pkgEmail: String by project
val pkgHub: String by project

val buildVersion = if (modVersion.endsWith("SNAPSHOT")) {
	modVersion + ".${SimpleDateFormat("YYYY.MMdd.HHmmss").format(Date())}"
} else {
	modVersion
}

project.group = group
version = buildVersion

//compileKotlin.kotlinOptions.jvmTarget = "1.8"

repositories {
	mavenCentral()
	maven(url = "https://kotlin.bintray.com/kotlinx")
	maven(url = "https://maven.terraformersmc.com/") {
		name = "Mod Menu"
	}
}

minecraft { }

dependencies {
	//to change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	mappings("net.fabricmc:yarn:${yarnMappings}:v2")
	modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")

	modApi("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.2")
	include("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.2")
	modApi("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
	include("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

	implementation("com.google.code.findbugs:jsr305:3.0.2")

	modImplementation(group = "net.fabricmc", name = "fabric-language-kotlin", version = "1.6.3+kotlin.1.5.21")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")
}

val remapJarTask = tasks.named("remapJar").get()
val javadocJarTask = tasks.getByName("javadocJar")
val sourcesJarTask = tasks.named("sourcesJar").get()
val remapSourcesJarTask = tasks.named("remapSourcesJar").get()

publishing {

	publications {
		create<MavenPublication>("Kambrik") {

			groupId = group
			artifactId = modId
			version = buildVersion

			artifact(remapJarTask) { builtBy(remapJarTask) }
			artifact(sourcesJarTask) { builtBy(remapSourcesJarTask) }
			artifact(javadocJarTask)

			pom {
				name.set(pkgName)
				description.set(pkgDesc)
				url.set(pkgHub)

				licenses {
					license {
						name.set("MIT License")
						url.set("https://opensource.org/licenses/MIT")
					}
				}

				developers {
					developer {
						name.set(pkgAuthor)
						id.set(pkgAuthor)
						email.set(pkgEmail)
					}
				}

				scm {
					connection.set("$pkgHub.git")
					url.set(pkgHub)
				}

			}


		}
	}

	repositories {
		maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2") {
			name = "Central"
			credentials {
				username = property("ossrh.username") as String
				password = property("ossrh.password") as String
			}
		}
		maven("https://maven.pkg.github.com/ejektaflex/kambrik") {
			name = "GitHub"
			credentials {
				username = property("gpr.user") as String
				password = property("gpr.key") as String
			}
		}
	}

}

signing {
	sign(publishing.publications)
}

tasks.getByName<ProcessResources>("processResources") {
	filesMatching("fabric.mod.json") {
		expand(
			mutableMapOf<String, String>(
				"modid" to modId,
				"version" to modVersion,
				"kotlinVersion" to kotlinVersion,
				"fabricApiVersion" to fabricVersion
			)
		)
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "16"
	}
}
