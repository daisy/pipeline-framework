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
  if name == ""
    error "'Get script' requires a name"
    return
  end
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
  doc = Rest.get_log(id)
  if doc == nil
    return
  end
  display_log(doc.xpath("//log")[0])
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

  puts "Really delete this job? (Y/n)"
  input = STDIN.gets().chomp()
  if input == "Y"
    Rest.delete_job(id)
  end
end

def run_preset_job1
  jobxml = "
<jobRequest xmlns='http://www.daisy.org/ns/pipeline/data'>
  <script href='http://www.daisy.org/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl'/>
  <input name='source'>
    <file src='./dtbook-basic.xml'/>
  </input>
  <option name='opt-mods-filename'>the-mods-file.xml</option>
  <option name='opt-css-filename'>the-css-file.css</option>
  <option name='opt-zedai-filename'>the-zedai-file.xml</option>
</jobRequest>
"

  zippath = File.expand_path(File.dirname($0)) + "/test/dtbook-basic.zip"

  Rest.post_job_multipart(jobxml, zippath)

end

def run_preset_job2
  jobxml = "
<jobRequest xmlns='http://www.daisy.org/ns/pipeline/data'>
  <script href='http://www.daisy.org/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl'/>
  <input name='source'>
    <docwrapper>
<dtbook xmlns='http://www.daisy.org/z3986/2005/dtbook/' version='2005-3' xml:lang='en-US'>
    <head>
        <meta content='pipeline2-dtbook-test-20110301-basic' name='dtb:uid'/>
        <meta content='Pipeline 2 DTBook Test Content: Basic' name='dc:Title'/>
        <meta name='dc:Creator' content='Marisa D.'/>
        <meta content='2011-03-01' name='dc:Date'/>
        <meta name='dc:Publisher' content='Marisa D.'/>
        <meta content='pipeline2-dtbook-test-20110301-basic' name='dc:Identifier'/>
        <meta content='en-US' name='dc:Language'/>
    </head>
    <!-- test comment -->
    <book>
        <frontmatter>
            <doctitle>Pipeline 2 DTBook Test Content: Basic</doctitle>
            <docauthor>Marisa D.</docauthor>
        </frontmatter>
        <bodymatter>
            <level1>
                <h1>Introduction</h1>
                <p><sent>The DAISY Pipeline 2 is an ongoing project to develop a next generation
                        framework for automated production of accessible materials for people with
                        print disabilities.</sent>
                    <sent>It is the follow-up and total redesign of the original DAISY Pipeline 1
                        project.</sent></p>
            </level1>
            <level1>
                <h1>About the Pipeline</h1>
                <p><sent>The overarching principle of the Pipeline 2 is to adopt recent
                        platform-neutral standards (and off-the-shelf implementations of those
                        standards) at the heart of a comprehensive framework, which will:</sent>
                    <list type='ul'>
                        <hd>The Heading For The List</hd>
                        <li>minimize the development and maintenance cost, allowing developers to
                            ultimately focus more on actual transformations rather than the engine
                            that drives the transformations.</li>
                        <li>lower the framework learning curve</li>
                        <li>increase interoperability with the heterogeneous production
                            workflows</li>
                        <li>increase the likelihood of re-use in both open source and commercial
                            applications.</li>
                    </list>
                </p>

            </level1>

        </bodymatter>
    </book>
</dtbook>

    </docwrapper>
  </input>
  <!-- no options specified, so the script's default values will be used -->
</jobRequest>
"


  Rest.post_job_xml(jobxml)

end


def result_preset_1
  jobs_doc = Rest.get_jobs
  first_job_id = jobs_doc.xpath(".//job")[0]['id']

  get_result(first_job_id)

end