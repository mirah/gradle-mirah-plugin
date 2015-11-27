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

/**
 * @author Schalk W. Cronjé
 */
class TestDefaults {
    final static File TESTRESOURCES = new File(System.getProperty('TESTRESOURCES') ?: './src/test/resources/testcode')
    final static File TESTROOT = new File(System.getProperty('TESTROOT') ?: './build/test/mirah')
    final static File REPODIR = new File(System.getProperty('TESTREPO') ?: './build/test/mirahTestRepo')
}
