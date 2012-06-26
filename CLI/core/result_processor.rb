class ResultProcessor


	def process(input)
		raise NotImplementedError 	
	end

	def notFound(err,resource)
		Ctxt.logger.debug("WS 404: "+resource.buildUri)
		raise RuntimeError, "Resource not found"
	end
	def error(err,resource)
		Ctxt.logger.debug("Generic error: "+resource.buildUri)
		raise RuntimeError, "WS Failure"
	end
	def internalError(err,resource)
		Ctxt.logger.warn("WS 500 :"+resource.buildUri)
		Ctxt.logger.warn("WS 500 :"+err.text)
		#TODO this should be a xml treat it!
		raise RuntimeError, "WS Internal Failure:\n\n#{err.text}"
	end
end
