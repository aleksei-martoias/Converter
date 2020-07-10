buildscript {
    val kotlinVersion by rootProject.extra { "1.3.50" }

    repositories {
        google()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath ("com.android.tools.build:gradle:3.5.3")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:<current_version>")
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version("9.2.1")
}

allprojects {
    repositories {
        google()
        jcenter()
        
    }
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}