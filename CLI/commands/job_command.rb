require_rel "./commands/command"

class JobCommand < Command

	def initialize
		super("status")
		@showMsgs=false
		build_parser
	end

	def execute(str_args)
			
		begin
			@parser.parse(str_args)
			job=Dp2.new.job_status(@id,0)
			str="No such job"
			if job != nil 
				str="Job Id:#{job.id}\n" 
				str+="\t Status: #{job.status}\n" 
				str+="\t Script: #{job.script}\n"
				if @showMsgs 
					job.messages.each{|msg| str+=msg.to_s+"\n"}
				end
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
		return "#{@name}\t\t\t\tShows the detailed status for a single job"	
	end
	def build_parser

		@parser=OptionParser.new do |opts|
			opts.on("--id JOB","job's id") do |v|
				@id=v
			end
			opts.on("-v","Shows the job's log messages") do |v|
				@showMsgs=true
			end
		end
		@parser.program_name="dp2 "+ @name
	end
end
