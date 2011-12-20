#!/usr/bin/env ruby
require 'rubygems'
require 'optparse'
require 'nokogiri'
require './settings'
require './resources'

def main

  checkargs

  if Settings.instance.command == "scripts"
    get_scripts
  elsif Settings.instance.command == "script"
    get_script(Settings.instance.options[:id])
  elsif Settings.instance.command == "jobs"
    get_jobs
  elsif Settings.instance.command == "job"
    get_job(Settings.instance.options[:id])
  elsif Settings.instance.command == "log"
    get_log(Settings.instance.options[:id])
  elsif Settings.instance.command == "result"
    get_result(Settings.instance.options[:id])
  elsif Settings.instance.command == "delete"
    delete_job(Settings.instance.options[:id])
  elsif Settings.instance.command == "new"
    post_job(Settings.instance.options[:job_request], Settings.instance.options[:job_data])
  else
    puts "Command #{Settings.instance.command} not recognized"
  end

end

def checkargs

  optparse = OptionParser.new do |opts|

  opts.banner = "

  Usage: main.rb command options

  Commands:

  scripts \t\t\t List all scripts
  script \t\t\t List details for a single script.
  jobs \t\t\t List all jobs
  job \t\t\t List details for a single job.
  log \t\t\t Show the log for a job.
  result \t\t\t Show where the result is stored for a job.
  delete \t\t\t Delete a job.
  new \t\t\t Create a new job.

  Examples:
  Show all scripts:
	  main.rb scripts
  Show a specific script:
	  main.rb script --id=http://www.daisy.org/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl
  Show a specific job:
	  main.rb job --id=873ce8d7-0b92-42f6-a2ed-b5e6a13b8cd7
  Create a job:
	  main.rb new --job-request=../testdata/job1.request.xml
  Create a job:
  	main.rb new --job-request=../testdata/job2.request.xml --job-data=../testdata/job2.data.zip

  "

    Settings.instance.options[:id] = nil
    opts.on('--id VALUE', 'ID of Job or Script') do |val|
      Settings.instance.options[:id] = val
    end

    Settings.instance.options[:job_data] = nil
      opts.on('--job-data VALUE', 'Zip file containing the job data') do |val|
        Settings.instance.options[:job_data] = val
    end

    Settings.instance.options[:job_request] = nil
      opts.on('--job-request VALUE', 'XML file representing the job request') do |val|
        Settings.instance.options[:job_request] = val
    end


    opts.on('--help', 'Display this screen') do
      puts opts
      exit
    end
  end

  optparse.parse!

  # only expecting one command so just grab the first one
  ARGV.each do |a|
    Settings.instance.command = a
    break
  end
end

def get_scripts
  doc = Resources.get_scripts
  if doc == nil
    return
  end
  puts doc.to_xml(:indent => 2)
end

def get_script(id)
  if id == ""
    puts "ID required"
    return
  end
  doc = Resources.get_script(id)
  if doc == nil
    puts "No data returned"
    return
  end
  puts doc.to_xml(:indent => 2)
end

def get_jobs
  doc = Resources.get_jobs
  if doc == nil
    puts "No data returned"
    return
  end
  puts doc.to_xml(:indent => 2)
end

def get_job(id)
  if id == ""
    puts "ID required"
    return
  end
  doc = Resources.get_job(id)
  if doc == nil
    puts "No data returned"
    return
  end
  puts doc.to_xml(:indent => 2)
end

def get_log(id)
  if id == ""
    puts "ID required"
    return
  end
  status = Resources.get_job_status(id)
  if status != "DONE"
    puts "Cannot get log until the job is done. Job status: #{status}."
    # return
  end

  log = Resources.get_log(id)
  if log == ""
    puts "No data returned"
    return
  end
  puts log
end

def get_result(id)
  if id == ""
    puts "ID required"
    return
  end
  status = Resources.get_job_status(id)
  if status != "DONE"
    puts "Cannot get result until the job is done. Job status: #{status}."
    return
  end

  response = Resources.get_result(id)
  if response == nil
    puts "No data returned"
    return
  end
  path = "/tmp/#{id}.zip"
  open(path, "wb") { |file|
    file.write(response)
  }
  puts "Saved to #{path}"
end

def post_job(job_request_filepath, job_data_filepath)
  if job_request_filepath == ""
    puts "job-request filepath required"
    return
  end

  request = File.read(job_request_filepath)
  data = nil
  if job_data_filepath != nil
    data = File.open(job_data_filepath, "rb")
  end

  response = Resources.post_job(request, data)

  if data != nil
    data.close
  end

  if response == nil
    puts "No data returned"
    return
  end

  puts "Job created #{response}"
end

def delete_job(id)
  if id == ""
    puts "ID required"
    return
  end

  status = Resources.get_job_status(id)
  if status != "DONE"
    puts "Cannot delete until the job is done. Job status: #{status}."
    return
  end

  result = Resources.delete(id)
  if result
    puts "Job deleted"
  else
    puts "Error deleting job"
  end

end

# execution starts here
main 

