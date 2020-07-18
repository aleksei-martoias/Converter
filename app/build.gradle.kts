plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(Sdk.COMPILE_SDK_VERSION)
    buildToolsVersion = "29.0.3"

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

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
            isMinifyEnabled = false
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

    implementation(SupportLibs.ANDROIDX_APPCOMPAT)
    implementation(SupportLibs.ANDROIDX_CORE_KTX)
    implementation(SupportLibs.ANDROIDX_CONSTRAINT_LAYOUT)
    implementation(SupportLibs.ANDROIDX_RECYCLER_VIEW)

    testImplementation(TestingLib.JUNIT)
    testImplementation(TestingLib.MOCKK)

    testImplementation(AndroidTestingLib.ANDROIDX_TEST_EXT_JUNIT)
    testImplementation(AndroidTestingLib.ANDROIDX_TEST_RUNNER)
    testImplementation(AndroidTestingLib.ANDROIDX_TEST_RULES)
    testImplementation(AndroidTestingLib.ESPRESSO_CORE)
}
