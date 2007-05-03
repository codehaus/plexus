
class Hello
  def print( greeting )
    puts "#{greeting} from #{$hello_from.to_s}"
  end
end

Hello.new.print "Hello"
