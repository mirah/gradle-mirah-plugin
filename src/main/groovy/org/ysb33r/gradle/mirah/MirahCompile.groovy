/*
 * ============================================================================
 * (C) Copyright Schalk W. Cronjé 2015-2016
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
import groovy.transform.PackageScope
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.AbstractCompile
import org.ysb33r.gradle.mirah.internal.MirahCore

import java.util.regex.Pattern

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class MirahCompile extends AbstractCompile {

    /** If Mirah class path is set,r esolve that using {@code project.files},
     * otherwise resolves from {@code classpath}
     * @return The classpath to use to load the Mirah compiler.
     *
     */
    @InputFiles
    FileCollection getMirahClasspath() {
        this.mirahClasspath ?  (project as Project).files(this.mirahClasspath) :
            MirahCore.resolveEngineClasspath(getClasspath())
    }

    /** Set Mirah tools classpath.
     *
     * @param cp Anything that can be resolved with {@code project.files}.
     */
    void setMirahClasspath(Object cp) {
        this.mirahClasspath = cp
    }

    /** Options for controlling Mirah byte code generation.
     */
    @Nested
    MirahCompileOptions mirahOptions= new MirahCompileOptions()

    /** Controls error listing. By default all errors will be shown if Gradle is run with
     * {@code --info} switch.
     */
    @Input
    boolean showAllErrors = project.logger.isEnabled(LogLevel.INFO)

//    @Optional
//    @InputFiles
//    FileCollection macroClasspath

//    void setJvmArgs(String... args) {
//        this.jvmArgs = args as List
//    }
//
//    void jvmArgs(String...args) {
//        this.jvmArgs+= args as List
//    }
//
//    @Input
//    List<String> getJvmArgs() {
//        this.jvmArgs
//    }

    @Override
    @TaskAction
    protected void compile() {
//        doCompile( jvmArgs, compilerArgs, getSource() , getMirahClasspath() )
        doCompile( [], getCompilerArgs(), getSource() , getMirahClasspath() )
    }

    private void validateCompilerClasspath( final FileCollection cp, final String classpathPropertyName, final String pluginName  ) {
        if (cp.empty) {
            throw new InvalidUserDataException(
                "'${name}.${classpathPropertyName}' must not be empty. If a Mirah compile dependency is provided, " +
                    "the '${pluginName}' plugin will attempt to configure '${classpathPropertyName}' automatically. " +
                    "Alternatively, you may configure '${classpathPropertyName}' explicitly.")
        }

    }

    /** Translates attributes to compile arguments that mirahc will understand.
     *
     * @return
     */
    @PackageScope
    List getCompilerArgs() {

        List args = mirahOptions.asCommandOptions()
        args.addAll ([
            '--dest', destinationDir.absolutePath,
            '--jvm', targetCompatibility
        ])

        if(project.logging.level == LogLevel.INFO) {
            args.add '--verbose'
        } else if(project.logging.level == LogLevel.QUIET) {
            args.add '--silent'
        }
        
        if (bootClasspath!=null) {
            args.add '--bootclasspath'
            args.add bootClasspath.asPath
        }

        if(!classpath.empty) {
            args.add '--classpath'
            args.add classpath.asPath
        }

        if(showAllErrors) {
            args.add '--all-errors'
        }

        // TODO: project.gradle.startParameter.??? -> --no-color. Only specific versions of gradle
        //  For now, just turn colour off
        args.add '--no-color'

        // TODO: bootclasspath ?
        // TODO: macroclasspath ?

        args
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
    FileCollection bootClasspath    
}

