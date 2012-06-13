"""The local webservice set up to receive callbacks about job progress"""
import web
from lxml import etree
import settings

class jobmessage:        
    def POST(self):
        xml = web.data()
        doc = etree.fromstring(xml)
        self.print_update(doc)

    def print_update(self, doc):
        xpath_expr = "//%sjob" % "{%s}" % settings.PX_NS
        xpath_fn = etree.ETXPath(xpath_expr)
        results = xpath_fn(doc)
        jobid = results[0].attrib['id']
        xpath_expr = "//%sjob/messages/message" % "{%s}" % settings.PX_NS
        xpath_fn = etree.ETXPath(xpath_expr)
        results = xpath_fn(doc)
        print "JOB UPDATE\n\tID %(id)s\n\tMessage(s):"
        for m in results:
            print "\n\t%(level)s - %(message)s" % {"level": m.attrib['level'], "message": m.text}
        print ""

class jobstatus:
    def POST(self):
        xml = web.data()
        doc = etree.fromstring(xml)
        self.print_update(doc)
    
    def print_update(self, doc):
        xpath_expr = "//%sjob" % "{%s}" % settings.PX_NS
        xpath_fn = etree.ETXPath(xpath_expr)
        results = xpath_fn(doc)
        print "JOB UPDATE\n\tID %(id)s\n\tStatus:\n\t%(status)s\n" % {"id": jobid, "status": jobstatus}

class miniws:
    def __init__(self):
        urls = (
        '/ws/jobmessage', 'jobmessage',
        '/ws/jobstatus', 'jobstatus')
        self.app = web.application(urls, globals())
    
    def start(self):
        self.app.run()

def main():
    ws = miniws()
    ws.start()
    
if __name__ == "__main__":
    main()