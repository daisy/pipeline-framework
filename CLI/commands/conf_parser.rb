class ConfParser
	def initialize
		build_parser
	end

	def help

		return @parser.help
	end

	def parse(args)
		unknown=[]
		nonopts=[]
		#This parses only the subset of args related to the global configuration items
		begin
			@parser.order!(args) do |nop|
				Ctxt.logger.debug("no option yieled #{nop}")
				nonopts << nop
			end
		rescue OptionParser::InvalidOption => e
			
			e.recover args	
			unknown << args.shift
			unknown << args.shift if args!=nil and args.size>0 and args.first[0..0]!='-'
			retry
		end
		nonopts.reverse.each { |nonopt| unknown.unshift(nonopt)}
		return unknown
	end	

	def build_parser
		@parser=OptionParser.new do |opts|
			Conf::CONFIG_ITEMS.each do |name,desc|
				if Conf::CONST_FILTER.index(name)==nil 	
					opts.on("--#{name} VALUE",desc+" default("+Ctxt.conf[name].to_s+")") do |v|
						Ctxt.conf[name]=v	
						Ctxt.conf.update_vals
					end
				end
			end
		end
		@parser.banner="Global CLI switches:"
	end
end
