<?php 
	class Rest {
		
		public static function get_resource($uri) {
			$ch = curl_init();
			curl_setopt($ch, CURLOPT_URL, $uri); 
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
			curl_setopt($ch, CURLOPT_TIMEOUT, '3');
			$resource = trim(curl_exec($ch));
			return $resource;			
		}
		
		public static function get_resource_as_xml($uri) {
			$resource = Rest::get_resource($uri);
			$xml = new SimpleXMLElement($resource);
			return $xml;
		}
	
		public static function post_resource($uri, $request_contents, $upload_data) {
			
		}
	
		public static function delete_resource($uri) {
			$ch = curl_init();
			curl_setopt($ch, CURLOPT_URL, $uri); 
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
			curl_setopt($ch, CURLOPT_TIMEOUT, '3');
			curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'DELETE');
			curl_exec($ch);
			$info = curl_getinfo($ch);
			if ($info['http_code'] == 204) {
				return true;
			}
			else {
				return false;
			}
		}
	}
?>