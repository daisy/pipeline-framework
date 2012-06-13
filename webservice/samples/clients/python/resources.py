"""Convenience layer on top of REST calls"""
import rest
import settings
from lxml import etree

def get_scripts():
    """Return an XML document for all scripts"""
    uri = "%s/scripts" % settings.BASEURI
    doc = rest.get_resource_as_xml(uri)
    return doc

def get_script(script_id):
    """Return an XML document for a given script"""
    uri = "%s/scripts/%s" % (settings.BASEURI, script_id)
    doc = rest.get_resource_as_xml(uri)
    return doc

def get_jobs():
    """Return an XML document for all jobs"""
    uri = "%s/jobs" % settings.BASEURI
    doc = rest.get_resource_as_xml(uri)
    return doc

def get_job(job_id):
    """Return an XML document for a given job"""
    uri = "%s/jobs/%s" % (settings.BASEURI, job_id)
    doc = rest.get_resource_as_xml(uri)
    return doc

def get_log(job_id):
    """Return the log data for a given job as a string"""    
    uri = "%s/jobs/%s/log" % (settings.BASEURI, job_id)
    doc = rest.get_resource(uri)
    return doc

def get_result(job_id):
    """Return an XML document for the result of a given job"""
    uri = "%s/jobs/%s/result" % (settings.BASEURI, job_id)
    doc = rest.get_resource(uri)
    return doc

def post_job(request, data):
    """Create a new job and return the job description XML document"""
    uri = "%s/jobs" % settings.BASEURI
    if data == None:
        doc = rest.post_resource(uri, request)
    else:
        doc = rest.post_resource(uri, {"job-data": data, "job-request": request})#{"job-request": request, "job-data": data})
    return doc

def delete_job(job_id):
    """Delete a job and return whether the deletion was successful"""
    uri = "%s/jobs/%s" % (settings.BASEURI, job_id)
    success = rest.delete_resource(uri)
    return success

def get_job_status(job_id):
    """Return the status of the given job"""
    doc = get_job(job_id)
    if doc == None:
        return ""
    xpath_expr = "//%sjob" % "{%s}" % settings.PX_NS
    xpath_fn = etree.ETXPath(xpath_expr)
    results = xpath_fn(doc)
    return results[0].attrib['status']
