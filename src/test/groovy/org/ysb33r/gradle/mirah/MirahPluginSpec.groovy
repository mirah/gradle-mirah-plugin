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

import org.apache.commons.io.FileUtils
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.mirah.internal.TestDefaults
import spock.lang.Specification


/**
 * @author Schalk W. Cronjé
 */
class MirahPluginSpec extends Specification {

    final static File TESTDIR = new File(TestDefaults.TESTROOT,'MirahPluginSpec')

    def project

    void setup() {
        if(TESTDIR.exists()) {
            TESTDIR.deleteDir()
        }
        TESTDIR.mkdirs()

        project = ProjectBuilder.builder().withProjectDir(TESTDIR).build()
    }
    def "When the plugin is applied, some basic configurations and tasks should be added" () {
        given: "A project setup"
        def cfg = project.configurations
        def tasks = project.tasks

        when: "The project is applied"
        project.apply plugin : 'org.mirah.lang'

        and:
        def sourcesets = project.sourceSets
        Set depsClasses = tasks.classes.dependsOn
        Set depsTestClasses = tasks.testClasses.dependsOn

        then: "the configurations are loaded as per Java plugin"
        cfg.getByName('compile')
        cfg.getByName('runtime')
        cfg.getByName('testCompile')
        cfg.getByName('testRuntime')

        and: "the sourcesets for mirah are added to main & test"
        sourcesets.main.mirah != null
        sourcesets.test.mirah != null
        sourcesets.main.getCompileTaskName('mirah') == MirahPlugin.COMPILE_TASK
        sourcesets.test.getCompileTaskName('mirah') == MirahPlugin.TEST_COMPILE_TASK

        and: 'the compilation tasks are added'
        tasks.getByName(MirahPlugin.COMPILE_TASK) instanceof MirahCompile
        tasks.getByName(MirahPlugin.TEST_COMPILE_TASK) instanceof MirahCompile

        and: 'the compilation tasks are linked to (test)classes lifecyle tasks'
        (depsClasses.any {
            it instanceof Task && it.name == MirahPlugin.COMPILE_TASK ||
                it instanceof CharSequence && it.toString() == MirahPlugin.COMPILE_TASK
        }) != null
        (depsTestClasses.any {
            it instanceof Task && it.name == MirahPlugin.TEST_COMPILE_TASK ||
                it instanceof CharSequence && it.toString() == MirahPlugin.TEST_COMPILE_TASK
        }) != null
    }

    def 'Compile Mirah main sourceset'() {
        given:
        File srcDir = new File(TESTDIR,'src/main/mirah')
        File classesDir = new File(TESTDIR,'build/classes/main')

        srcDir.mkdirs()
        FileUtils.copyDirectory(TestDefaults.TESTRESOURCES,srcDir)

        when:
        project.allprojects {
            repositories {
                flatDir {
                    dirs TestDefaults.REPODIR
                }
            }

            apply plugin : 'org.mirah.lang'

            dependencies {
                compile fileTree( dir : TestDefaults.REPODIR, includes : ['mirah-*.jar'])
            }

        }

        project.evaluate()
        project.tasks.getByName(MirahPlugin.COMPILE_TASK).with {
            println classpath.class.name
            println mirahClasspath.class.name
        }

        project.tasks.getByName(MirahPlugin.COMPILE_TASK).execute()

        then:
        classesDir.exists()

    }
}