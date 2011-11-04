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

	function post_job($job_request_filename, $job_data_filename) {
	
		$job_request = file_get_contents($job_request_filename);
		
		$job_data = null;
		if ($job_data_filename != null) {
			$fh = fopen($job_data_filename, "rb");
			if ($fh) {
				$job_data = fread($fh, filesize($job_data_filename));
				fclose($fh);
			}
		}
		
	
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
		} else {
		    header("Content-Type: application/xml");
            echo preg_replace('/>/is',">\n<?xml-stylesheet type=\"text/xsl\" href=\"ws-xhtml.xsl\"?".">",$xml->asXML(),1);
        }
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
	
	
	// examples of calling the above functions
	
	$id = "5650a8d0-16db-4dac-a1a9-0f1130c93c1c";
	$script = "http://www.daisy.org/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl";
	$job1_request_file = "testdata/job1.request.xml";
	$job2_request_file = "testdata/job2.request.xml";
	$job2_data_file = "testdata/job2.data.xml";
	
	//get_scripts();
	//get_script($script);
	//post_job($job1_request_file, null);
	
	//post_job($job2_request_file, $job2_data_file);
	
	//get_jobs();
	//get_job($id);
	//get_result($id);
	//get_log($id);
	//delete($id);
	
?>
