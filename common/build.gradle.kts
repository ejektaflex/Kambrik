plugins {
    id("multiloader-common-module")
}

repositories {
    mavenLocal()
}

dependencies {
    implementation("io.ejekta.percale:percale-common:${project.property("percale_version")}")
}
