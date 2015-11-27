/*
 * ============================================================================
 * (C) Copyright Schalk W. Cronjé 2015
 *
 * This software is licensed under the Apache License 2.0
 * See http://www.apache.org/licenses/LICENSE-2.0 for license details
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 * ============================================================================
 */
package org.ysb33r.gradle.mirah

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.AbstractCompile

import java.util.regex.Pattern

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class MirahCompile extends AbstractCompile {

    private static final Pattern MIRAH_FILE_PATTERN = ~/mirah-(\p{Digit}+\.){2}.+\.jar/

    /**
     * The classpath to use to load the Mirah compiler.
     */
    @InputFiles
    FileCollection getMirahClasspath() {
        if(this.mirahClasspath) {
            (project as Project).files(this.mirahClasspath)
        } else {
            classpath.filter { File file ->
                file.name =~ MIRAH_FILE_PATTERN
            }
        }
    }

    void setMirahClasspath(Object cp) {
        this.mirahClasspath = cp
    }

    void mirahClasspath(Object cp) {
        this.mirahClasspath = cp
    }

//    @Optional
//    @InputFiles
//    FileCollection macroClasspath

    /** Use new closure implementation
     *
     */
    @Input
    boolean newClosures = false

    @Input
    boolean showAllErrors = project.logger.isEnabled(LogLevel.INFO)

    void setJvmArgs(String... args) {
        this.jvmArgs = args as List
    }

    void jvmArgs(String...args) {
        this.jvmArgs+= args as List
    }

    @Input
    List<String> getJvmArgs() {
        this.jvmArgs
    }

    /** Translates attributes to compile arguments that mirahc will understand.
     *
     * @return
     */
    List getCompilerArgs() {
        List args = [
            '--dest', destinationDir.absolutePath,
            '--jvm', targetCompatibility
        ]
        if(project.logging.level == LogLevel.INFO) {
            args+= '--verbose'
        } else if(project.logging.level == LogLevel.QUIET) {
            args+= '--silent'
        }

        if(!classpath.empty) {
            args+= '--classpath'
            args+= classpath.asPath
        }

        if(showAllErrors) {
            args+='--all-errors'
        }

        if(newClosures) {
            args+='--new-closures'
        }

        // TODO: project.gradle.startParameter.??? -> --no-color. Only specific versions of gradle
        //  For now, just turn colour off
        args+='--no-color'

        // TODO: bootclasspath ?
        // TODO: macroclasspath ?

        args

    }

    @Override
    @TaskAction
    protected void compile() {
        doCompile( jvmArgs, compilerArgs, getSource() , getMirahClasspath() )
    }

    private void validateCompilerClasspath( final FileCollection cp, final String classpathPropertyName, final String pluginName  ) {
        if (cp.empty) {
            throw new InvalidUserDataException(
                "'${name}.${classpathPropertyName}' must not be empty. If a Mirah compile dependency is provided, " +
                    "the '${pluginName}' plugin will attempt to configure '${classpathPropertyName}' automatically. " +
                    "Alternatively, you may configure '${classpathPropertyName}' explicitly.")
        }

    }

    @CompileDynamic
    private def doCompile(
        final List jvmArgumentsToUse,
        final List compilerArgsToUse,
        final FileCollection sourcesToUse,
        final FileCollection mirahClasspathToUse
    ) {
        validateCompilerClasspath( mirahClasspathToUse, 'mirahClasspath', 'Mirah')

        logger.debug("Calling Mirah compiler with jvmArgs: '${jvmArgumentsToUse}'")
        logger.debug("Calling Mirah compiler with compilerArgs: '${compilerArgsToUse}'")
        logger.debug("Calling Mirah compiler with sources: '${sourcesToUse}'")
        logger.debug("Using Mirah jar: ${mirahClasspathToUse.asPath}")

        project.javaexec {
            setJvmArgs jvmArgumentsToUse
            classpath mirahClasspathToUse.asPath
            main 'org.mirah.tool.Mirahc'
            args compilerArgsToUse
            args sourcesToUse.files
            errorOutput System.err
            standardOutput System.out
        }
    }

    private List jvmArgs = []
    private Object mirahClasspath
}

