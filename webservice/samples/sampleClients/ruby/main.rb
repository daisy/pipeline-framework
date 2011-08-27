#!/usr/bin/env ruby

require 'rubygems'
require 'nokogiri'
require 'optparse'
require './settings.rb'
require './operations.rb'

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
    delete_job(Settings.options[:id])
  elsif Settings.instance.command == "createjob"
    create_job
  elsif Settings.instance.command == "runpreset"
    run_preset_job
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
  script \t\t\t List details for a single script. Requires option --id=shortname.
  jobs \t\t\t List all jobs
  job \t\t\t List details for a single job. Requires option --id=jobid.
  log \t\t\t Show the log for a job.  Requires option --id=jobid.
  result \t\t\t Show where the result is stored for a job.  Requires option --id=jobid.
  delete \t\t\t Delete a job.  Requires option --id=jobid.
  createjob \t\t\t Start creating a job.  Requires option --id=jobid.
  runpreset \t\t\t Create a job with all pre-set values (for testing only).

  "

    Settings.instance.options[:trace] = false
    opts.on('-t', '--trace', 'Turn on trace statements') do
      Settings.instance.options[:trace] = true
    end

    Settings.instance.options[:id] = nil
    opts.on('-i', '--id VALUE', 'specify an ID value') do |id|
      Settings.instance.options[:id] = id
    end

    Settings.instance.options[:baseuri] = nil
    opts.on('-b', '--baseuri VALUE', "Override the default baseuri") do |baseuri|
      Settings.instance.set_baseuri(baseuri)
    end

    opts.on('-h', '--help', 'Display this screen') do
      message(opts)
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


# execution starts here
main 

