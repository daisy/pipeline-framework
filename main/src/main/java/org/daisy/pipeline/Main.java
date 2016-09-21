package org.daisy.pipeline;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ServiceLoader;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.SystemPropertiesLookup;
import org.daisy.common.service.CreateOnStart;
import org.daisy.pipeline.webservice.impl.PipelineWebService_SPI;
import  org.daisy.pipeline.job.JobManagerFactory_SPI;

import com.google.common.collect.ImmutableList;

public class Main {


        /** Loads the config file under ${org.daisy.pipeline.home}/etc/system.properties 
        */ 
        public static void loadConfig(){
                String pipelineHome = System.getProperty("org.daisy.pipeline.home","");
                if (pipelineHome.isEmpty()){
                        throw new RuntimeException("Propterty org.daisy.pipeline.home is not set");
                }
                Path propertiesPath = Paths.get(pipelineHome,"etc","system.properties");
                Parameters params = new Parameters();
                FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                        new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                        .setFileName(propertiesPath.toString()));

                PropertiesConfiguration config = null;

                try{
                        config = (PropertiesConfiguration) builder.getConfiguration();
                }catch(ConfigurationException cex){
                        throw new RuntimeException(cex);
                }
                ConfigurationInterpolator allSystem = new ConfigurationInterpolator();
                allSystem.addDefaultLookup( new SystemPropertiesLookup());
                config.setInterpolator(allSystem);

                for(String key:ImmutableList.copyOf(config.getKeys())){
                        System.out.println(key+":"+config.getString(key));
                        System.setProperty(key,config.getString(key));
                }

        }

        public static void main(String[] args) {

                Main.loadConfig();



                for (CreateOnStart c : ServiceLoader.load(CreateOnStart.class));

                //new JobManagerFactory_SPI();
                //new org.daisy.pipeline.dtbooktoepub3.DtbookToEpub3_SPI();
                new PipelineWebService_SPI();

                //ServiceLoader<PipelineWebService> sl =  ServiceLoader.load(PipelineWebService.class);

        }


        //static class AllSysteLookup extends Lookup
        //{
                //public String lookup(String varName){
                        //return System.getProperty(varName,"");
                //}
        //}


}
