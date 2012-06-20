require_rel './core/ctxt'
require_rel './core/rest'
class Resource
	attr_accessor :path,:params,:resultProcessor,:result
	NS={"ns"=>'http://www.daisy.org/ns/pipeline/data'}
	def initialize(path,params,resultProcessor)	
		@path=path
		@params=params
		@resultProcessor=resultProcessor
		
	end

	def buildUri
    		uri = "#{Ctxt.conf[Ctxt.conf.class::BASE_URI]}#{@path}"
		Ctxt.logger.debug(uri)
		uri
	end	
	def getResource
		
		@result=Rest.get_resource(buildUri())
		return preProcess(@result)
	end
	def postResource(contents,data)
		@result=Rest.post_resource(buildUri(),contents,data)
		return preProcess(@result)
	end
	def deleteResource
		@result=Rest.delete_resource(buildUri())
		return preProcess(@result)
	end	
	def preProcess(result)
		if result.is_a? RestError
			case result.err 
				when Net::HTTPNotFound
					return @resultProcessor.notFound(result,self)
				else
					return @resultProcessor.internalError(result,self)
			end
		else
			return @resultProcessor.process(result)
		end
	end
end



