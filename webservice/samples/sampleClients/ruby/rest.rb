###################################
# takes care of all the rest calls
##################################
module Rest

  require 'net/http'
  require 'uri'
  require 'nokogiri'

  def get_script(scriptUri)
    begin
      uri = URI.parse("#{$baseuri}script?id=#{scriptUri}")
      response = Net::HTTP.get_response(uri)
      trace(response.body, "get script")
      if response.code == "200"
        doc = Nokogiri::XML(response.body)
        doc.remove_namespaces!
        return doc
      else
        error("Error: get script returned #{response.code.to)s}")
        return nil
      end
    rescue
      error("Error: GET #{uri.to_s} failed.")
      return nil
    end
  end

  def get_scripts
    begin
      uri = URI.parse("#{$baseuri}scripts")
      response = Net::HTTP.get_response(uri)
      trace(response.body, "get converters")
      doc = Nokogiri::XML(response.body)
      doc.remove_namespaces!
      return doc
    rescue
      error("Error: GET #{uri.to_s} failed.")
      return nil
    end
  end

  # TODO multipart
  def post_job(job_request_xml_string, zipfile)
    begin
      uri = URI.parse("#{$baseuri}jobs")
      request = Net::HTTP::Post.new(uri.path)
      request.body = job_request_xml_string

      trace(request.body, "post job request")
      trace(response.body, "post job response")

      if response.code == '201'
        return true
      else
        error("Error: Job creation request returned #{response.code.to_s}")
        return false
      end

    rescue
      error("Error: POST #{uri.to_s} failed.")
      return nil
    end
  end

  def get_jobs
    begin
      uri = URI.parse("#{$baseuri}jobs")
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

  def get_job(id)
    begin
      uri = URI.parse("#{$baseuri}jobs/#{id}")
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

  def get_job_results(id)
    begin
      uri = URI.parse("#{$baseuri}jobs/#{id}/result")
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

  def get_log(id)
    begin
      uri = URI.parse("#{$baseuri}jobs/#{id}/log")
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

  def delete_job(id)
    begin
      uri = URI.parse("#{$baseuri}jobs/#{id}")
      request = Net::HTTP::Delete.new(uri.path)
      response = Net::HTTP.start(uri.host, uri.port) {|http| http.request(request)}
      trace(response.body, "delete job")

      if response.code == "204"
        return true
      else
        error("Error: delete job returned #{response.code.to_s}")
        return nil
      end
    rescue
      error("Error: DELETE #{uri.to_s} failed.")
      return nil
    end

  end
end