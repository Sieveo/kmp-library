allprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven { url = uri("https://repo.sieveo.com/artifactory/libs-release-local") }
    }
}


//plugins {
//    kotlin("multiplatform") apply false
//    kotlin("plugin.serialization") apply false
//}