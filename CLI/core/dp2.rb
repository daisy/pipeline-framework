require './core/ctxt'
require './core/scripts'
require './core/alive'
class Dp2
	def initialize
		Ctxt.logger.debug("initialising dp2 link")
		@basePath=File::dirname(__FILE__)+File::SEPARATOR+".."+File::SEPARATOR
		alive!
		
	end

	#private methods
	def alive! 
		if !alive?
		
			execPath=File::expand_path(Ctxt.conf[Ctxt.conf.class::EXEC_LINE],@basePath)
			ex=IO.popen(execPath)
			#will throw execetion the command is not found
			pid =ex.pid
			Ctxt.logger().debug("ws launched with pid #{pid}")
			Ctxt.logger().info("wainting for the ws to come up...")
			wait_till_up	
			Ctxt.logger().info("ws up!")
		end	
		return true
	end

	def wait_till_up
		time_waiting=0
		time_to_wait=0.25
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

	def job(script)
		if alive?
			puts "[TODO] post job"
			#return JobResource.new.postResource(Job.fromScript(script))
		end
		return nil
	end

	def alive?	
  		return AliveResource.new.getResource 
	end	
	
	private :alive?,:alive!,:wait_till_up

end
