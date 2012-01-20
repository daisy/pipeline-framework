require_rel "./core/resource"
require_rel "./core/result_processor"
require "nokogiri"
class AliveResource < Resource
	def initialize
		super("/jobs",{},AliveProcessor.new)
	end
	def getResource
		
		@result=Rest.get_resource(buildUri(),3)
		return @resultProcessor.process(@result)
	end
end	
class AliveProcessor < ResultProcessor
	def process(input)
		return input!=nil	
	end
end
