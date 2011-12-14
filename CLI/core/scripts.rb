require "nokogiri"
require "./command"
require "./core/resource"
require "./core/result_processor"
require "./core/helpers"

# In this file:
# Script
# ScriptsResource
# ScriptsResultProcessor

class Script
	attr_accessor :href,:nicename,:desc,:opts,:inputs,:outputs

	def initialize(href,nicename,desc)
		@href=href
		@nicename=nicename
		@desc=desc
		@opts=[]
		@inputs=[]
		@outputs=[]
	end

	def to_s
		s="Name: #{@nicename}\n"
		s+="Description: #{@desc}\n"
		s+="URI: #{@href}\n"
		s+="\nInputs:\n"
		@inputs.each{|input| 
			s+="\t* #{input[:name]}:\n"
			s+="\t\t Desc:#{input[:desc]}\n"
			s+="\t\t Media type:#{input[:mediaType]}\n"
			s+="\t\t Sequence allowed:#{input[:sequenceAllowed]}\n"
		}
		s+="\nOutputs:\n"
		@outputs.each{ |output| 
			s+="\t* #{output[:name]}:\n"
			s+="\t\t Desc:#{output[:desc]}\n"
			s+="\t\t Media type:#{output[:mediaType]}\n"
			s+="\t\t Sequence allowed:#{output[:sequenceAllowed]}\n"
		}
		s+="\nOptions:\n"
		@opts.each{ |option| 
			s+="\t* #{option[:name]}:\n"
			s+="\t\t Desc:#{option[:desc]}\n"
			s+="\t\t Media type:#{option[:mediaType]}\n"
			s+="\t\t Required:#{option[:required]}\n"
			s+="\t\t Type:#{option[:type]}\n"
		}
		return s
	end

	def self.fromXmlElement(node)
			script=Script.new(node.attr("href"),Helpers.normalise_name(node.at_xpath("./nicename").content),node.at_xpath("./description").content)
			#options	
			node.xpath("./option").to_a.each {|option|
				opt={:name=>option.attr("name"),
					:desc=>option.attr("desc"),
					:mediaType=>option.attr("mediaType"),
					:name=>option.attr("name"),
					:required=>option.attr("required"),
					:type=>option.attr("type"),
				}
				script.opts.push(opt)

			}
			
			#outputs				
			node.xpath("./output").to_a.each {|output|
				out={:name=>output.attr("name"),
					:desc=>output.attr("desc"),
					:mediaType=>output.attr("mediaType"),
					:sequenceAllowed=>output.attr("sequenceAllowed"),
				}
				script.outputs.push(out)
		
			}	
			#inputs				
			node.xpath("./input").to_a.each {|input|
				inp={:name=>input.attr("name"),
					:desc=>input.attr("desc"),
					:mediaType=>input.attr("mediaType"),
					:sequenceAllowed=>input.attr("sequenceAllowed"),
				}
				script.inputs.push(inp)
		
			}	
			Ctxt.logger.debug("inserting script href: #{script.href} #{script.nicename} ")
			return script	
	end
end




class ScriptsResource < Resource
	def initialize
		super("/scripts",{},ScriptsResultProcessor.new)
	end	
end

class ScriptsResultProcessor < ResultProcessor
	def process(input)
		doc=input
		doc.remove_namespaces!
		scripts=doc.xpath("//script")
		map={}
		scripts.to_a.each { |xscript| 
			script=Script.fromXmlElement(xscript)
			map[script.nicename]=script }
		return map
	end
end
