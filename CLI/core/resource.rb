require './core/ctxt'
require './core/rest'
class Resource
	attr_accessor :path,:params,:resultProcessor,:result

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
		
		@result=Rest.get_resource_as_xml(buildUri())
		return @resultProcessor.process(@result)
	end
	def postResource(contents,data)
		@result=Rest.post_resource(buildUri(),contents,data)
		return @resultProcessor.process(@result)
		#return @resultProcessor.process(@result)
	end
		
end



