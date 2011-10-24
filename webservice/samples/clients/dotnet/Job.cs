using System;
using System.Collections.Generic;
using System.Xml;
using System.IO;

namespace PipelineWSClient
{
	public class MultipartJob : JobBase
	{
		public FileInfo Data {get; set;}
		public Dictionary<string, List<string>> Inputs {get; private set;}
		
		public MultipartJob()
		{
			Inputs = new Dictionary<string, List<string>>();
		}
		
		new public XmlDocument getAsXml()
		{
			XmlDocument doc = base.getAsXml();
			InputHelper.writeInput(Inputs, doc);
			return doc;
		}
		
	}
	public class InlineJob : JobBase
	{
		public Dictionary<string, List<XmlDocument>> Inputs {get; private set;}
		
		public InlineJob()
		{
			Inputs = new Dictionary<string, List<XmlDocument>>();
		}
		
		new public XmlDocument getAsXml()
		{
			XmlDocument doc = base.getAsXml();
			InputHelper.writeInput(Inputs, doc);
			return doc;
		}
	}
	
	// don't use directly; just a base class
	public class JobBase
	{
		public string Script {get; set;}
		public Dictionary<string, string> Options {get; private set;}
		
		public JobBase()
		{
			Options = new Dictionary<string, string>();
		}
		// create a jobRequest document
		public XmlDocument getAsXml()
		{
			XmlDocument doc = new XmlDocument();
			XmlProcessingInstruction pi = doc.CreateProcessingInstruction("xml", "version=\"1.0\"");
			doc.AppendChild(pi);
			XmlNode root = doc.CreateElement("jobRequest", "http://www.daisy.org/ns/pipeline/data");
			doc.AppendChild(root);
			
			XmlNode script = doc.CreateElement("script", "http://www.daisy.org/ns/pipeline/data");
			XmlAttribute scriptAttr = doc.CreateAttribute("href");
			scriptAttr.Value = Script;
			script.Attributes.SetNamedItem(scriptAttr);
			root.AppendChild(script);
			
			foreach(string key in Options.Keys)
			{
				XmlNode option = doc.CreateElement("option", "http://www.daisy.org/ns/pipeline/data");
				XmlAttribute nameAttr = doc.CreateAttribute("name");
				nameAttr.Value = key;
				option.Attributes.SetNamedItem(nameAttr);
				option.InnerText = Options[key];
				root.AppendChild(option);
			}
			
			return doc;
		}
	}
	
	public class InputHelper
	{
		public static void writeInput(Dictionary<string, List<string>> data, XmlDocument doc)
		{
			foreach(string key in data.Keys)
			{
				XmlNode inputNode = createInputNode(key, doc);
				
				foreach(string s in data[key])
				{
					XmlNode fileNode = createFileNode(s, doc);
					inputNode.AppendChild(fileNode);
				}
				
				doc.DocumentElement.AppendChild(inputNode);
			}
		}
		
		public static void writeInput(Dictionary<string, List<XmlDocument>> data, XmlDocument doc)
		{
			foreach(string key in data.Keys)
			{
				XmlNode inputNode = createInputNode(key, doc);
				
				foreach(XmlDocument inlineDoc in data[key])
				{
					XmlNode docwrapperNode = createDocwrapperNode(inlineDoc, doc);
					inputNode.AppendChild(docwrapperNode);
				}
				
				doc.DocumentElement.AppendChild(inputNode);
			}
		}
		
		private static XmlNode createInputNode(string name, XmlDocument doc)
		{
			XmlNode node = doc.CreateElement("input", "http://www.daisy.org/ns/pipeline/data");
			XmlAttribute attr = doc.CreateAttribute("name");
			attr.Value = name;
			node.Attributes.SetNamedItem(attr);
			return node;
		}
		
		private static XmlNode createFileNode(string src, XmlDocument doc)
		{
			XmlNode node = doc.CreateElement("file", "http://www.daisy.org/ns/pipeline/data");
			XmlAttribute attr = doc.CreateAttribute("src");
			attr.Value = src;
			node.Attributes.SetNamedItem(attr);
			return node;
		}
		
		private static XmlNode createDocwrapperNode(XmlDocument inlineDoc, XmlDocument doc)
		{
			XmlNode node = doc.CreateElement("docwrapper", "http://www.daisy.org/ns/pipeline/data");
			
			XmlNode inlineDocRoot = doc.ImportNode(inlineDoc.DocumentElement, true);
			node.AppendChild(inlineDocRoot);
			return node;	
		}
	}
	
}

