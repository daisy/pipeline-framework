require_rel "./commands/command"

class HelpCommand < Command
	
	def initialize(static_commands,dynamic_commands,version,config_parser)
		super("help")
		@parser=nil
		@global=false
		@s_commands=static_commands
		@s_commands[@name]=self
		@d_commands=dynamic_commands
		@commands={}
	        @commands.merge!(@s_commands)
		@commands.merge!(@d_commands)	
		@version=version
		@commands[@version.name]=@version
		@cnf_parser=config_parser
		build_parser
	
	end
	def build_parser
		
		@parser=OptionParser.new do |opts|
			opts.on("-g","shows global options") do |v|
				@global=true
			end
		end
		
	end
	def execute(str_args)
		cmd=@parser.permute! str_args	
		cmd=cmd.to_s
			
		if cmd=="pipeliners"
			showpipeliners!	
		elsif  cmd=="COMMAND"
			raise "with COMMAND I meant an available command name like '#{@commands.keys.shuffle[0]}'"
		elsif cmd==nil || cmd.size==0
			if @global
				puts @cnf_parser.help
			else
				puts help
			end
				
		else
			if @commands.has_key?(cmd)
				puts @commands[cmd].help 
			else
				raise "#{@name}: command not found #{cmd}"
			end
			puts "\n"+@cnf_parser.help if @global
		end

	end
	def help
		s="Usage: dp2 command [options]\n\n"
		s+="\nScript commands:\n\n"
		@d_commands.each{|name,cmd| s+="#{cmd.to_s}\n"}
		s+="\nGeneral commands:\n\n"
		
		@s_commands.each{|name,cmd| s+="#{cmd.to_s}\n" if name!="help" && name!="version"}

		s+="#{self.to_s}\n"
		s+="#{@version.to_s}\n"
		s+="\nTo list the global options type:  \tdp2 help -g" 	
		s+="\nTo get help for a command type:  \tdp2 help COMMAND"
			
		return s
	end
	def to_s
		s="help\t\t\t\tShows this message or the command help "  
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
