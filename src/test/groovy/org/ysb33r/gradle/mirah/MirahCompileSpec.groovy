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

    def "Extract Mirah JAR correctly from classpath"() {
        given:
        compile.classpath = project.fileTree( dir : TestDefaults.REPODIR, includes : ['*.jar'])
        def mcp = compile.mirahClasspath

        expect:
        !mcp.empty
        mcp.files.any { it.name.startsWith('mirah-') }
    }

    def "Given a classpath, compile one file"() {
        given:
        compile.with {
            showAllErrors = true
            setDestinationDir new File(TESTDIR,'output')
            setClasspath  project.fileTree( dir : TestDefaults.REPODIR, includes : ['*.jar'])
            setSourceCompatibility '1.7'
            setTargetCompatibility '1.7'
            source new File(TestDefaults.TESTRESOURCES,'Simple.mirah')
        }

        when:
        compile.execute()

        then:
        !compile.source.empty
        new File(compile.destinationDir,'Simple.class').exists()
    }
}