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

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.util.ConfigureUtil

/**
 * @author Schalk W. Cronjé
 */
class MirahSourceSet {

    MirahSourceSet(String displayName, FileResolver fileResolver) {
        this.mirah = new DefaultSourceDirectorySet(String.format("%s Mirah source", displayName), fileResolver)
        mirah.filter.include("**/*.mirah")
        allMirah = new DefaultSourceDirectorySet(String.format("%s Mirah source", displayName), fileResolver)
        allMirah.source(mirah)
        allMirah.filter.include("**/*.mirah")
    }

    SourceDirectorySet getMirah() {
        this.mirah
    }

    MirahSourceSet mirah(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getMirah())
        return this
    }

    SourceDirectorySet getAllMirah() {
        this.allMirah
    }

    private final SourceDirectorySet mirah
    private final SourceDirectorySet allMirah

}
