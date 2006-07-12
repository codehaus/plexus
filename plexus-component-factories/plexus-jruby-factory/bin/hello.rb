
class Hello
  def execute
    puts "from: #{$INPUTS[ 'from_class' ]}"
  end
  def print( msg )
    puts "printed: #{msg}"
  end
end

h = Hello.new

h.print "Hey"

return h
