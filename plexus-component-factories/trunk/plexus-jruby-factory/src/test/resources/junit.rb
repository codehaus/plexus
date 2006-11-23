require 'java'

include_class 'org.codehaus.plexus.component.factory.jruby.JRubyTestCase'

class RubyTest < JRubyTestCase
  def testSimple()
  end
  def run()
    puts "run run"
  end
end

return RubyTest.new
