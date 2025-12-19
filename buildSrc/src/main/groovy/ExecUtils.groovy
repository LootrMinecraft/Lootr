import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class ExecUtils {
    @Inject abstract ExecOperations getExecOperations()

    String getExecOutput(List<String> commands) {
        def out = new ByteArrayOutputStream()
        execOperations.exec {
            commandLine(commands)
            standardOutput = out
            errorOutput = out
        }
        return out.toString('UTF-8').trim()
    }
}