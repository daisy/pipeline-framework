package org.daisy.pipeline.persistence.jobs;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.daisy.common.base.Provider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.job.JobContext;
import org.daisy.pipeline.job.ResultSet;

import com.google.common.collect.Lists;

class ContextHydrator {
	//
	static void hydrateInputPorts(XProcInput.Builder builder,List<PersistentInputPort> inputPorts ){
		for ( PersistentInputPort input:inputPorts){
			for (PersistentSource src:input.getSources()){
				builder.withInput(input.getName(),src);
			}
		}
	}

	static  void hydrateOptions(XProcInput.Builder builder,List<PersistentOption> options){
		for (PersistentOption option:options){
			builder.withOption(option.getName(),option.getValue());
		}
	}

	static void hydrateParams(XProcInput.Builder builder,List<PersistentParameter> params){
		for(PersistentParameter param:params){
			builder.withParameter(param.getPort(),param.getName(),param.getValue());
		}
	}

	static void hydrateResultPorts(ResultSet.Builder builder,List<PersistentPortResult> portResults){
		for(PersistentPortResult pRes: portResults){
			builder.addResult(pRes.getPortName(),pRes.getJobResult());
		}
	}

	static void hydrateResultOptions(ResultSet.Builder builder,List<PersistentOptionResult> optionResults){
		for(PersistentOptionResult pRes: optionResults){
			builder.addResult(pRes.getOptionName(),pRes.getJobResult());
		}
	}
	
	static List<PersistentInputPort> dehydrateInputPorts(JobContext ctxt){
		List<PersistentInputPort> inputPorts = Lists.newLinkedList();
		for( XProcPortInfo portName:ctxt.getScript().getXProcPipelineInfo().getInputPorts()){
			PersistentInputPort anon=new PersistentInputPort(ctxt.getId(),portName.getName());
			for (Provider<Source> src:ctxt.getInputs().getInputs(portName.getName())){
				anon.addSource(new PersistentSource(src.provide().getSystemId()));
			}
			inputPorts.add(anon);
		}
		return inputPorts;
	}

	static List<PersistentOption> dehydrateOptions(JobContext ctxt){
		List<PersistentOption> options = Lists.newLinkedList();
		for(QName option:ctxt.getInputs().getOptions().keySet()){
			options.add(new PersistentOption(ctxt.getId(),option,ctxt.getInputs().getOptions().get(option)));
		}
		return options;
	}

	static List<PersistentParameter> dehydrateParameters(JobContext ctxt){
		List<PersistentParameter> parameters = Lists.newLinkedList();
		for( String portName:ctxt.getScript().getXProcPipelineInfo().getParameterPorts()){
			for (QName paramName :ctxt.getInputs().getParameters(portName).keySet()){
				parameters.add(new PersistentParameter(ctxt.getId(),portName,paramName,ctxt.getInputs().getParameters(portName).get(paramName)));
			}
		}
		return parameters;
	}
}
