require './rest'
module Resources
  module_function
  BASEURI = "http://localhost:8182/ws"
#  BASEURI = "http://localhost:9000/web/service"

  def get_scripts
    uri = "#{BASEURI}/scripts"
    doc = Rest.get_resource_as_xml(uri)
    return doc
  end

  def get_script(script_uri)
    uri = "#{BASEURI}/script?id=#{script_uri}"
    doc = Rest.get_resource_as_xml(uri)
    return doc
  end

  def get_jobs
    uri = "#{BASEURI}/jobs"
    doc = Rest.get_resource_as_xml(uri)
    return doc
  end

  def get_job(id)
    uri = "#{BASEURI}/jobs/#{id}"
    doc = Rest.get_resource_as_xml(uri)
    return doc
  end

  def get_log(id)
    uri = "#{BASEURI}/jobs/#{id}/log"
    doc = Rest.get_resource(uri)
    return doc
  end

  def get_result(id)
    uri = "#{BASEURI}/jobs/#{id}/result"
    doc = Rest.get_resource(uri)
    return doc
  end

  def post_job(request, data)
    uri = "#{BASEURI}/jobs"
    job_id = Rest.post_resource(uri, request, data);
    return job_id
  end

  def delete(id)
    uri = "#{BASEURI}/jobs/#{id}"
    success = Rest.delete_resource(uri)
    return success
  end

  def get_job_status(id)
    doc = get_job(id)
    doc.remove_namespaces!
    return doc.xpath(".//job")[0]['status']
  end


end
