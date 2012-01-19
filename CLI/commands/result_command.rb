require_rel "./commands/command"

class ResultCommand < Command

	def initialize
		super("result")
		@id=nil
		@output=nil
		build_parser
	end

	def execute(str_args)
			
		begin
			@parser.parse(str_args)
			raise RuntimeError,"no job id provided" if @id==nil
			raise RuntimeError,"no output file provided" if @output==nil
			res=Dp2.new.job_zip_result(@id,@output)
			puts "Job #{@id} stored at #{res}\n"
		rescue Exception => e
			 
			Ctxt.logger.debug(e)
			puts "\nERROR: #{e}\n\n"
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
			opts.on("--id JOB","job's id") do |v|
				@id=v
			end
			opts.on("--file PATH","where to store the job's result") do |v|
				@output=v
			end
		end
		@parser.program_name="dp2 "+ @name
	end
end
