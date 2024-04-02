plugins {
    id("java-library")
    id("maven-publish")

    alias(libs.plugins.nexusPublish)
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    group = "dev.httpmarco"
    version = "1.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)

        implementation(rootProject.libs.annotations)
        annotationProcessor(rootProject.libs.annotations)
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
        // options
        options.encoding = "UTF-8"
        options.isIncremental = true
    }

    extensions.configure<PublishingExtension> {
        publications {
            create("library", MavenPublication::class.java) {
                from(project.components.getByName("java"))

                pom {
                    name.set(project.name)
                    url.set("https://github.com/httpmarco/osgon")
                    description.set("Reflection/Data libary")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            name.set("Mirco Lindenau")
                            email.set("mirco.lindenau@gmx.de")
                        }
                    }
                    scm {
                        url.set("https://github.com/httpmarco/osgon")
                        connection.set("https://github.com/httpmarco/osgon.git")
                    }
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://nexus.bytemc.de/repository/maven-public/"))
            snapshotRepositoryUrl.set(uri("https://nexus.bytemc.de/repository/maven-public/"))

            username.set(System.getenv("BYTEMC_REPO_USER")?.toString() ?: "")
            password.set(System.getenv("BYTEMC_REPO_PASSWORD")?.toString() ?: "")
        }
    }
    useStaging.set(!project.rootProject.version.toString().endsWith("-SNAPSHOT"))
}