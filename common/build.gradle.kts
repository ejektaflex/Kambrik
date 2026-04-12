plugins {
    id("multiloader-common-module")
}

repositories {
    mavenLocal()
}

dependencies {
    compileOnly("io.ejekta.percale:percale-common-${project.property("minecraft_version")}:${project.property("percale_version")}")
}
