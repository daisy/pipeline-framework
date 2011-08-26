# guide the user through the process of creating a job
def job_creation_wizard

	input = ""
	until input == 'q'
		puts "Choose a converter for your job, or (q)uit: \n\n"
		# show available converters
		print_converters
		input = get_input

		if input == 'q'
			return
		end

		if ((1..$num_converters).include? input.to_i) == true
			# user chose a converter
			break
		else
			puts "Not recognized.\n"
		end
	end

	converter = get_converter_by_number(input.to_i)

	print_converter(converter)

	# now prompt for each input argument
	args = converter.xpath(".//arg")

	args_data = []
	if args.length > 0
		puts "This converter requires " + args.length.to_s + " input arguments. Enter data for each argument, or (q)uit.\n\n"

		args.each do |node|

			# requires an XML file
			if node['type'] == 'input'

				valid_input = false
				until valid_input
					puts "XML file path for arg name='" + node['name'] + "':"
					input = get_input

					if input == 'q'
						return
					end

# when the framework supports inline documents, replace this:
					if FileTest.exists?(input) == true
						argdata = OpenStruct.new
						argdata.value = input
						argdata.name = node['name']
						argdata.argtype = node['type']
						args_data.push(argdata)
						valid_input = true
					else
						puts "file not found"
					end

# with this:
=begin
					begin
						val = IO.read(get_input)
						argdata = OpenStruct.new
						argdata.value = val
						argdata.name = node['name']
						argdata.argtype = node['type']
						args_data.push(argdata)
						valid_input = true
					rescue
						puts "file not found"
					end
=end
			end
		# requires a string
			elsif node['type'] == 'option'

					puts "String data for arg name='" + node['name'] + "':"
					input = get_input
					if input == 'q'
						return
					end
					argdata = OpenStruct.new
					argdata.value = input
					argdata.name = node['name']
					argdata.argtype = node['type']
					args_data.push(argdata)
			# unrecognized arg type; maybe "output"
			else
				puts "Not collecting data for " + node['desc']
			end
		end
	else
		puts "This converter requires no input arguments."
	end

	if post_job(converter, args_data) == true
		$num_jobs += 1
		puts "Job created"
	else
		puts "Job error"
	end
end
