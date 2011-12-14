require "./command"

class HelpCommand < Command
	
	def initialize(commands)
		super("help")
		@commands=commands
		@commands[@name]=self
	end
	def execute(str_args)
		if str_args.size==0
			puts help	
		else
			cmd=str_args[0]
				
			if cmd=="pipeliners"
				showpipeliners!	
			elsif  cmd=="COMMAND"
				raise "with COMMAND I meant an available command name like '#{@commands.keys.shuffle[0]}'"
			else
				if @commands.has_key?(cmd)
					puts @commands[cmd].help 
				else
					raise "#{@name}: command not found #{cmd}"
				end
			end
		end		
	end
	def help
		s="Usage:\ndp2 command [options]\n\n"
		s+="Available commands:\n\n"
		@commands.each{|name,cmd| s+="#{cmd.to_s}\n" if name!="help"}
		s+="#{self.to_s}\n"
		s+="\nTo get help for a command write:\ndp2 help COMMAND"
		
		return s
	end
	def to_s
		s="help\t\t\t\tShows this message or the command help"  
	end	
	def showpipeliners!
		puts "The pipeliners!\n"
		File.foreach(File.dirname(__FILE__)+File::SEPARATOR+'rb.dat') { |s| puts s}
		puts "Make sure your command window is wide enough :D\n"
	end
end
