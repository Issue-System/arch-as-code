import de.undercouch.gradle.tasks.download.Download
import org.gradle.internal.os.OperatingSystem
import org.gradle.internal.os.OperatingSystem.LINUX
import org.gradle.internal.os.OperatingSystem.MAC_OS
import org.gradle.internal.os.OperatingSystem.WINDOWS
import java.nio.file.Paths

buildscript {
    dependencies {
        classpath("de.undercouch:gradle-download-task:4.0.4")
        classpath("com.palantir.graal:gradle-graal:0.6.0")
    }
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}

val graalvmVersion: String by project
val graalvmGithubRepo: String by project
val graalvmDownloadPrefix: String by project
val graalvmDownloadSuffix: String by project
val graalvmJava = "java11"
val platform by lazy {
    when (val os = OperatingSystem.current()!!) {
        LINUX -> "linux"
        WINDOWS -> "windows"
        MAC_OS -> "darwin"
        else -> throw UnsupportedOperationException("${os.name} is not supported")
    }
}
val graalvmFileExt by lazy {
    when (val os = OperatingSystem.current()!!) {
        LINUX, MAC_OS -> "tar.gz"
        WINDOWS -> "zip"
        else -> throw UnsupportedOperationException("${os.name} is not supported")
    }
}
val executableExt by lazy {
    when (val os = OperatingSystem.current()!!) {
        LINUX, MAC_OS -> ""
        WINDOWS -> ".exe"
        else -> throw UnsupportedOperationException("${os.name} is not supported")
    }
}
// same dir as used by com.palantir.graal
val cacheDir = project.findProperty("com.palantir.graal.cache.dir")?.let { Paths.get(it.toString()) }
        ?: project.gradle.gradleUserHomeDir.toPath().resolve("caches").resolve("com.palantir.graal")
val arch by lazy { System.getProperty("os.arch") }
val graalvmDir by lazy { cacheDir.resolve(graalvmVersion) }
val graalvmTargetDir by lazy { cacheDir.resolve(graalvmVersion).resolve("graalvm-ce-${graalvmVersion}") }
val graalvmWithJavaDir by lazy { cacheDir.resolve(graalvmVersion).resolve("graalvm-ce-${graalvmJava}-${graalvmVersion}") }
val graalvmArchiveFile by lazy { graalvmDir.resolve("graalvm-ce-${graalvmVersion}-amd64.${graalvmFileExt}") }
val graalvmDownloadUrl by lazy {
    "https://github.com/graalvm/${graalvmGithubRepo}/releases/download/" +
            "${graalvmDownloadPrefix}${graalvmVersion}${graalvmDownloadSuffix}/" +
            "graalvm-ce-${graalvmJava}-${platform}-amd64-${graalvmVersion}.${graalvmFileExt}"
}
val nativeImageExecutable by lazy { graalvmTargetDir.resolve("bin").resolve("native-image${executableExt}") }
val guExecutable by lazy {
    val guExecName = "gu${executableExt}"
    when (val os = OperatingSystem.current()!!) {
        LINUX, WINDOWS -> graalvmTargetDir.resolve("bin").resolve(guExecName)
        MAC_OS -> graalvmTargetDir.resolve(Paths.get("Contents", "Home", "bin", guExecName))
        else -> throw UnsupportedOperationException("${os.name} is not supported")
    }
}

// This custom implementation is needed due to https://github.com/palantir/gradle-graal/issues/239
task("buildNativeImage") {
    dependsOn("jar")
    doLast {
        if (!file(graalvmArchiveFile).exists()) {
            val graalVmArchive = graalvmArchiveFile.toFile()
            mkdir(graalvmArchiveFile.parent.toString())

            task<Download>("download-task") {
                src(graalvmDownloadUrl)
                dest(graalVmArchive)
            }.download()
        }
        if (!file(graalvmTargetDir).exists()) {
            if (!file(graalvmWithJavaDir).exists()) {
                if (graalvmFileExt == "zip") {
                    project.copy {
                        from(project.zipTree(graalvmArchiveFile))
                        into(graalvmDir)
                    }
                } else if (graalvmFileExt == "tar.gz") {
                    project.exec {
                        executable("tar")
                        args("-xzf", graalvmArchiveFile.toAbsolutePath())
                        workingDir(graalvmDir)
                    }
                }
            }
            file(graalvmWithJavaDir).renameTo(file(graalvmTargetDir))
        }
        if (!file(nativeImageExecutable).exists()) {
            project.exec {
                executable(guExecutable)
                args("install", "native-image")
            }
        }
        val extract = tasks.getByName("nativeImage")
        extract.actions.forEach {
            it.execute(extract)
        }
    }
}
