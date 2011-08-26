require 'rest'
require 'display'
require 'job_creation_wizard'

def get_scripts
  doc = Rest.get_scripts
  display_scripts(doc.xpath("//script"))
end

def get_script(uri)
  # TODO given the shortname, find the URI
  uri = "TODO"
  doc = Rest.get_script(uri)
  if doc == nil
    return
  end
  display_script(doc.xpath("//script"[0]))
end

def get_jobs
  doc = Rest.get_jobs
  if doc == nil
    return
  end
  display_jobs(doc.xpath("//job"))
end

def get_job(id)
  doc = Rest.get_job(id)
  if doc == nil
    return
  end
  display_job(doc.xpath("//job"[0]))
end

def get_log(id)
  doc = Rest.get_log(id)
  if doc == nil
    return
  end
  display_log(doc.xpath("//log"[0]))
end

def get_result(id)
  # TODO
  display_result(id, "TODO")
end

def create_job
  jobxml = job_creation_wizard
  Rest.post_job(jobxml)

end

def delete_job(id)

  # TODO check if job is elegible for deletion

  puts "Really delete this job? (Y/n)"
  input = STDIN.gets().chomp()
  if input == "Y"
    Rest.delete_job(id)
  end
end
