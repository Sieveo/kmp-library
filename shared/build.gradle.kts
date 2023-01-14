import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests

plugins {
    kotlin("multiplatform") version "1.8.0"
}

group = "com.sieveo.experiments"
version = "1.0-SNAPSHOT"
val artifact = "multiplatform"
val kmpLibraryName = "popcorn"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvm {
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

//    js(IR) {
//        browser {
//            commonWebpackConfig {
//                cssSupport {
//                    enabled.set(true)
//                }
//            }
//        }
//    }


    configureNativeLibrary(kmpLibraryName)

    sourceSets {
        val ktor_version = "2.2.1"

        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-server-websockets:$ktor_version")
                implementation("io.ktor:ktor-server-cio:$ktor_version")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:2.2.1")
            }
        }
        val jvmTest by getting

//        val jsMain by getting
//        val jsTest by getting

        //the sourceSet here ```native``` is based on the default set in the ```configureNativeLibrary``` function
        val nativeMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.2.1")
            }
        }
        val nativeTest by getting
    }
}

fun KotlinMultiplatformExtension.configureNativeLibrary(
    unixLibraryName: String, windowsLibraryName: String = "lib$unixLibraryName"
) {
    val hostOs = System.getProperty("os.name")

    when {
        hostOs == "Mac OS X" -> sharedLibConfigurationMac(unixLibraryName)
        hostOs == "Linux" -> sharedLibConfigurationLinux(unixLibraryName)
        hostOs.startsWith("Windows") -> sharedLibConfigurationWindows(windowsLibraryName)
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }
}

fun KotlinMultiplatformExtension.sharedLibConfigurationMac(
    libraryName: String,
    sourceSetName: String = "native",
    configuration: KotlinNativeTarget.() -> Unit = { binaries { sharedLib { baseName = libraryName } } }
): KotlinNativeTargetWithHostTests {
    val arch = System.getProperty("os.arch")
    return if (arch == "aarch64") {
        macosArm64(sourceSetName, configuration)
    } else {
        macosX64(sourceSetName, configuration)
    }
}

fun KotlinMultiplatformExtension.sharedLibConfigurationLinux(
    libraryName: String,
    sourceSetName: String = "native",
    configuration: KotlinNativeTarget.() -> Unit = { binaries { sharedLib { baseName = libraryName } } }
): KotlinNativeTarget {
    val arch = System.getProperty("os.arch")
    return if (arch == "aarch64") {
        linuxArm64(sourceSetName, configuration)
    } else {
        linuxX64(sourceSetName, configuration)
    }
}

fun KotlinMultiplatformExtension.sharedLibConfigurationWindows(
    libraryName: String,
    sourceSetName: String = "native",
    configuration: KotlinNativeTarget.() -> Unit = { binaries { sharedLib { baseName = libraryName } } }
): KotlinNativeTargetWithHostTests {
    return mingwX64(sourceSetName, configuration)
}

tasks.register<Copy>("compileAndCopyDebugNativeLibsToCppApp") {
    from(layout.buildDirectory.file("bin/native/debugShared/"))
    include("*.so", "*.dylib", "*.dll")
    into(File(projectDir, "cpp_app/libs"))

    dependsOn(":linkDebugSharedNative", ":copyDebugNativeHeadersToCppApp")
}

tasks.register<Copy>("compileAndCopyReleaseNativeLibsToCppApp") {
    from(layout.buildDirectory.file("bin/native/releaseShared/"))
    include("*.so", "*.dylib", "*.dll")
    into(File(projectDir, "cpp_app/libs"))

    dependsOn(":linkReleaseSharedNative", ":copyReleaseNativeHeadersToCppApp")
}

tasks.register<Copy>("copyDebugNativeHeadersToCppApp") {
    from(layout.buildDirectory.file("bin/native/debugShared/"))
    include("*.h")
    into(File(projectDir, "cpp_app/headers"))

    dependsOn(":linkDebugSharedNative")
}

tasks.register<Copy>("copyReleaseNativeHeadersToCppApp") {
    from(layout.buildDirectory.file("bin/native/releaseShared/"))
    include("*.h")
    into(File(projectDir, "cpp_app/headers"))

    dependsOn(":linkDebugSharedNative")
}
