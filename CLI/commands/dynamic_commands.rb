require_rel "./core/dp2"
class DynamicCommands

	def self.get
		commands=[]
		scripts=Dp2.new.scripts
		scripts.values.each { |script| commands.push(CommandScript.new(script))}
		return commands
	end

end

class CommandScript < Command
	attr_accessor :script,:opt_modifiers,:input_modifiers,:output_modifiers
	def initialize(script)
		super(script.nicename)
		@script=script.clone
		@opt_modifiers={}
		@input_modifiers={}
		@output_modifiers={}
		@background=false
		@persistent=false
		
		build_modifiers
		build_parser
	end
	def execute(str_args)

		begin
			dp2ws=Dp2.new
			@parser.parse(str_args)	
			job=dp2ws.job(@script,nil,!@background)
			if !@persistent
				puts job.status
				if  dp2ws.delete_job(job.id)
					puts "The job has been cleaned from the server"
				end
			end
		rescue Exception => e
				Ctxt.logger.info(e)
				puts "\nERROR: #{e}\n\n"
				puts help
		end
	end
	def help
		return @parser.help	
	end

	def to_s
		s="#{@script.nicename}\t\t\t#{@script.desc}"
		return s
	end

	def build_parser


		@parser=OptionParser.new do |opts|
			
			@input_modifiers.keys.each{|input|
				@input_modifiers[input][:value]=nil
				if @input_modifiers[input][:sequenceAllowed]=='true'
					opts.on(input+" input1,input2,input3",Array,@input_modifiers[input][:help]) do |v|
					   @input_modifiers[input][:value] = v
					end
				else
					opts.on(input+" input",@input_modifiers[input][:help]) do |v|
					   @input_modifiers[input][:value] = [v]
					end
				end

			}
			@output_modifiers.keys.each{|output|
				@output_modifiers[output][:value]=nil
				if @output_modifiers[output][:sequenceAllowed]=='true'
					opts.on(output+" output1,output2,output3",Array) do |v|
					   @output_modifiers[output][:value] = v
					end
				else
					opts.on(output+" output") do |v|
					   @output_modifiers[output][:value] = v
					end
				end

			}

			@opt_modifiers.keys.each{|option|
				@opt_modifiers[option][:value]=nil
				opts.on(option+" [option_value]",@opt_modifiers[option][:help]) do |v|
				    @opt_modifiers[option][:value] = v
				end
			}
			opts.on("--background","-b","Runs the job in the background (will be persistent)") do |v|
				@background=true
				@persistent=true
			end
			opts.on("--persistent","-p","Forces to keep the job data in the server") do |v|
				@persistent=true
			end

		end
		
		@parser.program_name="dp2 "+ @name
	end

	def build_modifiers
		
		@script.opts.each {|opt|
			modifier="--x-#{opt[:name]}"
			opt[:help]="\n\t\t Desc:#{opt[:desc]}\n"\
			+"\t\t Media type:#{opt[:mediaType]}\n"\
			+"\t\t Required:#{opt[:required]}\n"\
			+"\t\t Type:#{opt[:type]}\n\n"
			@opt_modifiers[modifier]=opt
			
		}
		@script.inputs.each {|input|
			modifier="--i-#{input[:name]}"
			input[:help] ="\n\t\t Desc:#{input[:desc]}\n"\
			+"\t\t Media type:#{input[:mediaType]}\n"\
			+"\t\t Sequence allowed:#{input[:sequenceAllowed]}\n\n"
			@input_modifiers[modifier]=input
		}
		@script.outputs.each {|out|
			modifier="--i-#{out[:name]}"
			@output_modifiers[modifier]=output
		}
	end
end
