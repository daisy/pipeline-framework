#!/usr/bin/env python
"""Command line interface client for DAISY Pipeline2 web service.

Comes with a companion web service that must be started separately:

1. run ws/miniws.py
2. include <callback> element(s) in the job request
3. check for job updates in the terminal window where miniws.py is running

"""



import os
import argparse
from lxml import etree
import resources

def main():
    """Main entry point"""
    
    usage = """
  
  Usage: main.py command options
  
  Commands:
  
  scripts       List all scripts.
  script        List details for a single script.
  jobs          List all jobs.
  job           List details for a single job.
  log           Show the log for a job.
  result        Show where the result is stored for a job.
  delete-job    Delete a job.
  new-job       Create a new job.
  
  Examples:
  Show all scripts:
      main.py scripts
  Show a specific script:
      main.py script --id=http://www.daisy.org/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl
  Show a specific job:
      main.py job --id=873ce8d7-0b92-42f6-a2ed-b5e6a13b8cd7
  Create a job:
      main.py new-job --request=../testdata/job1.request.xml
  Create a job:
      main.py new-job --request=../testdata/job2.request.xml --job-data=../testdata/job2.data.zip
  Get notifications of job updates:
      main.py new-job --request=../testdata/job2.request.xml --job-data=../testdata/job2.data.zip
  """
    parser = argparse.ArgumentParser(usage = usage, description = "Sample pipeline2 webservice client written in python")    
    parser.add_argument('command', metavar='command', help='an integer for the accumulator')
    parser.add_argument("--id", dest="id", help="job or script ID")
    parser.add_argument("--request", dest="request", help="XML file representing the job request")
    parser.add_argument("--job-data", dest="jobdata", help="Zipfile with job data")
        
    args = parser.parse_args()
    if args.command == None:
        print usage
        exit(1)
    
    handle_command(args)

def handle_command(args):
    """Process the command"""
    if args.command == "scripts":
        get_scripts()
    elif args.command == "script":
        get_script(args.id)
    elif args.command == "jobs":
        get_jobs()
    elif args.command == "job":
        get_job(args.id)
    elif args.command == "log":
        get_log(args.id)
    elif args.command == "result":
        get_result(args.id)
    elif args.command == "delete-job":
        delete_job(args.id)
    elif args.command == "new-job":
        post_job(args.request, args.jobdata)
    else:
        print "Command %s not recognized" % args.command

def get_scripts():
    """Print scripts XML"""
    doc = resources.get_scripts()
    if doc == None:
        print "No data returned"
        return
    print(etree.tostring(doc, pretty_print=True))

def get_script(script_id):
    """Print script XML"""
    if script_id == None or script_id == "":
        print "ID required"
        return
    doc = resources.get_script(script_id)
    if doc == None:
        print "No data returned"
        return
    print(etree.tostring(doc, pretty_print=True))

def get_jobs():
    """Print jobs XML"""
    doc = resources.get_jobs()
    if doc == None:
        print "No data returned"
        return
    print(etree.tostring(doc, pretty_print=True))

def get_job(job_id):
    """Print job XML"""
    if job_id == None or job_id == "":
        print "ID required"
        return
    doc = resources.get_job(job_id)
    if doc == None:
        print "No data returned"
        return
    
    print(etree.tostring(doc, pretty_print=True))

def get_log(job_id):
    """Print log"""
    if job_id == None or job_id == "":
        print "ID required"
        return
    status = resources.get_job_status(job_id)
    if status != "DONE":
        print "Cannot get log until the job is done. Job status: %s." % status
        return
    log = resources.get_log(job_id)
    if log == "":
        print "No data returned"
        return
    print log

def get_result(job_id):
    """Write the job result to disk"""
    if job_id == None or job_id == "":
        print "ID required"
        return
    
    status = resources.get_job_status(job_id)
    if status != "DONE":
        print "Cannot get result until the job is done. Job status: %s." % status
        return
    
    response = resources.get_result(job_id)
    if response == None:
        print "No data returned"
        return
    
    path = "/tmp/%s.zip" % job_id
    zipfile = open(path, "wb")
    zipfile.write(response)
    zipfile.close()

def post_job(job_request_filepath, job_data_filepath):
    """Create a new job"""
    if job_request_filepath == None or job_request_filepath == "":
        print "--request filepath required"
        return
    
    if os.path.exists(job_request_filepath) == False:
        print "Invalid request filepath"
        return
    
    datafile = open(job_request_filepath, "r")
    request = datafile.read()
    datafile.close()
    doc = None
    
    if job_data_filepath == None or job_data_filepath == "":
        doc = resources.post_job(request, None)
    else:
        if os.path.exists(job_data_filepath) == False:
            print "Invalid job data filepath"
            return
    
        doc = resources.post_job(request, job_data_filepath)
    
    if doc == None:
        print "No data returned"
        return
    
    print(etree.tostring(doc, pretty_print=True))

def delete_job(job_id):
    """Delete a job"""
    if job_id == None or job_id == "":
        print "ID required"
        return
    
    status = resources.get_job_status(job_id)
    if status != "DONE":
        print "Cannot delete until the job is done. Job status: %s." % status
        return
    
    result = resources.delete_job(job_id)
    if result != None:
        print "Job deleted"
    else:
        print "Error deleting job"


if __name__ == "__main__":
    main()

