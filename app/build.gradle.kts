import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Lecture de la clé Google Maps dans local.properties (fichier NON publié)
val proprietesLocales = Properties()
val fichierLocal = rootProject.file("local.properties")
if (fichierLocal.exists()) {
    fichierLocal.inputStream().use { proprietesLocales.load(it) }
}
val cleMaps: String = proprietesLocales.getProperty("MAPS_API_KEY", "CLE_A_CONFIGURER")

android {
    namespace = "com.example.contactsappv3"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.contactsappv3"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // La clé est injectée dans le Manifest à la compilation
        manifestPlaceholders["MAPS_API_KEY"] = cleMaps
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    // SDK Google Maps pour Android
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}