package org.ysb33r.gradle.mirah.internal

/**
 * @author Schalk W. Cronjé
 */
class TestDefaults {
    final static File TESTRESOURCES = new File(System.getProperty('TESTRESOURCES') ?: './src/test/resources/testcode')
    final static File TESTROOT = new File(System.getProperty('TESTROOT') ?: './build/test/mirah')
    final static File REPODIR = new File(System.getProperty('TESTREPO') ?: './build/test/mirahTestRepo')
}
