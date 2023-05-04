plugins {
    application
    eclipse
}

repositories {
    mavenCentral()
}

dependencies {
	testImplementation("junit:junit:4.13.2")
}

application {
    mainClass.set("ru.spbstu.j23.client.Client")
}
