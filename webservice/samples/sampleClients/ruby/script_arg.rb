class ScriptArg
  attr_reader :name
  attr_reader :value_list

  def initialize(name)
    @name = name
    @value_list = Array.new
  end

  def add_value(value)
    @value_list.push(value)
  end
end
