import java.util.*

plugins {
    application
}

project.version = thisRootLib.simple.calculator.get().version!!;
project.group = thisRootLib.simple.calculator.get().group!!;

val PROJECT_GROUP = project.group.toString()
    .replace( Regex("[ *#+-]"), "_");
val ROOT_PROJECT_NAME = project.rootProject.name
    .replace( "-", "_" );
val PROJECT_NAME = project.name
    .replace( "-", "_" );

project.application {
//    this.mainClass.set( "${project.group}.${ROOT_PROJECT_NAME}.${PROJECT_NAME}.MainExe")
    this.mainClass.set(
        "${PROJECT_GROUP}.${ROOT_PROJECT_NAME}.${PROJECT_NAME}" +
            ".MainExe"
    )
}

project.tasks.jar {
    this.manifest.attributes["Main-Class"] = project.application.mainClass;
    this.manifest {
        this.attributes["Implementation-Vendor"] = project.group;
        this.attributes["Author"] = project.loadProperties(project.projectDir.path + "/local.properties")
            .getProperty("AUTHOR")?.split(Regex("[ ,;:#*/+-]+"))
            ?: project.group.toString()
    }
}

fun Project.loadProperties(fileName: String): Properties {
    val properties = Properties();
    val file = File(fileName);
    if (file.exists())
        file.inputStream().use { properties.load(it) };
    return properties;
}
