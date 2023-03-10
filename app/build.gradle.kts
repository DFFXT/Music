import org.gradle.internal.impldep.com.amazonaws.PredefinedClientConfigurations.defaultConfig

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}
android {
    namespace = "com.music.m"
    compileSdk = 31
    // compileSdkVersion = "android-31"
    // buildToolsVersion "30.0.3"

    defaultConfig {
        applicationId = "com.music.m"
        minSdk = 23
        targetSdk = 31
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
            }
        }
        debug {
            isMinifyEnabled = false // 不进行混淆
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
            manifestPlaceholders["APP_NAME"] = "@string/app_name_dev"
            ndk {
                // "armeabi-v7a"：实体机   x86：模拟器
                abiFilters.add("armeabi-v7a")
                abiFilters.add("x86")
            }
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding.enable = true

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
    implementation("androidx.appcompat:appcompat:1.6.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.recyclerview:recyclerview:1.3.0-alpha01")
    implementation("com.google.android.material:material:1.6.0-beta01")
    // implementation ("org.litepal.android:core:1.6.1"
    implementation("org.litepal.guolindev:core:3.2.3")
    implementation("com.scwang.smartrefresh:SmartRefreshLayout:1.1.2")
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
    kapt("com.github.bumptech.glide:compiler:4.10.0")
    implementation("com.github.open-android:pinyin4j:2.5.0")
    // implementation "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("org.jsoup:jsoup:1.9.2")
    implementation(files("src/main/bin/rhino-1.7.9.jar"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")
    implementation("com.tencent:mmkv:1.0.10")
    implementation("com.google.android:flexbox:1.1.0")
    implementation("com.google.code.gson:gson:2.9.0")

    implementation("com.tencent.bugly:crashreport:3.2.33")
    implementation("me.jessyan:autosize:1.2.1")
    implementation("com.github.DFFXT:PreferenceUtils:1.2")

    implementation("com.mpatric:mp3agic:0.9.1")
    implementation("com.google.android.exoplayer:exoplayer-core:2.14.2")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.14.2")

    /* def room_version = "2.4.3"

     implementation "androidx.room:room-runtime:$room_version"
     annotationProcessor "androidx.room:room-compiler:$room_version"
     // To use Kotlin annotation processing tool (kapt)
     kapt "androidx.room:room-compiler:$room_version"
     // To use Kotlin Symbol Processing (KSP)
     kapt "androidx.room:room-compiler:$room_version"*/
}
