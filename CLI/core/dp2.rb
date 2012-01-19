require_rel './core/ctxt'
require_rel './core/scripts'
require_rel './core/alive'
require_rel './core/job'
require "open3"
#TODO asking if the service is alive before every call may not be a good idea, store that it's alive once and asume it in next calls
class Dp2
	def initialize
		Ctxt.logger.debug("initialising dp2 link")
		@basePath=File::dirname(__FILE__)+File::SEPARATOR+".."+File::SEPARATOR
		alive!
		
	end

	#private methods
	def alive! 
		if !alive?
			
			if Ctxt.conf[Ctxt.conf.class::LOCAL] == true
				execPath=File::expand_path(Ctxt.conf[Ctxt.conf.class::EXEC_LINE],@basePath)
				#
				ex=IO.popen(execPath ) 

				#system('start '+execPath)
				#will throw execetion the command is not found
				pid =ex.pid
				Ctxt.logger().debug("ws launched with pid #{pid}")
				Ctxt.logger().debug("waiting for the ws to come up...")
				puts "Waiting for the WS to come up"
				wait_till_up	
				Ctxt.logger().debug("ws up!")
				puts("The daisy pipeline 2 WS is up!")
			else
				raise RuntimeError,"Unable to reach the WS"
			end
		end	
		return true
	end

	def wait_till_up
		time_waiting=0
		time_to_wait=0.33
		while !alive?  && time_waiting<Ctxt.conf[Ctxt.conf.class::WS_TIMEUP]
			#Ctxt.logger.debug("going to sleep #{time_to_wait}")
			sleep time_to_wait
			time_waiting+=time_to_wait
			#Ctxt.logger.debug("time_waiting #{time_waiting}")
		end
		raise RuntimeError,"WS is not up and I have been waiting for #{time_waiting} s" if !alive?
	end
	#public methods
	def scripts
		if alive?
			return ScriptsResource.new.getResource
		end
		return nil
	end

	def job(script,data,wait)
		job=nil
		msgIdx=0
		if alive?
			job=JobResource.new.postResource(script.to_xml_request,data)
			if wait==true
				begin
					sleep 1.5 
					job=job_status(job.id,msgIdx)
					job.messages.each{|msg| puts msg.to_s}
					if job.messages.size > 0
						msgIdx=(Integer(job.messages[-1].seq)+1).to_s
					end
					Ctxt.logger.debug("msg idx #{msgIdx}")	
				end while job.status=='RUNNING' 
			end 
		end
		return job
	end

	def job_status(id,msgSeq=0)
		#if alive?
			return JobStatusResource.new(id,msgSeq).getResource
		#end
		return nil

	end

	def job_statuses
		return JobsStatusResource.new.getResource
	end

	def delete_job(id)
		return DeleteJobResource.new(id).deleteResource	
	end	
	def job_zip_result(id,outpath)
		return JobResultZipResource.new(id,outpath).getResource	
	end	
	def alive?	
  		return AliveResource.new.getResource 
	end	
	
	private :alive?,:alive!,:wait_till_up

end
