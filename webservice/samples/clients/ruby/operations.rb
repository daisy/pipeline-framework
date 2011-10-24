require './rest.rb'
require './display.rb'
require './job_creation_wizard.rb'
require './utils.rb'
require 'nokogiri'

def get_scripts
  doc = Rest.get_scripts
  if doc == nil
    return
  end
  display_scripts(doc.xpath("//script"))
end

def get_script_by_id(id)
  if id == ""
    error "'Get script' requires an ID"
    return
  end
  doc = Rest.get_script(id)
  if doc == nil
    return
  end
  display_script(doc.xpath("//script")[0])
end

def get_script_by_name(name)
  if name == ""
    error "'Get script' requires a name"
    return
  end
  uri = get_uri_from_shortname(name)
  get_script_by_id(uri)
end

def get_jobs
  doc = Rest.get_jobs
  if doc == nil
    return
  end
  display_jobs(doc.xpath("//job"))
end

def get_job(id)
  if id == ""
    error "'Get job' requires an ID"
    return
  end
  doc = Rest.get_job(id)
  if doc == nil
    return
  end
  display_job(doc.xpath("//job")[0])
end

def get_log(id)
  if id == ""
    error "'Get log' requires an ID"
    return
  end

  display_log(Rest.get_log(id), id)
end

def get_result(id)
  if id == ""
    error "'Get result' requires an ID"
    return
  end
  response = Rest.get_job_result(id)
  if response == nil
    error "Result is nil"
    return
  end
  path = "/tmp/#{id}.zip"
  open(path, "wb") { |file|
    file.write(response)
  }
  display_result(id, path)
end

def create_job
  job_creation_wizard
end

def delete_job(id)
  if id == ""
    error "'Delete job' requires an ID"
    return
  end

  # first check the job's status to make sure it can be deleted
  status = get_job_status(id)
  if status != "DONE"
    error "'Delete job' requires that the job be done. The job is currently #{status}."
    return
  end

  Rest.delete_job(id)

end

