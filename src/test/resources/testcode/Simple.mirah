
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

