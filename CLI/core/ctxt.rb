require 'logger'
require './core/conf'
class Ctxt
	@@logger=nil
	@@conf=nil
	def self.logger
		if @@logger==nil
			@@logger=Logger.new(STDERR)
			@@logger.level=Logger::INFO
		end
		@@logger
	end

	def self.conf(*file)
		if @@conf==nil
			@@conf=Conf.new(file[0])
		end
		@@conf
	end
end
