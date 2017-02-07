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

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTreeElement
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

/**
 * @author Schalk W. Cronjé
 */
class MirahBasePlugin implements Plugin<Project> {

    static final String LANG_NAME = MirahSourceSet.LANG_NAME
    static final String LANG_NAME_C = MirahSourceSet.LANG_NAME_C

    @CompileStatic
    void apply(Project project) {
        project.apply plugin : JavaBasePlugin
        createSourceSetDefaults(project)
        configureAssemblyTasks(project)
    }

    private void createSourceSetDefaults(project) {
        def sourceSets = project.convention.getPlugin(JavaPluginConvention).sourceSets
        sourceSets.all { SourceSet srcSet ->
            final MirahSourceSet langSourceSet = new MirahSourceSet(
                "${LANG_NAME_C} ${srcSet.name}",
                (project as ProjectInternal).fileResolver
            )
            final SourceDirectorySet dirSet = langSourceSet."${LANG_NAME}"

            new DslObject(srcSet).convention.plugins.put(LANG_NAME,langSourceSet)
            dirSet.srcDir("src/${srcSet.name}/${LANG_NAME}")
            srcSet.allJava.source(dirSet)
            srcSet.allSource.source(dirSet)
            srcSet.resources.filter.exclude {
                FileTreeElement fte -> srcSet[LANG_NAME].contains(fte.file)
            }
        }
    }

    private void configureAssemblyTasks(Project project) {
        def jpc = project.convention.getPlugin(JavaPluginConvention)
        jpc.sourceSets.all { SourceSet srcSet ->
            String compileTaskName = srcSet.getCompileTaskName(LANG_NAME)
            MirahCompile compile = project.tasks.create(compileTaskName, MirahCompile)
            project.plugins.getPlugin(JavaBasePlugin).configureForSourceSet(srcSet, compile)
            compile.with {
                dependsOn srcSet.compileJavaTaskName
                description  "Compiles the ${srcSet.name} ${LANG_NAME_C} source"
                source = srcSet."${LANG_NAME}"
            }
            compile.conventionMapping.map("classpath", { srcSet.compileClasspath }) // Evaluate classpath lazily in order to allow other code (running after us) to still change the "classpath" value.  
            project.tasks.getByName(srcSet.classesTaskName).dependsOn compile
        }
    }
}
