require_rel "./commands/id_based_command"

class ResultCommand < IdBasedCommand 

	def initialize
		super("result")
		@id=nil
		@output=nil
		build_parser
	end

	def execute(str_args)
			
		begin
			getId!(str_args)
			raise RuntimeError,"no output file provided" if @output==nil
			res=Dp2.new.job_zip_result(@id,@output)
			puts "[DP2] Job #{@id} stored at #{res}\n"
		rescue Exception => e
			 
			Ctxt.logger.debug(e)
			puts "\n[DP2] ERROR: #{e}\n\n"
			puts help
		end
	end
	def help
		return @parser.help
	end
	def to_s
		return "#{@name}\t\t\t\tGets the zip file containing the job results"	
	end
	def build_parser

		@parser=OptionParser.new do |opts|
			opts.on("--file PATH","where to store the job's result") do |v|
				@output=v
			end
			addLastId(opts)
		end
		@parser.banner="dp2 "+ @name + " [options] JOBID"
	end
end
