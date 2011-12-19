require 'yaml'
require './core/ctxt'
class Conf
	#KEYS
	PORT="port"
	HOST="host"
	WS_PATH="ws_path"
	EXEC_LINE_WIN="exec_line_win"
	EXEC_LINE_NIX="exec_line_nix"
	EXEC_LINE="exec_line"
	LOCAL="local"
	BASE_URI="base_uri"
	WS_TIMEUP="ws_timeup"
	CLIENT_KEY="client_key"
	CLIENT_SECRET="client_secret"
	@map=nil
	def initialize(file)
		@map=YAML.load_file file
		Ctxt.logger.debug(@map)
		if RUBY_PLATFORM =~ /mswin32/
			@map[EXEC_LINE]=@map[EXEC_LINE_WIN]
		else
			@map[EXEC_LINE]=@map[EXEC_LINE_NIX]
		end
		@map[BASE_URI]= @map[HOST]+":"+@map[PORT].to_s+"/"+@map[WS_PATH]
	end

	def [](key)
		return @map[key]
	end

end	
	
	
