require 'nokogiri'
require './settings.rb'
require 'digest'
require 'openssl'
require 'base64'
require 'time'
require 'cgi'

CLIENT_KEY = "clientkey"
CLIENT_SECRET = "supersecret"

def trace(string, prefix)
	if Settings.instance.options[:trace] == true
		puts prefix
		puts string
	end
end

def error(string)
  puts string
end

def message(string)
  puts string
end

def count_elements(doc, element_name)
  return doc.xpath("//#{element_name}").length
end

def get_uri_from_shortname(name)
  doc = Rest.get_scripts

  scripts = doc.xpath("//script")

  scripts.each do |script|
    if script.xpath("./nicename")[0].content == name
      return script["href"]
    end
  end

  return nil
end

def get_job_status(id)
  doc = Rest.get_job(id)
  return doc.xpath(".//job")[0]['status']
end

# the input URI includes all parameters except key, timestamp, and hash
def prepare_auth_uri(uri)
  uristring = ""
  timestamp = Time.now.utc.strftime('%Y-%m-%dT%H:%M:%S.%3NZ')
  nonce = generate_nonce
  params = "key=#{CLIENT_KEY}&time=#{timestamp}&nonce=#{nonce}"
  if uri.index("?") == nil
    uristring = "#{uri}?#{params}"
  else
    uristring += "#{uri}&#{params}"
  end
  hash = generate_hash(uristring)

  return "#{uristring}&sign=#{hash}"

end


def generate_hash(data)
  digest = OpenSSL::Digest::Digest.new('sha1')
  hash = OpenSSL::HMAC.digest(digest, CLIENT_SECRET, data)
  hash64 = Base64.encode64(hash).chomp
  return CGI.escape(hash64)
end

def generate_nonce
  rand(10 ** 30).to_s.rjust(30,'0')
end
