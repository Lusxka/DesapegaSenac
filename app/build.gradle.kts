plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.login"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.login"
        minSdk = 24
        targetSdk = 35
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
        // Mantenha como JavaVersion.VERSION_11 se for o seu padrão.
        // Certifique-se de que o SDK e JVM Target são compatíveis.
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src\\main\\assets", "src\\main\\assets")
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material) // Esta já cobre o Material Design
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Para lidar com permissões de forma mais fácil (Activity KTX) - Versões atualizadas
    implementation("androidx.activity:activity-ktx:1.9.0") // Atualizado para 1.9.0 (última estável)
    implementation("androidx.fragment:fragment-ktx:1.7.1") // Atualizado para 1.7.1 (última estável)

    // Para carregar imagens de forma eficiente (Glide - mantido para MinhaContaActivity)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Para CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Outras dependências que você já tinha:
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2") // Atualizado para 1.3.2 (última estável)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Atualizado para 4.12.0 (última estável)
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // Atualizado para 4.12.0 (última estável)
    implementation("com.google.code.gson:gson:2.10.1") // Atualizado para 2.10.1 (última estável)

    // RE-ADICIONADO: Dependência do Picasso, conforme sua preferência
    implementation("com.squareup.picasso:picasso:2.8")
}