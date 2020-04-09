import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "com.bilalekrem.healthcheck"
version = "1.0-SNAPSHOT"

val ktor_version = "1.3.2"
val logback_version = "1.2.3"
val junit_version = "4.12"

plugins {
    application
    kotlin("jvm") version "1.3.71"

    id("com.github.johnrengelman.shadow") version "5.2.0"
}

application {
    mainClassName = "com.bilalekrem.healthcheck.MainKt"
}

tasks {
    withType<ShadowJar> {
        baseName = "healthcheck-api"
        classifier = ""
        version = ""
    }
}

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    maven(url = "https://dl.bintray.com/kotlin/kotlin-dev")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(group="com.bilalekrem.healthcheck", name= "healthcheck-core", version ="1.0-SNAPSHOT")
    compile("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    compile("ch.qos.logback:logback-classic:$logback_version")

    testCompile(group = "junit", name = "junit", version = "4.12")
}

