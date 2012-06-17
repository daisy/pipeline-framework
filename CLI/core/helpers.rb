require_rel './core/ctxt'
module Helpers 

  	module_function
	def normalise_name(str) 
		if str != nil
			str=str.downcase
			str=str.gsub(/\s/,'-')
		else
			Ctxt.logger.warning("name is nil")
		end
		return str
	end
		
	def path_to_uri(path,is_local)
		Ctxt.logger.debug("transforming path uri")
		if is_local	
			uri=File.expand_path(path).gsub(/\\/,'/')
			uri='/'+uri if uri[0]!='/'[0]
			uri="file:"+uri
					
		else
			uri=path.gsub(/\\/,'/')
		end
		Ctxt.logger.debug("uri: #{uri}")
		return URI.escape(uri)
	end
	def last_id_store(job)
		path=File.dirname(__FILE__)+"/../.lastid"
		File.open(path, 'w') {|f| f.write(job.id) }
	end
	def last_id_read
		
		path=File.dirname(__FILE__)+"/../.lastid"
		id=nil
		if File.exists?(path)
			f=File.open(path, 'r') 
			id=f.gets() 
			f.close
		end
		return id
	end
end
