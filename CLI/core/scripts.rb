require_rel "./commands/command"
require_rel "./core/resource"
require_rel "./core/result_processor"
require_rel "./core/helpers"

# In this file:
# Script
# ScriptsResource
# ScriptsResultProcessor

class Script
	attr_accessor :href,:nicename,:desc,:opts,:inputs,:outputs,:local,:uri

	def initialize(href,nicename,desc,id,uri)
		@href=href
		@nicename=nicename
		@desc=desc
		@id=id
		@uri=uri
		@opts=[]
		@inputs=[]
		@outputs=[]
		@local=Ctxt.conf[Ctxt.conf.class::LOCAL]==true
	end
	def clone
		clone=Script.new(@href,@nicename,@desc,@id,@uri)
		clone.opts=@opts
		clone.inputs=@inputs.clone
		clone.outputs=@outputs.clone
		clone.outputs=@outputs.clone
		return clone
	end

	def to_s
		s="Name: #{@nicename}\n"
		s+="Description: #{@desc}\n"
		s+="HREF: #{@href}\n"
		s+="uri: #{@uri}"
		s+="id: #{@id}"
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
			script=Script.new(node.attributes["href"],Helpers.normalise_name(XPath.first(node,"./ns:nicename",Resource::NS).text),XPath.first(node,"./ns:description",Resource::NS).text,node.attributes["id"],node.attributes["script"])
			#options	
			XPath.each(node,"./ns:option",Resource::NS){|option|
				opt={:name=>option.attributes["name"],
					:desc=>option.attributes["desc"],
					:mediaType=>option.attributes["mediaType"],
					:name=>option.attributes["name"],
					:required=>option.attributes["required"],
					:type=>option.attributes["type"],
				}
				script.opts.push(opt)

			}
			
			#outputs				
			XPath.each(node,"./ns:output",Resource::NS){ |output|
				out={:name=>output.attributes["name"],
					:desc=>output.attributes["desc"],
					:mediaType=>output.attributes["mediaType"],
					:sequenceAllowed=>output.attributes["sequenceAllowed"],
				}
				script.outputs.push(out)
		
			}	
			#inputs				
			XPath.each(node,"./ns:input",Resource::NS) {|input|
				inp={:name=>input.attributes["name"],
					:desc=>input.attributes["desc"],
					:mediaType=>input.attributes["mediaType"],
					:sequenceAllowed=>input.attributes["sequenceAllowed"],
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
		raise RuntimeError,"scripts returned an empty result" if input==nil
		map={}
		doc= Document.new input
		scripts=XPath.match(doc,"//ns:script",Resource::NS)
		scripts.to_a.each { |xscript| 
			script=Script.fromXmlElement(xscript)
			map[script.nicename]=script }
		return map
	end
end


class ScriptResource < Resource
	def initialize(href)
		super("/scripts",{:href=>href},ScriptResultProcessor.new)
	end	
	def buildUri
    		uri = @params[:href]
		Ctxt.logger.debug("URI:"+uri)
		uri
	end
end

class ScriptResultProcessor < ResultProcessor
	def process(input)
		raise RuntimeError,"script returned an empty result" if input==nil
#		
		doc= Document.new input
		xscript = XPath.first(doc,"//ns:script",Resource::NS)
		script=Script.fromXmlElement(xscript)
		return script
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
		@doc= Document.new
		jobReqElem=Element.new E_JOB_REQUEST
		jobReqElem.add_namespace(NS);
		jobReqElem.add_element E_SCRIPT,{A_HREF=>@script.href}
		@doc << jobReqElem
		addInputs
		addOutputs
		addOptions
		return @doc
	end	

	def addOptions
		@script.opts.each{ |opt|
			raise "missing required option #{opt[:name]}" if !(opt[:value]!=nil && !opt[:value].empty?) && opt[:required]==('true')
			if (opt[:value]!=nil && !opt[:value].empty?)
				n=Element.new E_OPTION
				n.attributes[A_NAME]=opt[:name];
				value=opt[:value]
				value = Helpers.path_to_uri(value,@script.local) if opt[:type]=="anyFileURI" || opt[:type] == "anyDirURI"
				n.text=value
				@doc.root << n
			end
		}
	end
	def addInputs

		@script.inputs.each{ |input|
			raise "Input empty: #{input[:name]}" if !(input[:value]!=nil && !input[:value].empty?)
			values=input[:value]
			in_elem=Element.new E_INPUT
			in_elem.attributes[A_NAME]=input[:name]
			@doc.root << in_elem
			values.each{|file| in_elem.add_element E_FILE,{A_SRC=>Helpers.path_to_uri(file,@script.local)}} 
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


