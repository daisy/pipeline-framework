class VersionCommand < Command
	VERSION="1.0.0"
	def initialize()
		super("version")	
	end

	def execute(str_args)
		puts "Daisy Pipeline 2 Command Line Interface "
		puts "Version: #{VERSION}"
		puts ""
	end
	def to_s
		return "#{@name}\t\t\t\tShows version and exits"	
	end
	def help
		return "dp2 version \n\tPrints version and exists" 
	end
end
