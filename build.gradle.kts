import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.text.SimpleDateFormat
import java.util.Date
import java.net.URL

plugins {
	kotlin("jvm") version "1.6.0"
	kotlin("plugin.serialization") version "1.6.0"
	id("fabric-loom") version "0.10-SNAPSHOT"
	`maven-publish`
	signing
	`idea`
	id("org.jetbrains.dokka") version "1.5.30"
}

object Versions {
	const val Minecraft = "1.18-pre2"
	object Jvm {
		val Java = JavaVersion.VERSION_17
		const val Kotlin = "1.6.0"
		const val TargetKotlin = "17"
	}
	object Fabric {
		const val Yarn = "1.18-pre2+build.1"
		const val Loader = "0.12.5"
		const val Api = "0.42.4+1.18"
	}
	object Mod {
		const val Group = "io.ejekta"
		const val ID = "kambrik"
		const val Version = "2.0.0"
	}
	object Env {
		const val Serialization = "1.3.0"
		const val Kambrik = "3.+"
		const val FLK = "1.6.5+kotlin.1.5.31"
		const val ClothConfig = "6.0.42"
		const val ModMenu = "2.0.6"
	}
}


java {
	sourceCompatibility = Versions.Jvm.Java
	targetCompatibility = Versions.Jvm.Java
	withSourcesJar()
	withJavadocJar()
}

val pkgName: String by project
val pkgDesc: String by project
val pkgAuthor: String by project
val pkgEmail: String by project
val pkgHub: String by project

val buildVersion = if (Versions.Mod.Version.endsWith("SNAPSHOT")) {
	Versions.Mod.Version + ".${SimpleDateFormat("YYYY.MMdd.HHmmss").format(Date())}"
} else {
	Versions.Mod.Version
}

project.group = group
version = buildVersion

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
	minecraft("com.mojang:minecraft:${Versions.Minecraft}")
	mappings("net.fabricmc:yarn:${Versions.Fabric.Yarn}:v2")
	modImplementation("net.fabricmc:fabric-loader:${Versions.Fabric.Loader}")

	modApi("org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.Env.Serialization}")
	include("org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.Env.Serialization}")
	modApi("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.Env.Serialization}")
	include("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.Env.Serialization}")

	compileOnly("org.jetbrains:annotations:22.0.0")
	implementation("com.google.code.findbugs:jsr305:3.0.2")

	modImplementation(group = "net.fabricmc", name = "fabric-language-kotlin", version = "1.6.4+kotlin.1.5.30")

	modImplementation("net.fabricmc.fabric-api:fabric-api:${Versions.Fabric.Api}")
}

val remapJarTask = tasks.named("remapJar").get()
val javadocJarTask = tasks.getByName("javadocJar")
val sourcesJarTask = tasks.named("sourcesJar").get()
val remapSourcesJarTask = tasks.named("remapSourcesJar").get()

publishing {

	publications {
		create<MavenPublication>("Kambrik") {

			groupId = Versions.Mod.Group
			artifactId = Versions.Mod.ID
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
				username = property("ossrh.username") as? String
				password = property("ossrh.password") as? String
			}
		}
		maven("https://maven.pkg.github.com/ejektaflex/kambrik") {
			name = "GitHub"
			credentials {
				username = property("gpr.user") as? String
				password = property("gpr.key") as? String
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
				"modid" to Versions.Mod.ID,
				"version" to Versions.Mod.Version,
				"kotlinVersion" to Versions.Jvm.Kotlin,
				"fabricApiVersion" to Versions.Fabric.Api
			)
		)
	}
}

tasks.withType<JavaCompile> {
	//this.
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "17"
	}
}

tasks.dokkaHtml.configure {
	outputDirectory.set(buildDir.resolve("dokka"))

	moduleName.set("KambrikDokka")

	dokkaSourceSets {
		configureEach {
			skipEmptyPackages.set(true)
			sourceLink {
				localDirectory.set(file("src/main/java"))

				noJdkLink.set(true)

				remoteUrl.set(
					URL(
						"https://github.com/ejektaflex/Kambrik/tree/master/src/main/java"
					)
				)
				remoteLineSuffix.set("#L")
			}
		}
	}

}
