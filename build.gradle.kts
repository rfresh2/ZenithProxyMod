plugins {
	id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
}

val minecraft_version = project.properties["minecraft_version"] as String
val loader_version = project.properties["loader_version"] as String
val fabricApiVersion = project.properties["fabric_version"] as String
val modmenuVersion = project.properties["modmenu_version"] as String
version = project.properties["mod_version"] as String
group = project.properties["maven_group"] as String

base {
	archivesName = project.properties["archives_base_name"] as String
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
	maven("https://api.modrinth.com/maven") {
		name = "Modrinth"
	}
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
	minecraft("com.mojang:minecraft:${minecraft_version}")
	implementation("net.fabricmc:fabric-loader:${project.properties["loader_version"]}")
    implementation("net.fabricmc.fabric-api:fabric-api:${project.properties["fabric_version"]}")
    runtimeOnly("maven.modrinth:modmenu:$modmenuVersion")
}

tasks {
	processResources {
		inputs.property("version", version)

		filesMatching("fabric.mod.json") {
			expand("version" to version)
		}
	}
	jar {
		archiveVersion = "${project.properties["mod_version"]}+fabric-${minecraft_version}"
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
