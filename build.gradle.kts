plugins {
	id("fabric-loom") version "1.9-SNAPSHOT"
	id("maven-publish")
}

version = project.properties["mod_version"] as String
group = project.properties["maven_group"] as String

base {
	archivesName = project.properties["archives_base_name"] as String
}


repositories {
	maven("https://maven.parchmentmc.org") {
		content {
			includeGroup("org.parchmentmc.data")
		}
	}
}

loom {
	accessWidenerPath = file("src/main/resources/zenithproxy.accesswidener")
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${project.properties["minecraft_version"] as String}")
	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-1.21.1:2024.11.17@zip")
	})
	modImplementation("net.fabricmc:fabric-loader:${project.properties["loader_version"] as String}")
}

tasks {
	processResources {
		inputs.property("version", version)

		filesMatching("fabric.mod.json") {
			expand("version" to version)
		}
	}
	withType(JavaCompile::class.java).configureEach {
		options.release = 21
	}
	jar {
		from("LICENSE") {
			rename { "${it}_${project.base.archivesName.get()}" }
		}
	}
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

// configure the maven publication
//publishing {
//	publications {
//		create("mavenJava", MavenPublication) {
//			artifactId = project.archives_base_name
//			from components.java
//		}
//	}
//
//	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
//	repositories {
//		// Add repositories to publish to here.
//		// Notice: This block does NOT have the same function as the block in the top level.
//		// The repositories here will be used for publishing your artifact, not for
//		// retrieving dependencies.
//	}
//}
