class Settings
  TIMEOUT_SECONDS = 100
  BASEURI = "http://localhost:8182/ws/"

  attr_accessor :options
  attr_reader :baseuri
  attr_accessor :command

  def initialize
    @options = {}
    @baseuri = BASEURI
    @command = ""
  end

  @@instance = Settings.new

  def self.instance
    return @@instance
  end

  def set_baseuri(uri)
    if uri.length > 0
      @baseuri = uri
      if uri.end_with?("/") == false
        @baseuri = "#{uri}/"
      end
    end
  end
  private_class_method :new

end