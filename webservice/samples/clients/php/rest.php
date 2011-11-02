<?php 
	class Rest {
		
		public static function get_resource($uri) {
			$ch = curl_init();
			curl_setopt($ch, CURLOPT_URL, $uri); 
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
			curl_setopt($ch, CURLOPT_TIMEOUT, '3');
			$resource = trim(curl_exec($ch));
			curl_close($ch);
			return $resource;			
		}
		
		public static function get_resource_as_xml($uri) {
			$resource = Rest::get_resource($uri);
			$xml = new SimpleXMLElement($resource);
			return $xml;
		}
	
		// to send raw data as the request, just pass in the raw data
		// to send a multipart request, send an array with field names and data 
		public static function post_resource($uri, $data) {
			
			$ch = curl_init($uri);
			curl_setopt($ch, CURLOPT_POST, 1);
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
			
			// just post the request contents
			if (is_array($data) == false) {
				curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: text/xml'));
				curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
			}
			// else do a multipart upload
			else{
				// FIXME: this isn't getting interpreted by the WS as a multipart request.  can cURL do proper multipart uploads?
				$imploded = Rest::array_implode('=', '&', $data);
				curl_setopt($ch, CURLOPT_POSTFIELDS, $imploded);
			}
			
			curl_setopt($ch, CURLOPT_HEADER, true);
			$response = curl_exec($ch);
			$info = curl_getinfo($ch);
			curl_close($ch);
			
			
			// return the content location header
			if ($info['http_code'] == 201) {
				$parsed = Rest::parse_http_response($response);
				return $parsed[0]['content-location'];
			}
			else {
				return null;
			}
			
		}
	
		public static function delete_resource($uri) {
			$ch = curl_init();
			curl_setopt($ch, CURLOPT_URL, $uri); 
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
			curl_setopt($ch, CURLOPT_TIMEOUT, '3');
			curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'DELETE');
			curl_exec($ch);
			$info = curl_getinfo($ch);
			
			curl_close($ch);
			if ($info['http_code'] == 204) {
				return true;
			}
			else {
				return false;
			}
		}
		
		// from http://snipplr.com/view/17242/
		private static function parse_http_response($string)
	    {
	    	$headers = array();
	    	$content = '';
	    	$str = strtok($string, "\n");
	    	$h = null;
	    	while ($str !== false) {
	    		if ($h and trim($str) === '') {
	    			$h = false;
	    			continue;
	    		}
	    		if ($h !== false and false !== strpos($str, ':')) {
	    			$h = true;
		    		list($headername, $headervalue) = explode(':', trim($str), 2);
		    		$headername = strtolower($headername);
		    		$headervalue = ltrim($headervalue);
		    		if (isset($headers[$headername]))
		    			$headers[$headername] .= ',' . $headervalue;
		    		else
		    			$headers[$headername] = $headervalue;
		    	}
	    		if ($h === false) {
	    			$content .= $str."\n";
	    		}
	    		$str = strtok("\n");
	    	}
	    return array($headers, trim($content));
	    }
	
		private static function array_implode($glue, $separator, $array) {
		    if (!is_array( $array)) return $array;
		    $string = array();
		    foreach ($array as $key => $val) {
		        if (is_array($val))
		            $val = implode(',', $val);
		        $string[] = "{$key}{$glue}{$val}";
		    }
		    return implode( $separator, $string );
		}
	}
?>