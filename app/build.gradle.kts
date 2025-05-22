plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.atcumt.kxq"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.atcumt.kxq"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            // Debug 下启用 Mock
            buildConfigField("boolean", "USE_MOCK", "true")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "USE_MOCK", "false")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lint {
        // https://github.com/bumptech/glide/issues/4940
        lintConfig = file("$rootDir/android-lint.xml")
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

configurations.all {
    exclude(group = "org.jetbrains", module = "annotations-java5")
}

dependencies {
    // Markwon - Markdown 渲染库
    implementation ("io.noties.markwon:core:4.6.2")
    implementation ("io.noties.markwon:ext-strikethrough:4.6.2")
    implementation ("io.noties.markwon:ext-tables:4.6.2")
    implementation ("io.noties.markwon:ext-tasklist:4.6.2")
    implementation ("io.noties.markwon:html:4.6.2")
    implementation ("io.noties.markwon:image-glide:4.6.2")
    implementation("io.noties.markwon:inline-parser:4.6.2")

    // Prism4j - 代码语法高亮
    implementation("io.noties.markwon:syntax-highlight:4.6.2")
    implementation("io.noties:prism4j:2.0.0")
    ksp("io.noties:prism4j-bundler:2.0.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.navigation:navigation-compose:2.8.5") // 导航
    implementation(files("libs\\open_sdk_3.5.17.3_r75955a58_lite.jar")) // qq sdk
    implementation("com.squareup.okhttp3:okhttp:4.12.0") //网络请求
    implementation("com.squareup.okio:okio:1.12.0") //网络请求=
    implementation("io.coil-kt:coil-compose:2.6.0") // 图片加载
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.webkit:webkit:1.12.1") // webview
    implementation("androidx.compose.runtime:runtime-livedata")// livedata
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // retrofit
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
    implementation("com.google.dagger:hilt-android:2.49")
    implementation("androidx.security:security-crypto:1.1.0-alpha07")
    ksp("com.google.dagger:hilt-compiler:2.49")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.common) // room
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}