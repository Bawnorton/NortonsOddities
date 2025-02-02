import org.gradle.api.Project
import org.gradle.api.tasks.TaskContainer

class LoaderData(private val project: Project, private val name: String) {
    private val isFabric = name == "fabric"
    private val isNeoForge = name == "neoforge"
    private val isForge = name == "forge"

    fun getVersion() : String = if (isNeoForge) {
        project.property("neoforge_loader").toString()
    } else if (isFabric) {
        project.property("fabric_loader").toString()
    } else {
        project.property("forge_loader").toString()
    }

    override fun toString(): String {
        return name
    }

    fun neoforge(container: () -> Any) {
        if(isNeoForge) container.invoke()
    }

    fun fabric(container: () -> Any) {
        if(isFabric) container.invoke()
    }

    fun forge(container: () -> Any) {
        if(isForge) container.invoke()
    }
}