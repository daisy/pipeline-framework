using System;
using System.Xml;
using System.Collections.Generic;

namespace PipelineWSClient
{
	class MainClass
	{
		public static void Main (string[] args)
		{
			Actions.getJobs();
		}
		
		
	}
	
	class Actions
	{
		public static void getScripts()
		{
			XmlDocument doc = Resources.getScripts();
			Display.prettyPrint(doc);
		}
		
		public static void getScript()
		{
			XmlDocument doc = Resources.getScript("http://www.daisy.org/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl");
			Display.prettyPrint(doc);
		}
		
		public static void postInlineJob(string filepath)
		{
			XmlDocument inlineDoc = new XmlDocument();
			inlineDoc.Load(@"../../../testdata/dtbook.xml");
			
			InlineJob job = new InlineJob();
			job.Script = "http://www.daisy.org/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl";
			job.Options.Add("opt-mods-filename", "the-mods-file.xml");
			job.Options.Add("opt-css-filename", "the-css-file.xml");
			job.Options.Add("opt-zedai-filename", "the-zedai-file.xml");
			
			List<XmlDocument> documents = new List<XmlDocument>();
			documents.Add(inlineDoc);
			job.Inputs.Add("source", documents);
			XmlDocument jobRequestDoc = job.getAsXml();
			
			string jobId = Resources.postJob(jobRequestDoc);
			Console.WriteLine(jobId);
			
		}
		
		public static void postMultipartJob()
		{
		}
		
		public static void getJobs()
		{
			XmlDocument doc = Resources.getJobs();
			if (doc == null)
			{
				Console.WriteLine("No data returned.");
				return;
			}
			Display.prettyPrint(doc);			
		}
		
		public static void getJob(string id)
		{
			XmlDocument doc = Resources.getJob(id);
			if (doc == null)
			{
				Console.WriteLine("No data returned.");
				return;
			}
			Display.prettyPrint(doc);
		}
		
		public static void getLog(string id)
		{
			string status = Resources.getJobStatus(id);
			if (status != "DONE")
			{
				Console.WriteLine (String.Format("Cannot get log until job is done.  Job status: {0}.", status));
				return;
			}
			
			string log = Resources.getLog(id);
			if (log.Length == 0)
			{
				Console.WriteLine("No data returned.");
				return;
			}
			Console.WriteLine(log);
		}
		
		public static void getResult(string id)
		{
			string status = Resources.getJobStatus(id);
			if (status != "DONE")
			{
				Console.WriteLine (String.Format("Cannot get result until job is done.  Job status: {0}.", status));
				return;
			}
			string filepath = String.Format("/tmp/{0}.zip", id);
			Resources.getResult(id, filepath);
			
			Console.WriteLine(String.Format("Saved to {0}", filepath));
		}
		
		public static void deleteJob(string id)
		{
			bool result = Resources.deleteJob(id);
			if (!result)
			{
				Console.WriteLine("Error deleting job.");
			}
			else
			{
				Console.WriteLine ("Job deleted.");
			}
		}
	}
}
