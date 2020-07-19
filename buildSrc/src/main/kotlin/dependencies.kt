object Sdk {
    const val MIN_SDK_VERSION = 24
    const val TARGET_SDK_VERSION = 29
    const val COMPILE_SDK_VERSION = 29
}

object Versions {
    const val RX_JAVA = "3.0.4"
    const val RX_JAVA_ANDROID = "3.0.0"
    const val OKHTTP = "4.7.2"
    const val GSON = "2.8.6"
    const val KOIN = "2.1.5"
    const val GLIDE = "4.11.0"

    const val ANDROIDX_TEST_EXT = "1.1.1"
    const val ANDROIDX_TEST = "1.2.0"

    const val APPCOMPAT = "1.1.0"
    const val CONSTRAINT_LAYOUT = "1.1.3"
    const val RECYCLER_VIEW = "1.1.0"

    const val CORE_KTX = "1.3.0"

    const val ESPRESSO_CORE = "3.2.0"
    const val JUNIT = "4.13"
    const val MOCKK = "1.10.0"
}

object Libs {
    const val RX_JAVA = "io.reactivex.rxjava3:rxjava:${Versions.RX_JAVA}"
    const val RX_JAVA_ANDROID = "io.reactivex.rxjava3:rxandroid:${Versions.RX_JAVA_ANDROID}"
    const val OKHTTP = "com.squareup.okhttp3:okhttp:${Versions.OKHTTP}"
    const val GSON = "com.google.code.gson:gson:${Versions.GSON}"
    const val KOIN = "org.koin:koin-android:${Versions.KOIN}"
    const val GLIDE = "com.github.bumptech.glide:glide:${Versions.GLIDE}"
}

object BuildPluginsVersion {
    const val DETEKT = "1.10.0"
    const val KOTLIN = "1.3.72"
    const val KTLINT = "9.2.1"
    const val BUILD_GRADLE = "3.5.3"
    const val BUILD_TOOLS_VERSION = "29.0.3"
}

object SupportLibs {
    const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
    const val ANDROIDX_CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    const val ANDROIDX_RECYCLER_VIEW = "androidx.recyclerview:recyclerview:${Versions.RECYCLER_VIEW}"
    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
}

object TestingLib {
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val MOCKK = "io.mockk:mockk:${Versions.MOCKK}"
}

object AndroidTestingLib {
    const val ANDROIDX_TEST_RULES = "androidx.test:rules:${Versions.ANDROIDX_TEST}"
    const val ANDROIDX_TEST_RUNNER = "androidx.test:runner:${Versions.ANDROIDX_TEST}"
    const val ANDROIDX_TEST_EXT_JUNIT = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST_EXT}"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
    const val OKHTTP_MOCK = "com.squareup.okhttp3:mockwebserver:${Versions.OKHTTP}"
}