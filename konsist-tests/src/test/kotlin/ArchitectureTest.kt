import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.assertTrue
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import org.junit.Test

class ArchitectureTest {

    @Test
    fun `domain does not depend on Android framework`() {
        Konsist
            .scopeFromModule("domain")
            .files
            .assertTrue {
                it.imports.none { import -> import.name.startsWith("android.") }
            }
    }

    @Test
    fun `data does not depend on UI components`() {
        Konsist
            .scopeFromModule("data")
            .files
            .assertTrue {
                it.imports.none { import ->
                    import.name.startsWith("androidx.compose") ||
                    import.name.startsWith("android.widget") ||
                    import.name.startsWith("androidx.fragment")
                }
            }
    }

    @Test
    fun `use cases reside in domain usecase package`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue { it.resideInPackage("..domain..usecase..") }
    }

    @Test
    fun `repositories in domain are interfaces`() {
        Konsist
            .scopeFromModule("domain")
            .interfaces()
            .withNameEndingWith("Repository")
            .assertTrue { it.resideInPackage("..domain..repository..") }
    }

    @Test
    fun `repository implementations reside in data module`() {
        Konsist
            .scopeFromModule("data")
            .classes()
            .withNameEndingWith("RepositoryImpl")
            .assertTrue { it.resideInPackage("..data..repository..") }
    }
}
