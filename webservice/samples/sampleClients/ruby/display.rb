# show all the details for a script
def display_script(element)
  display_script_short(element)
	element.xpath(".//arg").each do |arg|
		puts "Argument \n\tname=" + arg['name']
		puts "\tdesc=" + arg['desc']
		puts "\ttype=" + arg['type']
		puts "\tport=" + arg['port']
	end
end

def display_script_short(element)
   puts element.xpath("./nicename")[0].content
   puts element.xpath("./description")[0].content
end
# show a list of script names and descriptions
def display_scripts(elements)

  count = 1
  elements.each do |elm|
    puts "#{count.to_s}. #{display_script_short(elm)}"
  end

end

def display_job(element)
  display_job_short(element)
  node.xpath(".//error").each do |err|
    puts "ERROR: " + err['level'] + ".  " + err.content
  end

end

def display_job_short(element)
  puts "Job ID = " + element['id'] + ",  Status = " + element['status']
end

def display_jobs(elements)
  count = 1
  elements.each do |elm|
    puts "#{count.to_s}. #{display_job_short(elm)}"
  end
end

def display_result(jobid, path)
  puts "Result for #{jobid} saved to #{path}"
end

def display_log(element)
  puts element.to_s
end