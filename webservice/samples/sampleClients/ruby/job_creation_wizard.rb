require './script_arg.rb'
require 'nokogiri'

# guide the user through the process of creating a job
def job_creation_wizard

  puts "Please enter the following information:"
  puts "Script name: "
  name  = STDIN.gets().chomp()
  scripturi = get_uri_from_shortname(name)

  doc = Rest.get_script(scripturi)

  input_args_data = prompt_for_input(doc)
  option_args_data = prompt_for_options(doc)

  jobdoc = create_job_xml(scripturi, input_args_data, option_args_data)

  trace(jobdoc.to_s,"JOBREQUEST")

  puts "Please enter the path to the zip file containing the data for this job:"
  zippath = STDIN.gets().chomp()

  res = Rest.post_job(jobdoc.to_s, zippath)

  if res == true
    message("Job created")
  else
    message("Job not created")
  end

end

# return an array of Arg objects
def prompt_for_input(doc)
  input_elms = doc.xpath(".//input")
	input_args_data = []
  input_elms.each do |node|

    input_arg_data = nil

    if node['sequenceAllowed'] == "true"
      input_arg_data = ScriptArg.new(node['name'])

      puts "Input for **#{node['name']}**. \nEnter one XML file path on each line, relative to the zip container. Press 'q' when done. "

      input = ''
      until input == "q"
        print '>'
        input = STDIN.gets().chomp()
        if input == 'q'
          break
        end
        input_arg_data.add_value(input)
      end

    else
      puts "Input for **#{node['name']}**. \nEnter a path to an XML file, relative to the zip container: "
      path = STDIN.gets().chomp()
      input_arg_data = ScriptArg.new(node['name'])
      input_arg_data.add_value(path)
    end

    input_args_data.push(input_arg_data)
  end

  return input_args_data
end

# return an array of Arg objects
def prompt_for_options(doc)
  option_elms = doc.xpath(".//option")
	option_args_data = []
  option_elms.each do |node|
    req_or_opt = "Required"
    if node['required'] == 'false'
      req_or_opt = "Optional"
    end
    puts "#{req_or_opt} option for **#{node['name']}**. Enter a #{node['type']}: "
    value = STDIN.gets().chomp()
    option_arg_data = ScriptArg.new(node['name'])
    option_arg_data.add_value(value)
    option_args_data.push(option_arg_data)
  end
  return option_args_data
end

def create_job_xml(scripturi, input_args, option_args)

  builder = Nokogiri::XML::Builder.new do |xml|
		xml.jobRequest(:xmlns => "http://www.daisy.org/ns/pipeline/data") {
      xml.script(:href => scripturi)
      input_args.each do |arg|
        xml.input(:name => arg.name) {
          arg.value_list.each do |v|
            xml.file(:src => v)
          end
        }
      end

      option_args.each do |arg|
        xml.option(:name => arg.name) {
          xml.text arg.value_list[0]
        }
      end
    }
  end

  return builder.doc
end