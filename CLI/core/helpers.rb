require_rel './core/ctxt'
module Helpers 

  	module_function
	def normalise_name(str) 
		if str != nil
			str=str.downcase
			str=str.gsub(/\s/,'-')
		else
			Ctxt.logger.warning("name is nil")
		end
		return str
	end
		
	def path_to_uri(path,is_local)
		Ctxt.logger.debug("transforming path uri")
		if is_local	
			uri=File.expand_path(path).gsub(/\\/,'/')
			uri='/'+uri if uri[0]!='/'[0]
			uri="file:"+uri
					
		else
			uri=path
		end
		Ctxt.logger.debug("uri: #{uri}")
		return uri
	end
end
