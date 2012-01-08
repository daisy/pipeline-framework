class Command

	attr_accessor :name 
	def initialize(name)
		@name=name
	end
	def execute(str_args)
		raise NotImplementedError
	end
	def help
		raise NotImplementedError
	end	
end
