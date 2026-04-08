plugins {
	id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
}

version = project.properties["mod_version"] as String
group = project.properties["maven_group"] as String

base {
	archivesName = project.properties["archives_base_name"] as String
}

loom {
	accessWidenerPath = file("src/main/resources/zenithproxy.accesswidener")
	runs {
		getByName("client") {
			client()
		}
	}
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${project.properties["minecraft_version"]}")
	implementation("net.fabricmc:fabric-loader:${project.properties["loader_version"]}")
    implementation("net.fabricmc.fabric-api:fabric-api:${project.properties["fabric_version"]}")
}

tasks {
	processResources {
		inputs.property("version", version)

		filesMatching("fabric.mod.json") {
			expand("version" to version)
		}
	}
	withType(JavaCompile::class.java).configureEach {
		options.release = 25
	}
	jar {
		archiveVersion = "${project.properties["mod_version"]}+fabric-${project.properties["minecraft_version"]}"
		from("LICENSE") {
			rename { "${it}_${project.base.archivesName.get()}" }
		}
	}
	register("printVersion") {
		doLast {
			println("${project.properties["mod_version"]}")
		}
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}
