import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass.set("codes.tad.nixandroidrepo.MainKt")
}

dependencyLocking {
    lockAllConfigurations()
}

dependencies {
    implementation(libs.common)
    implementation(libs.sdklib)
    implementation(libs.coroutines)
    implementation(libs.jaxb.api)
    runtimeOnly(libs.jaxb.impl)
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    register("downloadSources") {
        doLast {
            val componentIds = configurations.filter { it.isCanBeResolved }
                .flatMap { c -> c.incoming.resolutionResult.allComponents }
                .map { it.id }

            project.dependencies.createArtifactResolutionQuery()
                .forComponents(componentIds)
                .withArtifacts(JvmLibrary::class.java, SourcesArtifact::class.java)
                .execute()
                .resolvedComponents
                .flatMap { it.getArtifacts(SourcesArtifact::class.java) }
                .filterIsInstance<ResolvedArtifactResult>()
        }
    }

    wrapper {
        gradleVersion = "7.3.1"
    }
}
