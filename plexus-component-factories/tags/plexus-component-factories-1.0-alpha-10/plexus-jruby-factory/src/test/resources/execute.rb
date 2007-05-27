include_class 'org.codehaus.plexus.component.factory.jruby.Executor'

class JExecute < Executor
  def execute
    puts "random number: #{$random.nextInt.to_s}"
  end
end

#return value cannot be prefixed by 'return' keyword
JExecute.new
