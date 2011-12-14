require 'digest'
require 'openssl'
require 'base64'
require 'time'
require 'cgi'
require './core/ctxt'
module Authentication
  module_function


  # the input URI includes all parameters except key, timestamp, and hash
  def prepare_authenticated_uri(uri)
    uristring = ""
    timestamp = Time.now.utc.strftime('%Y-%m-%dT%H:%M:%SZ')
    nonce = generate_nonce
    params = "key=#{Ctxt.conf[Ctxt.conf.class::CLIENT_KEY]}&time=#{timestamp}&nonce=#{nonce}"
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
    hash = OpenSSL::HMAC.digest(digest,Ctxt.conf[Ctxt.conf.class::CLIENT_SECRET], data)
    hash64 = Base64.encode64(hash).chomp
    return CGI.escape(hash64)
  end

  def generate_nonce
    rand(10 ** 30).to_s.rjust(30,'0')
  end

end
