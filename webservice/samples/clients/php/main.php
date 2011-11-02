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
		download_file_test_fn($result, $id . ".zip", "application/zip");
	}

	function post_job($job_request, $job_data) {
		$result = Resources::post_job($job_request, $job_data);
		if ($result == null) {
			show_message("No data returned");
			return;
		}
		show_message($result);
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
	
	// this works for log (plain/text) files but zip files come through corrupted
	function download_file($file_contents, $filename, $content_type) {
		header("Content-Type: " . $content_type);  
		header('Content-Disposition: attachment; filename="' . $filename . '"');
		header("Content-Transfer-Encoding: binary");
		header('Expires: 0');
		header('Pragma: no-cache');
		header('Content-Length: ' . strlen($file_contents));
		echo $file_contents;
	}
	
	// FIXME: zip file download not working
	// experimental version of the above: trying to get this to work with zip files
	// they seem to be written correctly to disk (diffed against original), but they won't open
	function download_file_test_fn($file_contents, $filename, $content_type) {
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
	
	$id = "2e4fdd16-5dbd-43c7-9329-7308e8491425";
	$script = "http://www.daisy.org/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl";
	
	
	// use the testdata folder in the root of the samples dir (copy wherever you need to)
	$job1_request = file_get_contents("testdata/job1Request.xml");
	$job2_request = file_get_contents("testdata/job2Request.xml");
	$job2_data = file_get_contents("testdata/job2Data.zip");
	
	// these work:
	
	//get_scripts();
	//get_script($script);
	//get_jobs();
	//get_job($id);
	//get_log($id);
	//delete($id);
	//post_job($job1_request, null);
	
	// these don't
	//get_result($id);
	post_job($job2_request, $job2_data);
?>
