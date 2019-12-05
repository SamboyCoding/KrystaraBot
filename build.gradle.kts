plugins {
    java
    kotlin("jvm") version "1.3.61"
}

group = "me.samboycoding"
version = "2.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")

    //Jda
    implementation("net.dv8tion:JDA:4.0.0_46")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha0")

    //Reactive Jda
    implementation("club.minnced:jda-reactor:0.2.7")

    //GSON
    implementation("com.google.code.gson:gson:2.8.6")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.6.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}