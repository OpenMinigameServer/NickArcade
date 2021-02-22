import io.github.openminigameserver.arcadiumgradle.oms
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.github.openminigameserver.arcadiumgradle") version "1.0-SNAPSHOT"
}

group = "io.github.openminigameserver"
version = "1.0-SNAPSHOT"

spigot {
    name = "NickArcade"
    authors("NickAc")
    apiVersion = "1.16"
}

nickarcade {
    arcadiumVersion = "1.16.5-R0.1-SNAPSHOT"
    isCoreProject = true
}

val cloudVersion = "1.4.0"
val configurateVersion = "4.0.0"
val kMongoVersion = "4.2.3"
val coroutinesVersion = "1.4.2"
val daggerVersion = "2.32"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")

    //Hypixel API Client
    api(oms("HypixelApiClient"))

    //Jetbrains Annotations
    compileOnly("org.jetbrains:annotations:20.1.0")

    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")

    //Cloud - Commands
    implementation("cloud.commandframework:cloud-paper:$cloudVersion")
    implementation("cloud.commandframework:cloud-annotations:$cloudVersion")
    implementation("cloud.commandframework:cloud-kotlin-extensions:$cloudVersion")

    //Configurations
    implementation("org.spongepowered:configurate-yaml:$configurateVersion")
    implementation("org.spongepowered:configurate-extra-kotlin:$configurateVersion")

    //Database I/O
    implementation("org.litote.kmongo:kmongo-coroutine:$kMongoVersion")
}