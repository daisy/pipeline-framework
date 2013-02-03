package org.daisy.pipeline.job;

import java.net.URI;

import java.util.Collection;
import java.util.List;

import java.util.zip.ZipOutputStream;

import javax.xml.namespace.QName;

public interface ResultSet {
	public ZipOutputStream asZip(Collection<JobResult> results);
	public Collection<String> getPorts();
	public Collection<QName> getOptions();
	public Collection<JobResult> getResults(String port);
	public Collection<JobResult> getResults(QName option);
	public Collection<JobResult> getResults();
}
