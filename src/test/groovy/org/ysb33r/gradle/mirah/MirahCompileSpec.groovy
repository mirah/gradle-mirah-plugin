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

import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.mirah.internal.TestDefaults
import spock.lang.Specification


/**
 * @author Schalk W. Cronjé
 */
class MirahCompileSpec extends Specification {

    final static File TESTDIR = new File('./build/test/mirah','MirahCompileSpec')
    def project
    def compile

    void setup() {
        if(TESTDIR.exists()) {
            TESTDIR.deleteDir()
        }
        TESTDIR.mkdirs()

        project = ProjectBuilder.builder().withProjectDir(TESTDIR).build()
        compile = project.tasks.create('foo',MirahCompile)
    }

    def "Setting the destination directory"() {
        given:
        compile.with {
            setDestinationDir new File(TESTDIR,'output')
        }

        expect:
        compile.destinationDir == new File(TESTDIR,'output')
        compile.getDestinationDir() == new File(TESTDIR,'output')
    }

    def "Extract Mirah JAR correctly from classpath"() {
        given:
        compile.classpath = project.fileTree( dir : TestDefaults.REPODIR, includes : ['*.jar'])
        def mcp = compile.mirahClasspath

        expect:
        !mcp.empty
        mcp.files.any { it.name.startsWith('mirah-') }
    }

    def "Given a classpath, compile one file"() {
        when:
        compile.with {
            showAllErrors = true
            setDestinationDir new File(TESTDIR,'output')
            setClasspath  project.fileTree( dir : TestDefaults.REPODIR, includes : ['*.jar'])
            setSourceCompatibility '1.7'
            setTargetCompatibility '1.7'
            source new File(TestDefaults.TESTRESOURCES,'Simple.mirah')
        }

        then:
        compile.destinationDir != null

        when:
        compile.execute()

        then:
        !compile.source.empty
        new File(compile.destinationDir,'Simple.class').exists()
    }
}