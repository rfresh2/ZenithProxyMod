plugins {
	id("fabric-loom") version "1.17-SNAPSHOT"
}

val minecraft_version = project.properties["minecraft_version"] as String
val loader_version = project.properties["loader_version"] as String
val parchment_version = project.properties["parchment_version"] as String
val fabricApiVersion = project.properties["fabric_version"] as String
val modmenuVersion = project.properties["modmenu_version"] as String
version = project.properties["mod_version"] as String
group = project.properties["maven_group"] as String

base {
	archivesName = project.properties["archives_base_name"] as String
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

repositories {
	maven("https://maven.parchmentmc.org") {
		content {
			includeGroup("org.parchmentmc.data")
		}
	}
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
	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-$minecraft_version:$parchment_version@zip")
	})
	modImplementation("net.fabricmc:fabric-loader:${loader_version}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
	modRuntimeOnly("maven.modrinth:modmenu:$modmenuVersion")
}

tasks {
	processResources {
		inputs.property("version", version)

		filesMatching("fabric.mod.json") {
			expand("version" to version)
		}
	}
	jar {
		from("LICENSE") {
			rename { "${it}_${project.base.archivesName.get()}" }
		}
	}
	remapJar {
		archiveVersion = "${project.properties["mod_version"]}+fabric-${project.properties["minecraft_version"]}"
	}
	register("printVersion") {
		doLast {
			println("${project.properties["mod_version"]}")
		}
	}
}
