import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import net.fabricmc.loom.task.RemapSourcesJarTask

plugins {
	//id 'com.github.johnrengelman.shadow' version '6.1.0'
	kotlin("jvm") version "1.4.30"
	kotlin("plugin.serialization") version "1.4.30"
	id("fabric-loom") version "0.6-SNAPSHOT"
	`maven-publish`
	signing
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
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

project.group = group
version = modVersion

//compileKotlin.kotlinOptions.jvmTarget = "1.8"

repositories {
	mavenCentral()
	jcenter()
	maven(url = "https://kotlin.bintray.com/kotlinx")
	maven(url = "http://maven.fabricmc.net/") {
		name = "Fabric"
	}
	maven(url = "https://maven.terraformersmc.com/") {
		name = "Mod Menu"
	}
}

minecraft {

}

dependencies {
	//to change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	mappings("net.fabricmc:yarn:${yarnMappings}:v2")
	modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")

	modApi("org.jetbrains.kotlinx:kotlinx-serialization-core:1.1.0")
	include("org.jetbrains.kotlinx:kotlinx-serialization-core:1.1.0")
	modApi("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
	include("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")

	implementation("com.google.code.findbugs:jsr305:3.0.2")

	modApi("com.terraformersmc:modmenu:1.16.8") {
		exclude(module = "fabric-api")
		exclude(module = "config-2")
	}

	modImplementation(group = "net.fabricmc", name = "fabric-language-kotlin", version = "1.4.30+build.2")

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
			version = modVersion

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
		jvmTarget = "1.8"
	}
}
