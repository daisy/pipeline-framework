package org.daisy.pipeline.xproc.impl;

import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;

import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.io.WritableDocument;
import com.xmlcalabash.io.WritablePipe;
import com.xmlcalabash.model.Serialization;


public class WritableFactory {

	public static WritablePipe getWritable(Result res,XProcRuntime xproc,Serialization serial){
		if(res instanceof SAXResult){
			return new WritableDocument(xproc,  ((SAXResult)res).getSystemId(), serial);
		}else if(res instanceof StreamResult ){
			return new WritableOutputStream(xproc,  ((StreamResult)res).getOutputStream(), serial);
		//}else if(res instanceof DOMResult ){
		//		return new WritableOutputSream(xproc,  ((StreamResult)res).getOutputStream(), serial);
		}else 
			throw new IllegalArgumentException("The class "+res.getClass().getName()+" is not currently supported");
		
		
		
	}
	
}
