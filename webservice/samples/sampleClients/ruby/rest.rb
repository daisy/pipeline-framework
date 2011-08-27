# takes care of all the rest calls

module Rest

  require 'net/http'
  require 'uri'
  require 'nokogiri'
  require './settings.rb'
  require './multipart.rb'

  def get_script(script_uri)
    begin
      uri = URI.parse("#{Settings.instance.baseuri}script?id=#{script_uri}")
      response = Net::HTTP.get_response(uri)
      trace(response.body, "get script")
      if response.code == "200"
        doc = Nokogiri::XML(response.body)
        doc.remove_namespaces!
        return doc
      else
        error("Error: get script returned #{response.code.to_s}")
        return nil
      end
    rescue
      error("Error: GET #{uri.to_s} failed.")
      return nil
    end
  end
  module_function :get_script

  def get_scripts
    begin
      uri = URI.parse("#{Settings.instance.baseuri}scripts")
      response = Net::HTTP.get_response(uri)
      trace(response.body, "get scripts")
      doc = Nokogiri::XML(response.body)
      doc.remove_namespaces!
      return doc
    rescue
      error("Error: GET #{uri.to_s} failed.")
      return nil
    end
  end
  module_function :get_scripts

  def post_job(request_xml, zipfile_path)
    begin
      params = {}
      file = File.open(zipfile_path, "rb")
      params["jobData"] = file

      params["jobRequest"] = request_xml

      mp = Multipart::MultipartPost.new
      puts "hello"
      query, headers = mp.prepare_query(params)

      file.close

      uri = URI.parse("#{Settings.instance.baseuri}jobs")

      response = post_form(uri, query, headers)

      case response
        when Net::HTTPSuccess
        message("Hooray, got response: #{response.inspect}")
      when Net::HTTPInternalServerError
        raise "Server blew up"
      else
        raise "Unknown error: #{response}"
      end

      if response.code == '201'
        return true
      else
        error("Error: Job creation request returned #{response.code.to_s}")
        return false
      end

    rescue
      error("Error: POST #{uri.to_s} failed.")
      return false
    end
  end
  module_function :post_job

  def post_form(url, query, headers)
    Net::HTTP.start(url.host, url.port) {|con|
      con.read_timeout = Settings::TIMEOUT_SECONDS
      begin
        return con.post(url.path, query, headers)
      rescue => e
        error("POSTING Failed #{e}... #{Time.now}")
      end
    }
  end
  module_function :post_form

  def get_jobs
    begin
      uri = URI.parse("#{Settings.instance.baseuri}jobs")
      response = Net::HTTP.get_response(uri)
      trace(response.body, "get jobs")
      doc = Nokogiri::XML(response.body)
      doc.remove_namespaces!
      return doc
    rescue
      error("Error: GET #{uri.to_s} failed.")
      return nil
    end
  end
  module_function :get_jobs

  def get_job(id)
    begin
      uri = URI.parse("#{Settings.instance.baseuri}jobs/#{id}")
      response = Net::HTTP.get_response(uri)
      trace(response.body, "get job")
      if response.code == "200"
        doc = Nokogiri::XML(response.body)
        doc.remove_namespaces!
        return doc
      else
        error("Error: get job returned #{response.code.to_s}")
        return nil
      end
    rescue
      error("Error: GET #{uri.to_s} failed.")
      return nil
    end

  end
  module_function :get_job

  def get_job_result(id)
    begin
      uri = URI.parse("#{Settings.instance.baseuri}jobs/#{id}/result")
      response = Net::HTTP.get_response(uri)
      trace(response.body, "get job results")
      if response.code == "200"
        # TODO
        # handle file download as response
      else
        error("Error: get job returned #{response.code.to_s}")
        return nil
      end
    rescue
      error("Error: GET #{uri.to_s} failed.")
      return nil
    end
  end
  module_function :get_job_result

  def get_log(id)
    begin
      uri = URI.parse("#{Settings.instance.baseuri}jobs/#{id}/log")
      response = Net::HTTP.get_response(uri)
      trace(response.body, "get log")
      if response.code == "200"
        doc = Nokogiri::XML(response.body)
        doc.remove_namespaces!
        return doc
      else
        error("Error: get log returned #{response.code.to_s}")
        return nil
      end
    rescue
      error("Error: GET #{uri.to_s} failed.")
      return nil
    end
  end
  module_function :get_log

  def delete_job(id)
    begin
      uri = URI.parse("#{Settings.instance.baseuri}jobs/#{id}")
      request = Net::HTTP::Delete.new(uri.path)
      response = Net::HTTP.start(uri.host, uri.port) {|http| http.request(request)}
      trace(response.body, "delete job")

      if response.code == "204"
        return true
      else
        error("Error: delete job returned #{response.code.to_s}")
        return false
      end
    rescue
      error("Error: DELETE #{uri.to_s} failed.")
      return false
    end

  end
  module_function :delete_job

end