require "nokogiri"
require_rel "./commands/command"
require_rel "./core/resource"
require_rel "./core/result_processor"
require_rel "./core/helpers"

# In this file:
# Script
# ScriptsResource
# ScriptsResultProcessor

class Script
	attr_accessor :href,:nicename,:desc,:opts,:inputs,:outputs,:local

	def initialize(href,nicename,desc)
		@href=href
		@nicename=nicename
		@desc=desc
		@opts=[]
		@inputs=[]
		@outputs=[]
		@local=true
	end
	def clone
		clone=Script.new(@href,@nicename,@desc)
		clone.opts=@opts
		clone.inputs=@inputs.clone
		clone.outputs=@outputs.clone
		return clone
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

	def to_xml_request
	
#		<jobRequest xmlns='http://www.daisy.org/ns/pipeline/data'>
#		    <script href='http://www.daisy.org/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl'/>
#		    <input name='source'>
#			<file src='./dtbook-basic.xml'/>
#		    </input>
#		    <option name='opt-mods-filename'>the-mods-file.xml</option>
#		    <option name='opt-css-filename'>the-css-file.css</option>
#		    <option name='opt-zedai-filename'>the-zedai-file.xml</option>
#		</jobRequest>
		doc=XmlBuilder.new(self).xml
		return doc.to_s 
		
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

class XmlBuilder
	NS='http://www.daisy.org/ns/pipeline/data'
	E_JOB_REQUEST='jobRequest'
	E_SCRIPT='script'
	E_INPUT='input'
	E_FILE='file'
	E_OPTION='option'
	A_HREF='href'
	A_NAME='name'
	A_XMLNS='xmlns'
	A_SRC='src'

		
	def initialize(script)
		@script=script
	end
	def xml
		@doc=Nokogiri::XML::Document.new
		@doc << @doc.create_element(E_JOB_REQUEST,{A_XMLNS=>NS})
		@doc.root << @doc.create_element(E_SCRIPT,{A_HREF=>@script.href})
		addInputs
		addOutputs
		addOptions
		return @doc
	end	

	def addOptions
		@script.opts.each{ |opt|
			raise "missing required option #{opt[:name]}" if !(opt[:value]!=nil && !opt[:value].empty?) && opt[:required]==('true')
			if (opt[:value]!=nil && !opt[:value].empty?)
				n=@doc.create_element(E_OPTION,{A_NAME=>opt[:name]})
				value=opt[:value]
				value = Helpers.path_to_uri(value,@script.local) if opt[:type]=="anyFileURI" || opt[:type] == "anyDirURI"
				n.content=value
				@doc.root << n
			end
		}
	end
	def addInputs

		@script.inputs.each{ |input|
			raise "Input empty: #{input[:name]}" if !(input[:value]!=nil && !input[:value].empty?)
			values=input[:value]
			in_elem=@doc.create_element(E_INPUT,{A_NAME=>input[:name]})
			
			@doc.root << in_elem
			values.each{|file| in_elem << @doc.create_element(E_FILE,{A_SRC=>Helpers.path_to_uri(file,@script.local)})} 
		}
	end
	def addOutputs
		#TODO: not sure about how to hadle outputs... specially sequences, now relaying in the ws behaviour 
#		@script.outputs.each{ |output|
#			raise "Input empty: #{output[:name]}" if !(output[:value]!=nil && !output[:value].empty?)
#			values=output[:value]
#			if values.class != Array
#				values=[output[:value]] 
#			end
#			in_elem=@doc.create_element(E_INPUT,{A_NAME=>output[:name]})
#			
#			@doc.root << in_elem
#			values.each{|file| in_elem << @doc.create_element(E_FILE,{A_SRC=>file})} 
#		}
	end
end


#def test
#	scr=Script.new("http://google.com",nil,nil)
#	scr.opts.push({})
#	scr.inputs.push({})
#	scr.opts[0][:required]='true'
#	scr.opts[0][:name]='paco'
#	scr.opts[0][:value]='ratata'
#	scr.inputs[0][:name]='source'
#	scr.inputs[0][:value]='file1.xom'
#	puts XmlBuilder.new(scr).xml
#	
#end
