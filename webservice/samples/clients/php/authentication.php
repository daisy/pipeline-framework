<?php 
	class Authentication {
		
		private static $CLIENT_KEY = "clientkey";
		private static $CLIENT_SECRET = "supersecret";
		
		public static function prepare_authenticated_uri($uri) {
			$uristring = "";
			$timestamp = gmdate("Y-m-d\TH:i:s\Z");
			$nonce = Authentication::generate_nonce();
			$params = "key=" . Authentication::$CLIENT_KEY . "&time=" . $timestamp . "&nonce=" . $nonce;
			if (strpos($uri, "?") == false) {
				$uristring = $uri . "?" . $params;
			}
			else {
				$uristring = $uri . "&" . $params;
			}
			$hash = Authentication::generate_hash($uristring);
			return $uristring . "&sign=" . $hash;
		}
	
		private static function generate_hash($data) {
			$hash = hash_hmac("sha1", $data, Authentication::$CLIENT_SECRET, true);
			return rawurlencode(base64_encode($hash));		
		}
	
		// adapted from http://www.peej.co.uk/projects/phphttpdigest
		private static function generate_nonce() {
			$time = ceil(time() / 300) * 300;
			return md5(date('Y-m-d H:i', $time)  .':' . Authentication::$CLIENT_SECRET);
		}

	}
?>