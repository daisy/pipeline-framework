require "./commands/command"

class DeleteCommand < Command

	def initialize
		super("delete")
		build_parser
	end

	def execute(str_args)
			
		begin
			@parser.parse(str_args)
			res=Dp2.new.delete_job(@id)
			str="The job wasn't deleted"
			if res 
				str="Job #{@id} has been deleted\n" 
				str+= "\n"
			end
			puts str
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
		return "#{@name}\t\t\t\tDeletes a job"	
	end
	def build_parser

		@parser=OptionParser.new do |opts|
			opts.on("--id JOB","job's id") do |v|
				@id=v
			end
		end
		@parser.program_name="dp2 "+ @name
	end
end
