require_rel "./commands/command"

class IdBasedCommand < Command
	def initialize(name)
		super(name)	
		@lastid=false
	end

	def getId!(str_args) 
		@id=@parser.parse(str_args)
		if @lastid 
			@id=Helpers.last_id_read()
		end
		if @id==nil
			raise RuntimeError "No job id"
		end

	end

	def addLastId(opts)
		opts.on("-l","--lastid","Uses the id of the last job executed") do |v|
			@lastid=true
		end
	end

end
