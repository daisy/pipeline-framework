require_rel "./commands/command"

class JobsCommand < Command

	def initialize
		super("jobs")
	end

	def execute(str_args)
		raise RuntimeError,help if str_args.size!=0
		begin
			jobs=Dp2.new.job_statuses
			jobs.each { |job|
				str="Job Id:#{job.id}\n" 
				str+="\t Status: #{job.status}\n" 
				str+="\t Script: #{job.script}\n\n"
				puts str
			}
			puts "No jobs where found" if jobs.size==0
			
		rescue Exception => e
			 
			Ctxt.logger.info(e)
			puts "\nERROR: #{e}\n\n"
			puts help
		end
	end
	def help
		return "Shows the status for every job"	
	end
	def to_s
		return "#{@name}\t\t\t\t#{help}"	
	end
end
