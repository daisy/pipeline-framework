require 'nokogiri'

def trace(string, prefix)
	if $trace == true:
		puts prefix
		puts string
	end
end

def error(string)
  puts string
end

def job_request_xml(script, args)
	builder = Nokogiri::XML::Builder.new do |xml|
		xml.jobRequest(:xmlns => "http://www.daisy.org/ns/pipeline/data") {
      xml.script(:href => script['href']) {
        args.each do |a|
					if a.argtype == 'input'
          	xml.input(:name => a.name) {
							xml.docwrapper {
								xml.text a.value
							}
						}
					elsif a.argtype == 'option'
						xml.option(:name => a.name) {
							xml.text a.value
						}
					end
					}
        end
      }
    }
  end

  return builder.doc.to_s
end

def count_elements(doc, element_name)
  return doc.xpath("//#{element_name}").length
end