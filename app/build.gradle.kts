import org.gradle.internal.impldep.com.amazonaws.PredefinedClientConfigurations.defaultConfig

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.compose")
}
android {
    namespace = "com.music.m"
    compileSdk = 35
    // compileSdkVersion = "android-31"
    // buildToolsVersion "30.0.3"

    defaultConfig {
        applicationId = "com.music.m"
        minSdk = 23
        targetSdk = 35
        // testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release").apply {
            this.keyAlias = "key0"
            // keyAlias = "key0"
            this.keyPassword = ("542517")
            this.storeFile = (file("sign.jks"))
            this.storePassword = ("542517")
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false // 不进行混淆
            // applicationIdSuffix(".release")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
            signingConfig = signingConfigs.getByName("release")
            // signingConfig =signingConfigs.release
            // **manifest占位符
            manifestPlaceholders["APP_NAME"] = "@string/app_name"
            // manifestPlaceholders = [APP_NAME:"@string/app_name"]
            ndk {
                abiFilters.add("armeabi-v7a")
                abiFilters.add("arm64-v8a")
            }
        }
        debug {
            isMinifyEnabled = false // 不进行混淆
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
            manifestPlaceholders["APP_NAME"] = "@string/app_name_dev"
            ndk {
                // "armeabi-v7a"：实体机   x86：模拟器
                abiFilters.add("armeabi-v7a")
                abiFilters.add("arm64-v8a")
                abiFilters.add("x86")
            }
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }

    compileOptions {
        targetCompatibility(1.8)
        sourceCompatibility(1.8)
    }
    sourceSets {

        getByName("main").res {
            val file: Array<File> = File(projectDir.path + "/src/main/next_res").listFiles()
            this.srcDirs(file)
        }
    }
    buildOutputs.all {
        System.out.println("->"+this.outputFile.absolutePath)
    }
}

dependencies {
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.google.android.material:material:1.12.0")
    // implementation ("org.litepal.android:core:1.6.1"
    implementation("com.github.guolindev:LitePal:8ad8322cc6")
    // implementation("com.scwang.smartrefresh:SmartRefreshLayout:1.1.2")
    implementation("io.github.scwang90:refresh-layout-kernel:3.0.0-alpha")      //核心必须依赖
    implementation("io.github.scwang90:refresh-header-classics:3.0.0-alpha")    //经典刷新头
    //implementation("io.github.scwang90:refresh-header-radar:3.0.0-alpha")       //雷达刷新头
    //implementation("io.github.scwang90:refresh-header-falsify:3.0.0-alpha")     //虚拟刷新头
    //implementation("io.github.scwang90:refresh-header-material:3.0.0-alpha")    //谷歌刷新头
    //implementation("io.github.scwang90:refresh-header-two-level:3.0.0-alpha")   //二级刷新头
    //implementation("io.github.scwang90:refresh-footer-ball:3.0.0-alpha")        //球脉冲加载
    implementation("io.github.scwang90:refresh-footer-classics:3.0.0-alpha")    //经典加载
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    implementation("androidx.paging:paging-runtime-ktx:2.1.2")
    // implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:retrofit-converters:2.4.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.7.2")
    implementation("io.reactivex.rxjava2:rxjava:2.2.14")
    implementation("com.alibaba:fastjson:1.2.73")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    //kapt("com.github.bumptech.glide:compiler:4.10.0")
    implementation("com.github.open-android:pinyin4j:2.5.0")
    // implementation "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("org.jsoup:jsoup:1.9.2")
    implementation(files("src/main/bin/rhino-1.7.9.jar"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    implementation("com.tencent:mmkv:2.2.1")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("com.tencent.bugly:crashreport:4.1.9.3")
    implementation("com.github.JessYanCoding:AndroidAutoSize:v1.2.1")
    implementation("com.github.DFFXT:PreferenceUtils:1.2")

    implementation("com.mpatric:mp3agic:0.9.1")
    implementation("com.google.android.exoplayer:exoplayer-core:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")
    implementation("com.github.DFFXT.SkinSwitch:SkinCore:0.22.6")
    debugImplementation("com.github.DFFXT.SkinSwitch:ViewDebug:0.22.6")

    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.8.3")

    /* def room_version = "2.4.3"

     implementation "androidx.room:room-runtime:$room_version"
     annotationProcessor "androidx.room:room-compiler:$room_version"
     // To use Kotlin annotation processing tool (kapt)
     kapt "androidx.room:room-compiler:$room_version"
     // To use Kotlin Symbol Processing (KSP)
     kapt "androidx.room:room-compiler:$room_version"*/
}
