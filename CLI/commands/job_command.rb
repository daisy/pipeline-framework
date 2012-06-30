require_rel "./commands/id_based_command"

class JobCommand < IdBasedCommand

	def initialize
		super("status")
		@showMsgs=false
		build_parser
	end
	def execute(str_args)
			
		begin
			getId!(str_args)	
			job=Dp2.new.job_status(@id,0)
			str="No such job"
			if job != nil 
				str="Job Id:#{job.id}\n" 
				str+="\t Status: #{job.status}\n" 
				str+="\t Script: #{job.script.uri}\n"
				if @showMsgs 
					job.messages.each{|msg| str+=msg.to_s+"\n"}
				end
				str+= "\n"
			end
			puts "[DP2] "+ str
		rescue Exception => e
			 
			Ctxt.logger.debug(e)
			puts "\n[DP2] ERROR: #{e.message}\n\n"

			puts to_s 
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
			opts.on("-v","Shows the job's log messages") do |v|
				@showMsgs=true
			end
			addLastId(opts)
		end
		@parser.banner="dp2 "+ @name + " [options] JOBID"
	end
end
