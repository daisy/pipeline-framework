require_rel "./commands/command"

class HelpCommand < Command
	
	def initialize(static_commands,dynamic_commands)
		super("help")
		@s_commands=static_commands
		@s_commands[@name]=self
		@d_commands=dynamic_commands
		@commands={}
	        @commands.merge!(@s_commands)
		@commands.merge!(@d_commands)	
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
		s="Usage: dp2 command [options]\n\n"
		s+="#{self.to_s}\n"
		s+="\nScript commands:\n\n"
		@d_commands.each{|name,cmd| s+="#{cmd.to_s}\n"}
		s+="\nAdvanced commands:\n\n"
		
		@s_commands.each{|name,cmd| s+="#{cmd.to_s}\n" if name!="help"}
		s+="\nTo get help for a command write:\ndp2 help COMMAND"
		
		return s
	end
	def to_s
		s="help\t\t\t\tShows this message or the command help"  
	end	
	def showpipeliners!
		puts "The pipeliners!\n"
	if ENV["OCRA_EXECUTABLE"]==nil
		File.foreach(File.dirname(__FILE__)+File::SEPARATOR+'rb.dat') { |s| puts s}
	else
		File.foreach(File.dirname(ENV["OCRA_EXECUTABLE"])+File::SEPARATOR+"commands"+File::SEPARATOR+'rb.dat') { |s| puts s}
	end
		puts "Make sure your command window is wide enough :D\n"
	end
end
