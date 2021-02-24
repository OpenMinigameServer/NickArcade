import io.github.openminigameserver.arcadiumgradle.oms

plugins {
    id("io.github.openminigameserver.arcadiumgradle") version "1.0-SNAPSHOT"
}

nickarcade {
    isCoreProject = true
    arcadiumVersion = "1.16.5-R0.1-SNAPSHOT"
}

val cloudVersion = "1.4.0"
val configurateVersion = "4.0.0"
val kMongoVersion = "4.2.3"
val coroutinesVersion = "1.4.2"
val adventureVersion = "4.0.0-SNAPSHOT"

dependencies {
    //Hypixel API Client
    api(oms("HypixelApiClient"))

    //Jetbrains Annotations
    compileOnly("org.jetbrains:annotations:20.1.0")

    //Cloud - Commands
    api("cloud.commandframework:cloud-paper:$cloudVersion")
    api("cloud.commandframework:cloud-annotations:$cloudVersion")
    api("cloud.commandframework:cloud-kotlin-extensions:$cloudVersion")

    //Configurations
    api("org.spongepowered:configurate-yaml:$configurateVersion")
    api("org.spongepowered:configurate-extra-kotlin:$configurateVersion")

    //Database I/O
    api("org.litote.kmongo:kmongo-coroutine:$kMongoVersion")
}