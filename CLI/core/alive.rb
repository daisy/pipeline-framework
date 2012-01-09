require_rel "./core/resource"
require_rel "./core/result_processor"
require "nokogiri"
class AliveResource < Resource
	def initialize
		super("/jobs",{},AliveProcessor.new)
	end
end	
class AliveProcessor < ResultProcessor
	def process(input)
		return input!=nil	
	end
end
