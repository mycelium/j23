plugins {
    application
    eclipse
}

repositories {
    mavenCentral()
}

dependencies {
	
	implementation("org.xerial:sqlite-jdbc:3.41.2.1")

	testImplementation("junit:junit:4.13.2")
}


application {
    mainClass.set("ru.spbstu.j23.server.App")
}
