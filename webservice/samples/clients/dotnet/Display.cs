using System.Xml;
using System;
using System.IO;

namespace PipelineWSClient
{
	public class Display
	{
		public static void prettyPrint(XmlDocument doc)
		{
			using (StringWriter stringWriter = new StringWriter())
			{
				XmlNodeReader xmlReader = new XmlNodeReader(doc);
				XmlTextWriter xmlWriter = new XmlTextWriter(stringWriter);
				xmlWriter.Formatting = Formatting.Indented;
				xmlWriter.Indentation = 1;
				xmlWriter.IndentChar = '\t';
				xmlWriter.WriteNode(xmlReader, true);
				Console.WriteLine(stringWriter.ToString());
			}
		}
		
		public static void displayScripts(XmlNode node)
		{
			XmlNamespaceManager manager = new XmlNamespaceManager(node.OwnerDocument.NameTable);
			manager.AddNamespace("ns", "http://www.daisy.org/ns/pipeline/data");

			XmlNodeList list = node.SelectNodes(".//ns:script", manager);
			int count = 1;
			Console.WriteLine("Scripts: ");
			foreach (XmlNode n in list)
			{
				Console.WriteLine(String.Format("{0}. {1}", count, displayScriptShort(n)));
				count++;
			}
		}
					
		public static void displayScript(XmlNode node)
		{
			XmlNamespaceManager manager = new XmlNamespaceManager(node.OwnerDocument.NameTable);
			manager.AddNamespace("ns", "http://www.daisy.org/ns/pipeline/data");

			Console.WriteLine("\nScript: ");
			XmlNode homepage = node.SelectSingleNode("./ns:homepage");
			if (homepage != null)
			{
				Console.WriteLine(String.Format("Homepage: {0}", homepage.InnerText));
			}
			foreach (XmlNode n in node.SelectNodes("./ns:input"))
			{
				Console.WriteLine(String.Format("Input argument \n\tname = {0}", n.Attributes.GetNamedItem("name").Value));
				Console.WriteLine(String.Format ("\tdesc = {0}", n.Attributes.GetNamedItem("desc").Value));
				Console.WriteLine(String.Format ("\tmedia type = {0}", n.Attributes.GetNamedItem("mediaType").Value));
				Console.WriteLine(String.Format ("\tsequence allowed = {0}", n.Attributes.GetNamedItem("sequenceAllowed").Value));
			}
			foreach (XmlNode n in node.SelectNodes("./ns:option"))
			{
				Console.WriteLine(String.Format("Option argument \n\tname = {0}", n.Attributes.GetNamedItem("name").Value));
				Console.WriteLine(String.Format ("\tdesc = {0}", n.Attributes.GetNamedItem("desc").Value));
				Console.WriteLine(String.Format ("\ttype = {0}", n.Attributes.GetNamedItem("type").Value));
				Console.WriteLine(String.Format ("\tmedia type = {0}", n.Attributes.GetNamedItem("mediaType").Value));
				Console.WriteLine(String.Format ("\trequired = {0}", n.Attributes.GetNamedItem("required").Value));
			}
		}
		private static string displayScriptShort(XmlNode node)
		{
			XmlNamespaceManager manager = new XmlNamespaceManager(node.OwnerDocument.NameTable);
			manager.AddNamespace("ns", "http://www.daisy.org/ns/pipeline/data");
			
			string nicename = node.SelectSingleNode("./ns:nicename", manager).InnerText;
			string description = node.SelectSingleNode("./ns:description", manager).InnerText;
			return String.Format("{0}\n\t{1}", nicename, description);
		}		
		
		public static void displayJobs(XmlNode node)
		{
			XmlNamespaceManager manager = new XmlNamespaceManager(node.OwnerDocument.NameTable);
			manager.AddNamespace("ns", "http://www.daisy.org/ns/pipeline/data");
			
			XmlNodeList list = node.SelectNodes(".//ns:job", manager);
			int count = 1;
			Console.WriteLine("Jobs: ");
			foreach (XmlNode n in list)
			{
				Console.WriteLine(String.Format("{0}. {1}", count, displayJobShort(n)));
				count++;
			}
		}
		public static void displayJob(XmlNode node)
		{
			XmlNamespaceManager manager = new XmlNamespaceManager(node.OwnerDocument.NameTable);
			manager.AddNamespace("ns", "http://www.daisy.org/ns/pipeline/data");
			
			Console.WriteLine("Job: ");
			Console.WriteLine (displayJobShort(node));
			
			XmlNodeList errors = node.SelectNodes(".//ns:error", manager);
			foreach (XmlNode n in errors)
			{
				Console.WriteLine (String.Format("ERROR: {0}.  {1}", n.Attributes.GetNamedItem("level"), n.InnerText));
			}
			
			XmlNodeList warnings = node.SelectNodes(".//ns:warning", manager);
			foreach (XmlNode n in warnings)
			{
				Console.WriteLine (String.Format("WARNING: {0}.  {1}", n.Attributes.GetNamedItem("level"), n.InnerText));
			}
		}
			 
		private static string displayJobShort(XmlNode node)
		{
			XmlNamespaceManager manager = new XmlNamespaceManager(node.OwnerDocument.NameTable);
			manager.AddNamespace("ns", "http://www.daisy.org/ns/pipeline/data");
			
			string id = node.Attributes.GetNamedItem("id").Value;
			string status =  node.Attributes.GetNamedItem("status").Value;
			
			return String.Format("Job ID = {0}, Status = {1}", id, status);
		}

		public static void displayResult(string id, string filename)
		{
			Console.WriteLine(String.Format("Result for {0} saved to {1}", id, filename));
		}

		public static void displayLog(string id, string log)
		{
			Console.WriteLine(String.Format("Log for job {0}: ", id));
			Console.WriteLine(log);
		}
		
	}
}