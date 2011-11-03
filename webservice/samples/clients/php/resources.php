<?php

	require("rest.php");
	class Resources {
		private static $BASEURI = "http://localhost:8182/ws";
		
		public static function get_scripts() {
			$uri = Resources::$BASEURI . "/scripts";
			$doc = Rest::get_resource_as_xml($uri);
			return $doc;
		}
		
		public static function get_script($id) {
			$uri = Resources::$BASEURI . "/script?id=" . $id;
			$doc = Rest::get_resource_as_xml($uri);
			return $doc;
		}
		
		public static function get_jobs() {
			$uri = Resources::$BASEURI . "/jobs";
			$doc = Rest::get_resource_as_xml($uri);
			return $doc;
		}
		
		public static function get_job($id) {
			$uri = Resources::$BASEURI . "/jobs/" . $id;
			$doc = Rest::get_resource_as_xml($uri);
			return $doc;
		}
		
		public static function get_log($id) {
			$uri = Resources::$BASEURI . "/jobs/" . $id . "/log";
			$result = Rest::get_resource($uri);
			return $result;
		}
		
		public static function get_result($id) {
			$uri = Resources::$BASEURI . "/jobs/" . $id . "/result";
			$result = Rest::get_resource($uri);
			return $result;
		}
		
		public static function post_job($job_request, $job_data) {
			$uri = Resources::$BASEURI . "/jobs";
			$result = null;
			if ($job_data != null) {
				
				$job_request_arr = array("data" => $job_request, 
										"content-type" => "text/xml", 
										"encoding" => "utf8", 
										"is-file-attachment" => false, 
										"filename" => null);
							
				$job_data_arr = array("data" => $job_data,
				 					  "content-type" => "application/zip", 
				                      "encoding" => "binary", 
									  "is-file-attachment" => true, 
									  "filename" => "data.zip");
				$data = array(
					"job-request" => $job_request_arr,
					"job-data" => $job_data_arr
				);
				
				$result = Rest::post_resource_multipart($uri, $data);
			}
			else {
				$data = $job_request;
				$content_type = "text/xml";
				$result = Rest::post_resource($uri, $data, $content_type);
			}
			
			return $result;
		}
		
		public static function delete($id) {
			$uri = Resources::$BASEURI . "/jobs/" . $id;
			$was_deleted = Rest::delete_resource($uri);
			return $was_deleted;
		}
	}

?>