import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.changelog.Changelog
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.gradleIntellijPlugin)
    alias(libs.plugins.grammerKit)
    alias(libs.plugins.changelog)
    alias(libs.plugins.gitVersion)
}

val gitDetails = versionDetails()
version = if (gitDetails.isCleanTag) {
    gitDetails.version
} else {
    // eg. 0.1.4-dev.12345678
    providers.gradleProperty("intellijGnVersion").get().ifEmpty {
        throw IllegalStateException("intellijGnVersion must be set in gradle.properties")
    } + "-dev." + gitDetails.gitHash
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.kotlinTest)
    testImplementation(libs.kotlinTestJdk7)
    testImplementation(platform(libs.junitBom))
    testRuntimeOnly(libs.junitPlatformLauncher) {
        because("Only needed to run tests in a version of IntelliJ IDEA that bundles older versions")
    }
    testRuntimeOnly(libs.junitJupiterEngine)
    testRuntimeOnly(libs.jnintVintageEngine)
}

grammarKit {
    grammarKitRelease.set("2021.1.2")
}

task("generateLexerTask", GenerateLexerTask::class) {
    // source flex file
    sourceFile.set(file("src/grammar/gn.flex"))

    // target directory for lexer
    targetOutputDir = project.layout.projectDirectory.dir("src/gen/com/google/idea/gn")

    // if set, plugin will remove a lexer output file before generating new one. Default: false
    purgeOldFiles = true
}

task("generateParserTask", GenerateParserTask::class) {
    // source bnf file
    sourceFile.set(file("src/grammar/gn.bnf"))

    // optional, task-specific root for the generated files. Default: none
    targetRootOutputDir = project.layout.projectDirectory.dir("src/gen")

    // path to a parser file, relative to the targetRoot
    pathToParser = "/com/google/idea/gn/parser/GnParser.java"

    // path to a directory with generated psi files, relative to the targetRoot
    pathToPsiRoot = "/com/google/idea/gn/psi"

    // if set, plugin will remove a parser output file and psi output directory before generating new ones. Default: false
    purgeOldFiles = true
}

intellij {
    version = "2022.3"
    sandboxDir = "tmp/sandbox"
}

changelog {
    groups.empty()
}

tasks.named("compileKotlin") {
    setDependsOn(listOf(tasks.named("generateLexerTask"), tasks.named("generateParserTask")))
}

tasks.withType<JavaCompile> {
    val enableWarningAsError = project.findProperty("enableWarningAsError")?.toString()?.toBoolean() ?: false
    if (enableWarningAsError) {
        var compilerArgs = options.compilerArgs
        if (compilerArgs == null) {
            compilerArgs = mutableListOf()
        }
        compilerArgs.add("-Werror")
        options.compilerArgs = compilerArgs
    }
}

tasks.withType<KotlinJvmCompile> {
    val enableWarningAsError = project.findProperty("enableWarningAsError")?.toString()?.toBoolean() ?: false
    kotlinOptions.jvmTarget = "17"
    if (enableWarningAsError) {
        kotlinOptions.allWarningsAsErrors = true
    }
}

sourceSets {
    main {
        java {
            srcDir("src/gen")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val intellijSinceBuild = "223"
val intellijUntilBuild = ""

tasks.patchPluginXml {
    sinceBuild = intellijSinceBuild
    untilBuild = intellijUntilBuild
    val changelog = project.changelog // local variable for configuration cache compatibility
    // Get the latest available change notes from the changelog file
    changeNotes = providers.gradleProperty("intellijGnVersion").map { pluginVersion ->
        with(changelog) {
            renderItem(
                (getOrNull(pluginVersion) ?: getUnreleased())
                    .withHeader(false)
                    .withEmptySections(false),
                Changelog.OutputType.HTML,
            )
        }
    }
}

tasks.publishPlugin {
    token = providers.environmentVariable("ORG_GRADLE_PROJECT_intellijPublishToken")
}

// Helper build task to create a local updatePlugins.xml file to serve updates
// locally.
task("serverPlugins") {
    dependsOn(tasks.named("buildPlugin"))
    group = "intellij"
    doLast {
      File(layout.buildDirectory.asFile.get(), "distributions/updatePlugins.xml").writeText("""<?xml version="1.0" encoding="UTF-8"?>
<plugins>
    <<plugin id="com.google.idea.gn" url="http://localhost:8080/gn-${version}.zip" version="$version">
      <name>GN</name>
      <description>Experimental GN plugin for intellij</description>
    <idea-version since-build="$intellijSinceBuild" />
  </plugin>
</plugins>
""".trimIndent())
    }
}
