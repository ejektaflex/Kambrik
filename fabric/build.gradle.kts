plugins {
    `maven-publish`
    signing
}

architectury {
    // Create the IDE launch configurations for this subproject.
    platformSetupLoomIde()
    // Set up Architectury for Fabric.
    fabric()
}

// The files below are for using the access widener for the common project.
// We need to copy the file from common resources to fabric resource
// for Fabric Loader to find it and not crash.

// The generated resources directory for the AW.
val generatedResources = file("src/generated/resources")
// The path to the AW file in the common subproject.
val accessWidenerFile = project(":common").file("src/main/resources/kambrik.accesswidener")

loom { accessWidenerPath.set(accessWidenerFile) }

sourceSets {
    main {
        resources {
            srcDir(generatedResources)
        }
    }
}

// Set up various Maven repositories for mod compat.
repositories {
    maven("https://maven.terraformersmc.com/releases/") // Mod Menu
}

dependencies {
    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }

    bundle(project(path = ":common", configuration = "transformProductionFabric")) {
        isTransitive = false
    }

    // Standard Fabric mod setup.
    modImplementation("net.fabricmc:fabric-loader:0.14.21") {
        exclude("net.fabricmc", "fabric-loader")
    }
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.83.0+1.20") {
        exclude("net.fabricmc", "fabric-loader")
    }
    modApi("net.fabricmc:fabric-language-kotlin:1.9.4+kotlin.1.8.21") {
        exclude("net.fabricmc", "fabric-loader")
    }

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
        inputs.property("version", rootProject.version.toString())

        // Replace the $version template in fabric.mod.json with the project version.
        filesMatching("fabric.mod.json") {
            expand("version" to rootProject.version.toString())
        }
    }
}


publishing {
    publications {
        create<MavenPublication>("Kambrik") {
            groupId = "io.ejekta"
            artifactId = "kambrik-fabric"
            version = rootProject.version.toString()
            from(components.getByName("java"))
        }
    }
}
