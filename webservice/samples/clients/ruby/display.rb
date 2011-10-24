# display REST XML responses as human-friendly messages

require './utils.rb'

def display_script(element)
  message("\nScript: ")
  puts display_script_short(element)

  if element.xpath(".//homepage").length > 0
    puts "Homepage: #{element.xpath('.//homepage')[0].content}"
  end
  element.xpath(".//input").each do |arg|
		puts "Input argument \n\tname = #{arg['name']}"
		puts "\tdesc = #{arg['desc']}"
		puts "\tmedia type = #{arg['mediaType']}"
		puts "\tsequence allowed = #{arg['sequenceAllowed']}"
  end

element.xpath(".//option").each do |arg|
		puts "Option argument \n\tname = #{arg['name']}"
		puts "\tdesc = #{arg['desc']}"
		puts "\ttype = #{arg['type']}"
    puts "\tmedia type = #{arg['mediaType']}"
		puts "\trequired = #{arg['required']}"
  end

  message("\n")
end

def display_script_short(element)
  nicename = element.xpath("./nicename")[0].content
  description = element.xpath("./description")[0].content
  return "#{nicename}\n\t#{description}"
end

def display_scripts(elements)
  message("\nScripts: ")
  count = 1
  elements.each do |elm|
    puts "#{count.to_s}. #{display_script_short(elm)}"
    count += 1
  end
  message("\n")
end

def display_job(element)
  message("\nJob: ")

  puts display_job_short(element)
  element.xpath(".//error").each do |err|
    puts "ERROR: #{err['level']}. #{err.content}"
  end
  element.xpath(".//warning").each do |err|
    puts "WARNING: #{err['level']}. #{err.content}"
  end
  message("\n")
end

def display_job_short(element)
  return "Job ID = #{element['id']},  Status = #{element['status']}"
end

def display_jobs(elements)
  message("\nJobs: ")

  count = 1
  elements.each do |elm|
    puts "#{count.to_s}. #{display_job_short(elm)}"
    count += 1
  end
  message("\n")
end

def display_result(jobid, path)
  puts "Result for #{jobid} saved to #{path}"
  message("\n")
end

def display_log(logfile, jobid)
  message("\nLog for job #{jobid}: ")
  puts logfile
  message("\n")
end