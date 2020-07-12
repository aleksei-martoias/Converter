plugins {
    id("org.jlleitschuh.gradle.ktlint") version BuildPluginsVersion.KTLINT
    id("io.gitlab.arturbosch.detekt") version BuildPluginsVersion.DETEKT
}

buildscript {
    repositories {
        google()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
        maven("https://oss.jfrog.org/libs-snapshot")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${BuildPluginsVersion.BUILD_GRADLE}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${BuildPluginsVersion.KOTLIN}")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:${BuildPluginsVersion.KTLINT}")
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${BuildPluginsVersion.DETEKT}")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

subprojects {
    apply {
        plugin("org.jlleitschuh.gradle.ktlint")
        plugin("io.gitlab.arturbosch.detekt")
    }

    detekt {
        config = rootProject.files("config/detekt/detekt.yml")

        reports {
            txt.enabled = false
            xml.enabled = false
            html {
                enabled = true
                destination = file("build/reports/detekt/detekt.html")
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}