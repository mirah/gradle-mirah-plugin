package org.ysb33r.gradle.mirah.internal

import groovy.transform.CompileDynamic

/** Used to maintain backwards compatibility across 2.7-2.8 Gradle boundary.
 * @since 1.0
 */
class Compatibility {

    @CompileDynamic
    static String capitalize(final String f) {
        f.capitalize()
    }
}
