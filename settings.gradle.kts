
settings.rootProject.name = "learn-java-gui"

settings.include(":java-swing");
settings.include(":java-fx");

final lateinit var JAVA_PLUGIN_IDS: Set<String>;

settings.gradle.settingsEvaluated {
    JAVA_PLUGIN_IDS = setOf(
        "java",
        "java-application",
        "application",
        "java-library",
        "war",
        "kotlin",
        "groove"
    ).map { it.lowercase() }.toSet();
}

settings.gradle.projectsEvaluated {
    this.allprojects {
        if( JAVA_PLUGIN_IDS.any{ javaPluginId ->
            this.project.plugins.hasPlugin( javaPluginId )
        }) {
            val SOURCE_SETS_EXTENSION: SourceSetContainer =
                this.project.extensions.getByType<SourceSetContainer>();

            SOURCE_SETS_EXTENSION.apply {
                this.named( "main" ) {
                    this.java {
                        this.setSrcDirs( listOf( "src/main/" ) );
                        this.setExcludes( listOf( "src/main/resources/" ) );
                    }
                    this.resources {
                        this.setSrcDirs( listOf( "src/main/" ) );
                        this.setExcludes( listOf( "src/main/" ) );
                    }
                }
                this.named( "test" ) {
                    this.java {
                        this.setSrcDirs( listOf( "src/test/" ) );
                        this.setExcludes( listOf( "src/test/resources/" ) );
                    }
                    this.resources {
                        this.setSrcDirs( listOf( "src/test/resources/" ) );
                        this.setExcludes( listOf( "src/test/" ) );
                    }
                }
            }
        }
    }
}