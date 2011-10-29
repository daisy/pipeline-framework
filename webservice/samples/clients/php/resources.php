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
			$uri = Resources::$BASEURI . "/jobs/" . $id . "/result.zip";
			$result = Rest::get_resource($uri);
			return $result;
		}
		
		public static function post_job($job_request, $job_data) {
		
		}
		
		public static function delete($id) {
			$uri = Resources::$BASEURI . "/jobs/" . $id;
			$was_deleted = Rest::delete_resource($uri);
			return $was_deleted;
		}
	}

?>