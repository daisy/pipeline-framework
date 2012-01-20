# takes care of all the rest calls
require 'net/http'
require 'uri'
require 'nokogiri'
require_rel './core/multipart.rb'
require_rel './core/authentication'
require_rel './core/ctxt'
class Rest
  	@@conn=nil
	def self.init_conn(authUri)
		if @@conn==nil
			@@conn= Net::HTTP.start(authUri.host,authUri.port)
		end
	end
	def self.restart()
		@@conn.finish()
		@@conn=nil
	end
	def self.get_resource(uri,time_out=nil)
		begin
   
      			authUri = URI.parse(Authentication.prepare_authenticated_uri(uri))
			self.init_conn(authUri)
			Ctxt.logger.debug(authUri) 
			if time_out==nil
				@@conn.read_timeout = Ctxt.conf[Ctxt.conf.class::TIMEOUT_SECONDS].to_s.to_i
			else
				@@conn.read_timeout=time_out
			end
			
      			request = Net::HTTP::Get.new(authUri.request_uri)
			@@conn.request(request){ |response|
				#puts "Response was #{response}"
				case response
					when Net::HTTPSuccess
						 return response.body
					when Net::HTTPInternalServerError
						return nil
					else
						return nil
				end
      			}
	    rescue Exception=>e
		#puts e.message
      		#puts e.backtrace
		puts "Error: GET #{uri.to_s} failed."
	       return nil
	    end
      	##response = Net::HTTP.get_response(authUri)
      	#puts "Response was #{response}"

	end


  # TODO make this more WS-agnostic (factor out post field names)
  def self.post_resource(uri, request_contents, data)
    begin
      authUri = URI.parse(Authentication.prepare_authenticated_uri(uri))
      self.init_conn(authUri)
      request = Net::HTTP::Post.new(authUri.request_uri)

      # send the request as the body
      if data == nil
        request.body = request_contents
	Ctxt.logger.debug(request.body)	
        response = @@conn.request(request)
      # else attach the data as a file in a multipart request
      else
        params = {}
        params["job-data"] = data
        params["job-request"] = request_contents

        mp = Multipart::MultipartPost.new
        query, headers = mp.prepare_query(params)
        response = post_form(authUri, query, headers)

      end
      Ctxt.logger.debug("Response was #{response}")

      case response
        when Net::HTTPCreated
          return response.body
        when Net::HTTPInternalServerError
          return nil
        else
          return nil
      end
    rescue Exception => e
      #puts e.backtrace	
      #puts e.message 
      raise RuntimeError,"Error: POST #{uri.to_s} failed."
      
    end
  end

  def self.delete_resource(uri)
    begin
      authUri = URI.parse(Authentication.prepare_authenticated_uri(uri))
      request = Net::HTTP::Delete.new(authUri.request_uri)
      self.init_conn(authUri)	
      response = @@conn.request(request)

      Ctxt.logger.debug("Response was #{response}")

      case response
        when Net::HTTPNoContent
          return true
        when Net::HTTPInternalServerError
          return false
        else
          return false
      end
    rescue
      error("Error: DELETE #{uri.to_s} failed.")
      return false
    end

  end

  def self.post_form(url, query, headers)
      @@conn.read_timeout = Ctxt.conf[Ctxt.conf.class::TIMEOUT_SECONDS].to_s.to_i
      begin
        return @@conn.post(url.request_uri, query, headers)
      rescue => e
        puts "POST Failed #{e}... #{Time.now}"
      end
  end

end
