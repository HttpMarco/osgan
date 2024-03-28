dependencies {
    implementation(libs.netty5)
    compileOnly(libs.gson)
    api(project(":osgan-utils"))
    api(project(":osgan-files"))
    api(project(":osgan-reflections"))

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
