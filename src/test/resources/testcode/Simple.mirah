#
# ============================================================================
# (C) Copyright Schalk W. Cronjé 2015-2016
#
# This software is licensed under the Apache License 2.0
# See http://www.apache.org/licenses/LICENSE-2.0 for license details
#
# Unless required by applicable law or agreed to in writing, software distributed under the License is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and limitations under the License.
#
# ============================================================================
#


# This code is a rip-off from Mirah HOWTO (http://www.mirah.org/wiki/MirahHowto)
class Simple
 def foo
   home = System.getProperty "java.home"
   System.setProperty "hello.world", "something"
   hello = System.getProperty "hello.world"

   puts home
   puts hello
 end
end

