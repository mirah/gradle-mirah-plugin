/*
 * ============================================================================
 * (C) Copyright Schalk W. Cronjé 2015-2016
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
import org.gradle.api.plugins.JavaPlugin

/**
 * @author Schalk W. Cronjé
 */
class MirahPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.apply plugin : MirahBasePlugin
        project.apply plugin : JavaPlugin
    }
}
