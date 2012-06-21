import mimetypes
import os
import string

class Param:
    """A simple key/value pair"""
    
    def __init__(self, key, val):
        self.key = key
        self.val = val
        
    def to_multipart(self):
        """A string representing the key/value pair as a multipart request"""
        return """Content-Disposition: form-data; name="{0}"\r\n\r\n{1}\r\n""".format(self.key, self.val)

class FileParam:
    """A key/file pair"""
    
    def __init__(self, key, filename, content):
        self.key = key
        self.filename = filename
        self.content = content
    
    def to_multipart(self):
        """A string representing the key/file pair as a multipart request"""
        return """Content-Disposition: form-data; name="{0}"; filename="{1}"\r\n""" \
        "Content-Transfer-Encoding: binary\r\n" \
        "Content-Type: {2}\r\n\r\n{3}" \
        "\r\n".format(self.key, self.filename, self.content, mimetypes.guess_type(self.filename)[0])

class MultipartPost:
    """Formats key/value or key/filename pairs into a multipart request"""
    BOUNDARY = 'pipeline-rules0000'
    HEADER = {"Content-type": "multipart/form-data; boundary=" + BOUNDARY + " "}
    FORMAT = "--{0}\r\n{1}"
    
    def prepare_query(self, data):
        """Format as multipart"""
        params = []
        for key, val in data.items():
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
            param_strings.append(FORMAT.format(self.BOUNDARY, param.to_multipart()))
            #print param_strings[-1]
        query = str.join("", param_strings) + ("--{0}--".format(self.BOUNDARY))
        return query, self.HEADER
