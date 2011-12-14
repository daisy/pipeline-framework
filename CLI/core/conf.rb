require 'yaml'
require './core/ctxt'
class Conf
	#KEYS
	PORT="port"
	HOST="host"
	WS_PATH="ws_path"
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
		@map[BASE_URI]= @map[HOST]+":"+@map[PORT].to_s+"/"+@map[WS_PATH]
	end

	def [](key)
		return @map[key]
	end

end	
	
	
