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

def get_script(name)
  uri = get_uri_from_shortname(name)
  doc = Rest.get_script(uri)
  if doc == nil
    return
  end
  display_script(doc.xpath("//script")[0])
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
  display_job(doc.xpath("//job")[0])
end

def get_log(id)
  doc = Rest.get_log(id)
  if doc == nil
    return
  end
  display_log(doc.xpath("//log")[0])
end

def get_result(id)
  # TODO store result
  display_result(id, "TODO")
end

def create_job
  job_creation_wizard
end

def delete_job(id)

  # TODO check if job is eligible for deletion

  puts "Really delete this job? (Y/n)"
  input = STDIN.gets().chomp()
  if input == "Y"
    Rest.delete_job(id)
  end
end

def run_preset_job
  jobxml = "
<jobRequest xmlns='http://www.daisy.org/ns/pipeline/data'>
  <script href='http://www.daisy.org/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl'/>
  <input name='source'>
    <file src='./dtbook-basic.xml'/>
  </input>
  <option name='opt-output-dir'>/tmp/d2z</option>
  <option name='opt-mods-filename'>m.xml</option>
  <option name='opt-css-filename'>c.css</option>
  <option name='opt-zedai-filename'>z.xml</option>
</jobRequest>
"

  zippath = File.expand_path(File.dirname($0)) + "/test/dtbook-basic.zip"

  Rest.post_job(jobxml, zippath)

end