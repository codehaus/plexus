
class Hello
  def execute
    puts "java.util.Random.nextInt: #{$random.nextInt}"
    puts "from_class: #{$from_class.to_s}"
  end

  def print( msg )
    puts "printed: #{msg}"
  end
end

h = Hello.new

h.print "Hey"

return h
