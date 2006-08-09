require 'java'

#include_class 'java.util.Random'
#include_class 'java.lang.Integer'

class Hello
  # All non-primitives must be wrapped in "JavaObject"s. This looks
  # to be the only way to call a method.
  def java_method( method_sym, java_object, *params )
    return java_object.java_class.java_method( method_sym.to_s ).invoke( java_object, *params )
  end

  def execute
    puts $INPUTS[ 'from_class' ].name

#    puts java_method( :setSeed, $INPUTS[ 'random' ], 1 )
#    $INPUTS[ 'random' ].java_class.java_instance_methods[0].class
  end

  def print( msg )
    puts "printed: #{msg}"
#    $INPUTS[ 'random' ].java_class.java_instance_methods.each { |m| puts m.name }
    $INPUTS[ 'random' ].java_class.java_instance_methods.each { |m| puts m.name } #java_instance_methods.each { |m| puts m.name }
    puts "-------------"
    $INPUTS[ 'random' ].java_class.methods.each { |m| puts m } #java_instance_methods.each { |m| puts m.name }
#    puts java_method( :setSeed, $INPUTS[ 'random' ], 1 )
  end
end

h = Hello.new

h.print "Hey"

return h
