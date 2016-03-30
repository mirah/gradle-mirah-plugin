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
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.util.ConfigureUtil

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class MirahSourceSet {

    static final String LANG_NAME = 'mirah'
    static final String LANG_NAME_C = LANG_NAME.capitalize()

    MirahSourceSet(String displayName, FileResolver fileResolver) {
        this.mirah = createSourceDirectorySet(
            "${displayName} ${LANG_NAME_C} source",
            fileResolver
        )

        // TODO: If Mirah supports Join compilation, this will need to be fixed
        this.mirah.filter.include("**/*.mirah")

        mirahOnly = createSourceDirectorySet(
            "${displayName} ${LANG_NAME_C} source",
            fileResolver
        )
        mirahOnly.source(this.mirah)
        mirahOnly.filter.include('**/*.mirah')
    }

    SourceDirectorySet getMirah() {
        this.mirah
    }

    MirahSourceSet mirah(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getMirah())
        return this
    }

    SourceDirectorySet getAllMirah() {
        this.mirahOnly
    }

    /** Handles the internal API change between Gradle 2.0/2.11 & Gradle 2.12
     *
     * @param name Descriptive name for source set
     * @param fileResolver Internal provision file resolver.
     * @return
     */
    @CompileDynamic
    private DefaultSourceDirectorySet createSourceDirectorySet(
        String name,
        FileResolver fileResolver
    ) {
        DefaultSourceDirectorySet.constructors.findResult { ctor ->
            if(ctor.parameterTypes == [String,FileResolver] as Class[]) {
                return ctor.newInstance(name,fileResolver)  // <2>
            } else if (ctor.parameterTypes == [String,String,FileResolver] as Class[]) {
                return null // <3>
            }
            try {
                Class<?> dftfInterface = Class.forName(
                    'org.gradle.api.internal.file.collections.DirectoryFileTreeFactory'
                )
                Class<?> fileTreeFactory = Class.forName(
                    'org.gradle.api.internal.file.collections.DefaultDirectoryFileTreeFactory'
                )

                if(ctor.parameterTypes == [String,FileResolver,dftfInterface] as Class[] ) {
                    return ctor.newInstance(name,fileResolver,fileTreeFactory.newInstance())
                }
            } catch (ClassNotFoundException){ // <8>
            }
            null
        }
    }
    private final SourceDirectorySet mirah
    private final SourceDirectorySet mirahOnly

}

