#!/usr/bin/env ruby

require 'rubygems'
require 'nokogiri'

def print_usage
	puts "
	Syntax:

  main.rb command options

  Commands:

  scripts \t\t\t List all scripts
  script \t\t\t List details for a single script. Requires option --id=shortname.
  jobs \t\t\t List all jobs
  job \t\t\t List details for a single job. Requires option --id=jobid.
  log \t\t\t Show the log for a job.  Requires option --id=jobid.
  result \t\t\t Show where the result is stored for a job.  Requires option --id=jobid.
  delete \t\t\t Delete a job.  Requires option --id=jobid.
  createjob \t\t\t Start creating a job.  Requires option --id=jobid.

  Options:

  --id \t\t\t String value to identify a resource, such as a converter or a job.
  --trace \t\t\t Turn on trace statements
  --baseuri \t\t\t Override the baseuri with another value; e.g. http://localhost:3000/ws/
  --help \t\t\t Show this message.
  "

end

def main

  $baseuri = "http://localhost:8182/ws/"

	checkargs

  if $help
    print_usage
    return
  end

  if $command != ""

    if $command == "scripts"
      get_scripts
    elsif $command == "script"
      get_script($id)
    elsif $command == "jobs"
      get_jobs
    elsif $command == "job"
      get_job($id)
    elsif $command == "log"
      get_log($id)
    elsif $command == "result"
      get_result($id)
    elsif $command == "delete"
      delete_job($id)
    elsif $command == "createjob"
      create_job
    else
      puts "Command #{$command} not recognized"
    end
  end

end

def checkargs
	$trace = false
	$help = false
	$command = ""
  $id = ""

	ARGV.each do|a|
	  if a == '--trace'
			$trace = true
		elsif a == "--help"
			$help = true
		elsif a == "--test"
			$test = true
    elsif a == "--baseuri"
      $baseuri = "TODO and end it with a slash"
    elsif a == "--id"
      $id = "TODO"
    else
      $command = a
    end
	end
end

## execution starts here
main 

