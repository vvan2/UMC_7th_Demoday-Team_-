import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
}

val properties =
    Properties().apply {
        load(project.rootProject.file("local.properties").inputStream())
    }

android {
    namespace = "com.example.umc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.umc"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = false
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.protolite.well.known.types)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // splash
    implementation ("androidx.core:core-splashscreen:1.0.1")


    // Material Calendar
    implementation(libs.material.calendarview)
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")

    // Threetenabp
    implementation(libs.threetenabp)

    // DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.android.compiler)

    // Timber
    implementation(libs.timber)

    // Glide
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // coil
    implementation(libs.coil)

    // ViewPager2
    implementation(libs.androidx.viewpager2)

    // navigation
    implementation(libs.bundles.navigation)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation(libs.kotlinx.coroutines)

    // Network
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.retrofit)

    // Mp android chart
    implementation(libs.mpandroidchart)

    // API + retrofit 의존성주입
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0") // JSON 변환 라이브러리 추가

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation ("com.google.android.material:material:1.11.0")


    implementation ("androidx.browser:browser:1.5.0") // 최신 버전 사용 가능


    // Lottie
    implementation ("com.airbnb.android:lottie:5.0.2")

    //tooltip
    implementation ("com.github.skydoves:balloon:1.4.6")

    //implementation ("com.kakao.sdk:v2-user:2.14.0")  // 카카오 로그인 SDK

    implementation ("com.navercorp.nid:oauth:5.2.0")  // 네이버 로그인 SDK 추가




}