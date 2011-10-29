<?php
	header("Cache-Control: no-cache, must-revalidate"); // HTTP/1.1
	require("resources.php");

	function get_scripts() {
		$result = Resources::get_scripts();
		show_xml($result);
	}

	function get_script($id) {
		$result = Resources::get_script($id);
		show_xml($result);
	}

	function get_jobs() {
		$result = Resources::get_jobs();
		show_xml($result);
	}

	function get_job($id) {
		$result = Resources::get_job($id);
		show_xml($result);
	}

	function get_log($id) {
		$result = Resources::get_log($id);
		if ($result == null) {
			show_message("No data returned");
			return;
		}
		download_file($result, $id . ".log", "text/plain");
	}

	function get_result($id) {
		$result = Resources::get_result($id);
		if ($result == null) {
			show_message("No data returned");
			return;
		}
		download_file($result, $id . ".zip", "application/zip");
	}

	function post_job($job_request, $job_data) {

	}

	function delete($id) {
		$result = Resources::delete($id);
		if ($result == true) {
			show_message("Success");
		}
		else {
			show_message("Failed");
		}
	}
	function show_xml($xml) {
		if ($xml == NULL) {
			show_message("No data returned");
			return;
		}
		header("Content-Type: application/xml");
		echo $xml->asXML();
	}
	function download_file($file_contents, $filename, $content_type) {
		header("Content-Type: " . $content_type);  
		header('Content-Disposition: attachment; filename="' . $filename . '"');
		header("Content-Transfer-Encoding: binary");
		header('Expires: 0');
		header('Pragma: no-cache');
		header('Content-Length: ' . strlen($file_contents));
		echo $file_contents;
	}
	function show_message($text) {
		header("Content-Type: text/html");
		echo "<p>" . $text . "</p>";
	}
	
	//get_scripts();
	//get_script("http://www.daisy.org/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl");
	//get_jobs();
	//get_job('39b0a59d-d5df-459c-ab58-29eb9a8d68a3');
	//get_log('39b0a59d-d5df-459c-ab58-29eb9a8d68a3');
	delete('39b0a59d-d5df-459c-ab58-29eb9a8d68a3');
	//get_result('39b0a59d-d5df-459c-ab58-29eb9a8d68a3');
?>
