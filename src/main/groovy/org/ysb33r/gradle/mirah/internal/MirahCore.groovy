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
package org.ysb33r.gradle.mirah.internal

import org.gradle.api.file.FileCollection

import java.util.regex.Pattern

/**
 * @author Schalk W. Cronjé
 */
class MirahCore {
    static final Pattern MIRAH_FILE_PATTERN = ~/mirah-(\p{Digit}+\.){2}.+\.jar/

    /** Returns a {@code FileCollection} that contains the necessary JARs to run Mirah tools
     *
     * @param FileCollection Arbitrary classpath
     * @return Resolved classpath (could be empty).
     */
    static FileCollection resolveEngineClasspath(FileCollection classpath) {
        classpath.filter { File file ->
            file.name =~ MirahCore.MIRAH_FILE_PATTERN
        }
    }
}
