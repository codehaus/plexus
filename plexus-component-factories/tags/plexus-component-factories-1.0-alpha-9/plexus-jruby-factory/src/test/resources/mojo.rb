require 'java'

include_class 'org.apache.maven.plugin.Mojo'

class RubyMojo < Mojo
  def execute
#    puts $LOG.methods
#    puts $LOG.java_class
    $LOG.info "from a mojo"
  end
end

return RubyMojo.new
