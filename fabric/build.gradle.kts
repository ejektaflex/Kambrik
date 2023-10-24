import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("com.github.johnrengelman.shadow") version libs.versions.shadow
    `maven-publish`
    signing
}

architectury {
    platformSetupLoomIde()
    fabric()
}

// The generated resources directory for the AW.
val generatedResources = file("src/generated/resources")
// The path to the AW file in the common subproject.
val accessWidenerFile = project(":common").file("src/main/resources/kambrik.accesswidener")

loom { accessWidenerPath.set(accessWidenerFile) }

// Mark the AW generated resource directory as a source directory for the resources of the "main" source set.
sourceSets {
    main {
        resources {
            // TODO does this break AW?
            //srcDir(generatedResources)
            srcDir(project(":common").file("src/main/resources"))
        }
    }
}

// Set up various Maven repositories for mod compat.
repositories {
    maven("https://maven.terraformersmc.com/releases") // Modmenu
    maven("https://maven.terraformersmc.com/releases/") // Shedaniel
    mavenLocal() // Kambrik
}

// Please just use current fab loader
configurations.all {
    resolutionStrategy {
        force(libs.fabric.loader)
    }
}

dependencies {
    implementation(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionFabric")) { isTransitive = false }

    // Standard Fabric mod setup.
    modImplementation(libs.bundles.mod.deps.fabric) {
        isTransitive = false
    }
    modImplementation(libs.fabric.api)  {
        exclude("net.fabricmc", "fabric-loader")
    }
    modApi(libs.fabric.adapter)
}

tasks {
    // The AW file is needed in :fabric project resources when the game is run.
    // This task simply copies it.
    val copyAccessWidener by registering(Copy::class) {
        from(accessWidenerFile)
        into(generatedResources)
    }

    processResources {
        // Hook the AW copying to processResources.
        dependsOn(copyAccessWidener)
        // Mark that this task depends on the project version,
        // and should reset when the project version changes.
        inputs.property("version", libs.versions.mod.get())

        // Replace the $version template in fabric.mod.json with the project version.
        filesMatching("fabric.mod.json") {
            expand("version" to libs.versions.mod.get())
        }
    }

}

publishing {
    publications {
        create<MavenPublication>("Kambrik") {
            groupId = "io.ejekta"
            artifactId = "kambrik-fabric"
            version = "${libs.versions.fullversion.get()}.SNAPSHOT.${SimpleDateFormat("YYYY.MMdd.HHmmss").format(Date())}"
            from(components.getByName("java"))
        }
    }
}
