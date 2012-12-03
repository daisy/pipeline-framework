import mimetypes
import os

class Param:
    """A simple key/value pair"""
   
    def __init__(self, key, val):
        self.key = key
        self.val = val
       
    def to_multipart(self):
        """A string representing the key/value pair as a multipart request"""
        return """Content-Disposition: form-data; name="%s"\r\n\r\n%s\r\n""" % (self.key, self.val)

class FileParam:
    """A key/file pair"""
   
    def __init__(self, key, filename, content):
        self.key = key
        self.filename = filename
        self.content = content
   
    def to_multipart(self):
        """A string representing the key/file pair as a multipart request"""
        return """Content-Disposition: form-data; name="%(k)s"; filename="%(filename)s"\r\n""" \
        "Content-Transfer-Encoding: binary\r\n" \
        "Content-Type: %(mime)s\r\n\r\n%(content)s" \
        "\r\n" % {"k": self.key, "filename": self.filename, "content": self.content, "mime": mimetypes.guess_type(self.filename)[0]}

class MultipartPost:
    """Formats key/value or key/filename pairs into a multipart request"""
    BOUNDARY = 'pipeline-rules0000'
    HEADER = {"Content-type": "multipart/form-data; boundary=" + BOUNDARY + " "}
    FORMAT = "--%(boundary)s\r\n%(param)s"
   
    def prepare_query(self, data):
        """Format as multipart"""
        params = []
        for key, val in data.items():
            print "Processing %s" % key
            param = None
            if os.path.exists(val):
                fin = open(val, "rb")
                fin_data = fin.read()
                param = FileParam(key, val, fin_data)
                fin.close()
            else:
                param = Param(key, val)
           
            if (param != None):
                params.append(param)
        param_strings = []
        for param in params:
            param_strings.append(self.FORMAT % {"boundary": self.BOUNDARY, "param": param.to_multipart()})
            #print param_strings[-1]
        query = str.join("", param_strings) + ("--%s--" % self.BOUNDARY)
        fout = open("/tmp/tpy", "w")
        fout.write(query)
        fout.close()
        return query, self.HEADER
