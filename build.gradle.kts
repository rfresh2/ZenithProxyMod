plugins {
	id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
}

val minecraft_version = property("minecraft_version") as String
val loader_version = property("loader_version") as String
val fabric_api_version = property("fabric_version") as String
val modmenu_version = property("modmenu_version") as String
val mod_version = property("mod_version") as String
val maven_group = property("maven_group") as String
val archives_base_name = property("archives_base_name") as String
version = mod_version
group = property("maven_group") as String

base {
	archivesName = archives_base_name
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
	maven("https://api.modrinth.com/maven") {
		name = "Modrinth"
		content {
			includeGroup("maven.modrinth")
		}
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
	minecraft("com.mojang:minecraft:${minecraft_version}")
	implementation("net.fabricmc:fabric-loader:${loader_version}")
	implementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
	runtimeOnly("maven.modrinth:modmenu:$modmenu_version")
}

tasks {
	processResources {
		inputs.property("version", mod_version)

		filesMatching("fabric.mod.json") {
			expand("version" to mod_version)
		}
	}
	jar {
        archiveVersion = "$mod_version+fabric-$minecraft_version"
		from("LICENSE") {
			rename { "${it}_${archives_base_name}" }
		}
	}
	register("printVersion") {
		doLast {
			println(mod_version)
		}
	}
}
