# takes care of all the rest calls
require 'net/http'
require 'uri'
require 'nokogiri'
require_rel './core/multipart.rb'
require_rel './core/authentication'
require_rel './core/ctxt'
module Rest
  module_function

  def get_resource(uri)
    begin
      authUri = URI.parse(Authentication.prepare_authenticated_uri(uri))
      Ctxt.logger.debug(authUri)
      response = Net::HTTP.get_response(authUri)

      #puts "Response was #{response}"

      case response
        when Net::HTTPSuccess
          return response.body
        when Net::HTTPInternalServerError
          return nil
        else
          return nil
      end

    rescue
      #puts "Error: GET #{uri.to_s} failed."
      return nil
    end
  end

  def get_resource_as_xml(uri)
    resource = get_resource(uri)
    if resource == nil
      return nil
    end
    doc = Nokogiri.XML(resource) do |config|
      config.default_xml.noblanks
    end
    return doc
  end

  # TODO make this more WS-agnostic (factor out post field names)
  def post_resource(uri, request_contents, data)
    begin
      authUri = URI.parse(Authentication.prepare_authenticated_uri(uri))
      request = Net::HTTP::Post.new(authUri.request_uri)

      # send the request as the body
      if data == nil
        request.body = request_contents
	Ctxt.logger.debug(request.body)	
        response = Net::HTTP.start(authUri.host, authUri.port) {|http| http.request(request)}

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
          return response.get_fields('content-location')[0]
        when Net::HTTPInternalServerError
          return nil
        else
          return nil
      end
    rescue Exception => e
      puts e 
      puts "Error: POST #{uri.to_s} failed."
      return nil
    end
  end

  def delete_resource(uri)
    begin
      authUri = URI.parse(Authentication.prepare_authenticated_uri(uri))
      request = Net::HTTP::Delete.new(authUri.request_uri)
      response = Net::HTTP.start(authUri.host, authUri.port) {|http| http.request(request)}

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

  def post_form(url, query, headers)
    Net::HTTP.start(url.host, url.port) {|con|
      con.read_timeout = Ctxt.conf[Ctxt.conf.class::TIMEOUT_SECONDS].to_s.to_i
      begin
        return con.post(url.request_uri, query, headers)
      rescue => e
        puts "POST Failed #{e}... #{Time.now}"
      end
    }
  end

end
