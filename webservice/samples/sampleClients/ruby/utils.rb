require 'nokogiri'
require './settings.rb'

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
