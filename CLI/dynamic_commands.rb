require "./core/dp2"
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
		@script=script
		@opt_modifiers={}
		@input_modifiers={}
		@output_modifiers={}
		build_modifiers
	end
	def execute(str_args)
		puts "Done!\n\n\n\n\n\n...just joking, no script submission is implemented yet"
		
	end
	def help
		s="* #{@script.nicename}\n"
		s+="Description: #{@script.desc}\n"
		s+="\nInputs:\n"
	
		@input_modifiers.each{|modif,input| 
			s+="\t#{modif}\n"
			s+="\t\t Desc:#{input[:desc]}\n"
			s+="\t\t Media type:#{input[:mediaType]}\n"
			s+="\t\t Sequence allowed:#{input[:sequenceAllowed]}\n"
		}
		s+="\nOutputs:\n"
		@output_modifiers.each{|modif,output| 
			s+="\t#{modif}\n"
			s+="\t\t Desc:#{output[:desc]}\n"
			s+="\t\t Media type:#{output[:mediaType]}\n"
			s+="\t\t Sequence allowed:#{output[:sequenceAllowed]}\n"
		}
		
		s+="\nOptions:\n"
		@opt_modifiers.each{ |modif,option| 
			s+="\t#{modif}:\n"
			s+="\t\t Desc:#{option[:desc]}\n"
			s+="\t\t Media type:#{option[:mediaType]}\n"
			s+="\t\t Required:#{option[:required]}\n"
			s+="\t\t Type:#{option[:type]}\n"
		}
		return s	
	end

	def to_s
		s="#{@script.nicename}\t\t\t#{@script.desc}"
		return s
	end

	def build_modifiers
		
		@script.opts.each {|opt|
			modifier="--x-#{opt[:name]}"
			@opt_modifiers[modifier]=opt
		}
		@script.inputs.each {|inp|
			modifier="--i-#{inp[:name]}"
			@input_modifiers[modifier]=inp
		}
		@script.outputs.each {|out|
			modifier="--i-#{out[:name]}"
			@output_modifiers[modifier]=output
		}
	end
end
