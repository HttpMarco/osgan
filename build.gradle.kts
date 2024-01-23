plugins {
    id("java")
    `maven-publish`
}

group = "dev.httpmarco"
version = "1.0.0-SNAPSHOT"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/HttpMarco/reflections")
            credentials {
                username = System.getenv("GITHUB_PUBLISH_USER")
                password = System.getenv("GITHUB_PUBLISH_KEY")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}