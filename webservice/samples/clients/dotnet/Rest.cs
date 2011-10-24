using System.Net;
using System.IO;
using System.Text;
using System.Xml;
using System.Collections.Generic;

namespace PipelineWSClient
{
	// wraps basic HTTP methods
	class Rest
	{
		// success: return response body
		// failure: return empty string
		public static string getResource(string uri)
		{
			HttpWebRequest request = (HttpWebRequest)WebRequest.Create(uri);
			request.Method = "GET";
			HttpWebResponse response = (HttpWebResponse)request.GetResponse();
			
			// 200
			if (response.StatusCode == HttpStatusCode.OK) 
			{
				Stream receiveStream = response.GetResponseStream();
				return normalizeData(receiveStream);
				//StreamReader readStream = new StreamReader(receiveStream, Encoding.UTF-8);
				//return readStream.ReadToEnd();
			}
			// 404
			else if (response.StatusCode == HttpStatusCode.NotFound) 
			{
				return "";
			}	
			else 
			{
				return "";
			}
		}
		
		// success: return XmlDocument
		// failure: return null
		public static XmlDocument getResourceAsXml(string uri)
		{
			string resource = getResource(uri);
			if (resource == "") 
			{
				return null;
			}
			
			XmlDocument doc = new XmlDocument();
			doc.LoadXml(resource);
			return doc;
		}
		
		// success: return the location of the new resource
		// failure: return empty string
		// use this to post non-mulitpart data (all data in one string)
		public static string postResource(string uri, string postData)
		{
			byte[] bytes = System.Text.Encoding.UTF8.GetBytes(postData);
			
			HttpWebRequest request = (HttpWebRequest)WebRequest.Create(uri);
  			request.Method = "POST";
  			request.KeepAlive = true;
			request.ContentType = "application/xml";
            request.ContentLength = bytes.Length;
  			Stream requestStream = request.GetRequestStream();
			requestStream.Write(bytes, 0, bytes.Length);
			requestStream.Close();
			
			HttpWebResponse response = (HttpWebResponse)request.GetResponse();
			
		  	// 201
			if (response.StatusCode == HttpStatusCode.Created)
			{
				return response.Headers.Get("content-location");
			}
			// 400
			else if (response.StatusCode == HttpStatusCode.BadRequest)
			{
				return "";
			}
			else 
			{
				return "";
			}
			
		}
		
		// success: return the location of the new resource
		// failure: return empty string
		// use this function to post multipart data, where postData contains each segment
		// and fileToUpload is the file to upload
		public static string postResource(string uri, Dictionary<string, string> postData,
			FileInfo fileToUpload, string fileMimeType, string fileFormKey)
		{
			HttpWebRequest request = (HttpWebRequest)WebRequest.Create(uri);
  			request.Method = "POST";
  			request.KeepAlive = true;
  			string boundary = MultipartBoundary.CreateFormDataBoundary();
  			request.ContentType = "multipart/form-data; boundary=" + boundary;
  			Stream requestStream = request.GetRequestStream();
  			postData.WriteMultipartFormData(requestStream, boundary);
		  	if (fileToUpload != null)
		  	{
		    	fileToUpload.WriteMultipartFormData(requestStream, boundary, fileMimeType, fileFormKey);
		  	}
		  	byte[] endBytes = System.Text.Encoding.UTF8.GetBytes("--" + boundary + "--");
		  	requestStream.Write(endBytes, 0, endBytes.Length);
		  	requestStream.Close();
		    HttpWebResponse response = (HttpWebResponse)request.GetResponse();
			
		  	// 201
			if (response.StatusCode == HttpStatusCode.Created)
			{
				return response.Headers.Get("content-location");
			}
			// 400
			else if (response.StatusCode == HttpStatusCode.BadRequest)
			{
				return "";
			}
			else 
			{
				return "";
			}
			
		}
		
		// success: return true
		// failure: return false
		public static bool deleteResource(string uri)
		{
			HttpWebRequest request = (HttpWebRequest)WebRequest.Create(uri);
			request.Method = "DELETE";
			HttpWebResponse response = (HttpWebResponse)request.GetResponse();
			
			// 204
			if (response.StatusCode == HttpStatusCode.NoContent)
			{
				return true;
			}
			else 
			{
				return false;
			}
		}
		
		// filter out null characters in the stream, otherwise the XML parser fails
		private static string normalizeData(Stream data)
		{
			byte[] bytes = streamToByteArray(data);
			using(MemoryStream buffer = new MemoryStream(bytes.Length)) 
			{
				foreach (byte b in bytes) 
				{
					if (b > 0x0) 
					{
						buffer.WriteByte(b);
					}
				}
				bytes = buffer.ToArray();
			}
			string response = Encoding.UTF8.GetString(bytes);
			return response;
		}
		
		private static byte[] streamToByteArray(Stream data)
		{
		    byte[] buffer = new byte[16*1024];
		    using (MemoryStream ms = new MemoryStream())
		    {
		        int read;
		        while ((read = data.Read(buffer, 0, buffer.Length)) > 0)
		        {
		            ms.Write(buffer, 0, read);
		        }
		        return ms.ToArray();
		    }
		}
	}
}