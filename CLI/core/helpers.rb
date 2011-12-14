require './core/ctxt'
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
end
