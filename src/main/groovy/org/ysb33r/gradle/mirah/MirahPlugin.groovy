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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTreeElement
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet

/**
 * @author Schalk W. Cronjé
 */
class MirahPlugin implements Plugin<Project> {
    static final String SOURCESET_NAME = 'mirah'
    static final String DIR_NAME = 'mirah'
    static final String COMPILE_TASK = 'compileMirah'
    static final String TEST_COMPILE_TASK = 'compileTestMirah'

    void apply(Project project) {
        project.with {
            apply plugin : JavaPlugin

            tasks.create COMPILE_TASK, MirahCompile
            tasks.create TEST_COMPILE_TASK, MirahCompile

            sourceSets.each { SourceSet srcSet ->
                final MirahSourceSet mss = new MirahSourceSet("Mirah ${srcSet.name}",(project as ProjectInternal).fileResolver)
                new DslObject(srcSet).convention.plugins.put(SOURCESET_NAME,mss)
                mss.mirah.srcDir("src/${srcSet.name}/${DIR_NAME}")

                srcSet.allJava.source(mss.mirah)
                srcSet.allSource.source(mss.mirah)
                srcSet.resources.filter.exclude { FileTreeElement element -> mss.contains(element.file) }

                def compile = tasks.getByName(srcSet.getCompileTaskName(SOURCESET_NAME))
                plugins.getPlugin(JavaBasePlugin).configureForSourceSet(srcSet,compile)
                compile.with {
                    dependsOn srcSet.compileJavaTaskName
                    description "Compile the ${srcSet.name} Mirah source"
                    source = {mss.mirah}
                    classpath = srcSet.compileClasspath
                }
                tasks.getByName(srcSet.classesTaskName).dependsOn compile
            }
        }
    }
}
