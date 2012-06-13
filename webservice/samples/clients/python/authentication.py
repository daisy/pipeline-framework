import hashlib
import hmac
import base64
import random
import time
import urllib

AUTH_ID = "clientid"
SECRET = "supersecret"

def prepare_authenticated_uri(uri):
    """Prepare an authenticated URI. 
    The uri param includes all parameters except id, timestamp, and hash"""
    
    uristring = ""
    timestamp = time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())
    nonce = generate_nonce()
    params = "authid=%(authid)s&time=%(timestamp)s&nonce=%(nonce)s" % {"authid": AUTH_ID, "timestamp": timestamp, "nonce": nonce}
    if uri.find("?") == -1:
        uristring = "%(uri)s?%(params)s" % {"uri": uri, "params": params}
    else:
        uristring += "%(uri)s&%(params)s" % {"uri": uri, "params": params}
    
    hashed_string = generate_hash(uristring)
    auth_uri = "%(uristring)s&sign=%(hash)s" % {"uristring": uristring, "hash": hashed_string}
    print auth_uri
    return auth_uri


def generate_hash(data):
    """Return a SHA1 hash of the data using the secret"""
    digest = hmac.new(SECRET, data, hashlib.sha1).digest()
    hash64 = base64.b64encode(digest)
    return urllib.quote_plus(hash64) # escape chars for URI

def generate_nonce():
    """Generate a random number 30 digits long"""
    randomnum = random.randrange(10**30)
    rjust_str = '{:<30}'.format(str(randomnum))
    return rjust_str


