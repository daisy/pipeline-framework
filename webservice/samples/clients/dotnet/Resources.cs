using System;
using System.Collections.Generic;
using System.Xml;
using System.IO;
using System.Net;

namespace PipelineWSClient
{
	class Resources
	{
		private static string JOB_REQUEST = "jobRequest";
		private static string JOB_DATA = "jobData";
		
		public static string baseUri = "http://localhost:8182/ws";
		
		public static XmlDocument getScript(string id)
		{
			string uri = String.Format("{0}/script?id={1}", baseUri, id);
			return Rest.getResourceAsXml(uri);
		}	
		
		public static XmlDocument getScripts()
		{
			string uri = String.Format("{0}/scripts", baseUri);
			return Rest.getResourceAsXml(uri);
		}
		
		public static XmlDocument getJob(string id)
		{
			string uri = String.Format("{0}/jobs/{1}", baseUri, id);
			return Rest.getResourceAsXml(uri);
		}
		
		public static XmlDocument getJobs()
		{
			string uri = String.Format("{0}/jobs", baseUri);
			return Rest.getResourceAsXml(uri);
		}
		
		public static string getLog(string id)
		{
			string uri = String.Format("{0}/jobs/{1}/log", baseUri, id);
			return Rest.getResource(uri);
		}
		
		public static void getResult(string id, string filepath)
		{
			string uri = String.Format("{0}/jobs/{1}/result.zip", baseUri, id);
			using (var client = new WebClient())
			{
				client.DownloadFile(uri,filepath);
			}
		}
		
		public static bool deleteJob(string id)
		{
			string uri = String.Format("{0}/jobs/{1}", baseUri, id);
			return Rest.deleteResource(uri);
		}
		
		public static string postJob(XmlDocument request)
		{
			string uri = String.Format ("{0}/jobs", baseUri);
			return Rest.postResource(uri, xmlDocToString(request));
		}
			
		public static string postJob(XmlDocument request, FileInfo data)
		{
			string uri = String.Format ("{0}/jobs", baseUri);
			Dictionary<string, string> postData = new Dictionary<string, string>();
			postData.Add(JOB_REQUEST, xmlDocToString(request));
			string fileMimeType = "application/zip";
			string fileFormKey = JOB_DATA;
			return Rest.postResource(uri, postData, data, fileMimeType, fileFormKey);			
		}	
		
		// returns "DONE", "IDLE", or "RUNNING"
		// status isn't a core pipeline resource, but it's useful nonetheless
		public static string getJobStatus(string id)
		{
			
			XmlDocument doc = getJob(id);
			XmlNamespaceManager manager = new XmlNamespaceManager(doc.NameTable);
			manager.AddNamespace("ns", "http://www.daisy.org/ns/pipeline/data");

			XmlNode node = doc.SelectSingleNode("//ns:job", manager);
			return node.Attributes.GetNamedItem("status").Value;
		}
		
		private static string xmlDocToString(XmlDocument doc)
		{
			StringWriter stringWriter = new StringWriter();
			XmlTextWriter textWriter = new XmlTextWriter(stringWriter);
			doc.WriteTo(textWriter);
			string retval = stringWriter.ToString();
			return retval;
		}
	}
}

