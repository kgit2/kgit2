import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.kgit2.ksp.ProcessorDispatcher
import freemarker.template.Configuration
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule

lateinit var koin: Koin

class ProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        environment.codeGenerator.generatedFile.forEach {
            environment.logger.warn("Platform: $it")
        }
        val application = startKoin {
            printLogger(Level.DEBUG)
            modules(
                defaultModule,
                module {
                    single { environment }
                    single {
                        Configuration(Configuration.VERSION_2_3_31).apply {
                            setClassForTemplateLoading(ProcessorProvider::class.java, "/templates")
                            defaultEncoding = "UTF-8"
                        }
                    }
                },
            )
        }
        koin = application.koin
        return koin.get<ProcessorDispatcher>()
    }
}
