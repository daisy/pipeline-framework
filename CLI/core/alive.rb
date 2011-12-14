require "./core/resource"
require "./core/result_processor"
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
