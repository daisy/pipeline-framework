require './core/resource'
class JobResource < Resource
	def initialize
		super("/jobs",{},JobPostResultProcessor.new)
	end	
end

class JobStatusResource < Resource
	def initialize(id,seq=0)
		super("/jobs",{:id=>id,:seq=>seq},JobStatusResultProcessor.new)
	end
	def buildUri
    		uri = "#{Ctxt.conf[Ctxt.conf.class::BASE_URI]}#{@path}/#{@params[:id]}"
		Ctxt.logger.debug("URI:"+uri)
		uri
	end
			
end

class JobStatusResultProcessor < ResultProcessor
	def process(input)
		raise "Empty job result from server ",RuntimeError if input==nil
		doc=input
		
		doc.remove_namespaces!
		xjob=doc.at_xpath("//job")
		job=Job.fromXml(xjob)
		Ctxt.logger.debug(job.to_s)
		return  job
	end
end

class JobPostResultProcessor < ResultProcessor
	def process(input)
		if input==nil
			raise "Error submitting job"
		end
		id=input.split(/\//)[-1]
		puts "Job with ID #{id} submitted"
		return id 
	end
end
class Message
	attr_accessor :msg,:level
	def to_s
			return "#{@level} - #{@msg[0..200]}"
	end
end
class Job

	attr_accessor :id,:status,:script,:result,:messages,:log
	def initialize(id)	
		@id=id
		@messages=[]
	end
	def self.fromXml(element)
		job=Job.new(element.attr("id"))
		job.status=element.attr("status")
	
		xscript=element.at_xpath("./script")
		xresult=element.at_xpath("./result")
		xlog=element.at_xpath("./log")
		xmessages=element.xpath("./messages/message")
		xmessages.each{|xmsg|
			msg= Message.new 
			msg.msg=xmsg.content
			msg.level=xmsg.attr("level")
			job.messages.push(msg)		
		}
		job.script=xscript.attr("href") if xscript!=nil
		job.result=xresult.attr("href") if xresult!=nil
		job.log=xlog.attr("href") if xlog!=nil
	
		#TODO: messages	
		return job
	end

	def to_s
		s="Job Id: #{@id}\n"
		s+="\t Status: #{@status}\n"
		s+="\t Script: #{@script}\n" if @script!=nil
		s+="\t Result: #{@result}\n" if @result!=nil
		s+="\t Log: #{@log}\n" if @log!=nil
		s+="\n"
		return s	
	end	

end
