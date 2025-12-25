plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.rosan.hidden_api"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24
        lint.targetSdk = 36
    }

    buildFeatures.aidl = true
    buildFeatures.buildConfig = false
}

dependencies {
    implementation(libs.annotation)

    annotationProcessor(libs.rikka.refine.annotation.processor)
    compileOnly(libs.rikka.refine.annotation)
}