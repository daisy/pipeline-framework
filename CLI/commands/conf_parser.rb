class ConfParser
	def initialize
		build_parser
	end

	def help
		return @parser.help
	end

	def parse(args)
		@parser.parse(args)
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
		@parser.banner=""
	end
end
