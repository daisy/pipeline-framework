require_rel "./commands/command"
require 'tmpdir'
class HaltCommand < Command

	def initialize
		super("halt")
		build_parser
	end

	def execute(str_args)
			
		begin
			@parser.parse(str_args)
			res=Dp2.new.halt(get_key)
			puts "WS stopped"
		rescue Exception => e
			 
			Ctxt.logger.debug(e)
			puts "\nERROR: #{e}\n\n"
			puts help
		end
	end
	def help
		return @parser.help
	end
	def to_s
		return "#{@name}\t\t\t\tStops the WS"	
	end
	def build_parser
		@parser=OptionParser.new 
		@parser.banner="Usage: dp2 "+ @name
	end

	def get_key
		keyfile=Dir.tmpdir+File::SEPARATOR+"dp2key.txt"
		key=0
		File.open(keyfile, "r") do |infile|
			key=infile.gets
		end
		return key	
	end
		
end
