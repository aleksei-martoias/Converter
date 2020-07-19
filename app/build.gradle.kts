import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(Sdk.COMPILE_SDK_VERSION)
    buildToolsVersion = BuildPluginsVersion.BUILD_TOOLS_VERSION

    defaultConfig {
        applicationId = "com.alekseimy.converter"
        minSdkVersion(Sdk.MIN_SDK_VERSION)
        targetSdkVersion(Sdk.TARGET_SDK_VERSION)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    signingConfigs {
        create("release") {
            val properties = loadProperties(rootProject.file("\\signing.properties").path)

            /**
             * file signing.properties with content
             * keyAlias=aliasName
             * ...
             * has to be created in root dir to run release build
             */
            keyAlias = properties["keyAlias"] as String
            keyPassword = properties["keyPassword"] as String
            storeFile = file(properties["storeFilePath"] as String)
            storePassword = properties["storePassword"] as String
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
        }

        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${BuildPluginsVersion.KOTLIN}")

    implementation(Libs.RX_JAVA)
    implementation(Libs.RX_JAVA_ANDROID)
    implementation(Libs.OKHTTP)
    implementation(Libs.GSON)
    implementation(Libs.KOIN)
    implementation(Libs.GLIDE)

    implementation(SupportLibs.ANDROIDX_APPCOMPAT)
    implementation(SupportLibs.ANDROIDX_CORE_KTX)
    implementation(SupportLibs.ANDROIDX_CONSTRAINT_LAYOUT)
    implementation(SupportLibs.ANDROIDX_RECYCLER_VIEW)

    testImplementation(TestingLib.JUNIT)
    testImplementation(TestingLib.MOCKK)

    androidTestImplementation(TestingLib.JUNIT)
    androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_EXT_JUNIT)
    androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_RUNNER)
    androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_RULES)
    androidTestImplementation(AndroidTestingLib.ESPRESSO_CORE)
    androidTestImplementation(AndroidTestingLib.OKHTTP_MOCK)
}
