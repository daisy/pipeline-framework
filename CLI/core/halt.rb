require_rel './core/resource'
class HaltResource < Resource
	def initialize(key)
		super("/admin/halt",{:key=>key},HaltResourceProcessor.new)
	end	
	def buildUri
    		uri = "#{Ctxt.conf[Ctxt.conf.class::BASE_URI]}#{@path}/#{@params[:key]}"
		Ctxt.logger.debug("URI:"+uri)
		uri
	end
end

class HaltResourceProcessor < ResultProcessor
	def process(input)
		return  true
	end
end
