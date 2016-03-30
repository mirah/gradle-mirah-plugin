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

import org.gradle.api.tasks.Input

/** Various Mirah compile options
 *
 */
class MirahCompileOptions {
    /** Use new closure implementation
     *
     */
    @Input
    boolean newClosures = false

    /** Returns the options as command-line switches
     *
     * @return List suitable to be passed to {@code JavaExec}.
     */
    List asCommandOptions() {
        List args= []
        if(newClosures) {
            args+='--new-closures'
        }
        args
    }

}
