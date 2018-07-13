import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.daisy.pipeline.modules.JarModuleBuilder;
import org.daisy.pipeline.modules.Module;
import org.daisy.pipeline.modules.ModuleRef;
import org.daisy.pipeline.modules.OSGIModuleBuilder;
import org.daisy.pipeline.xmlcatalog.XmlCatalog;
import org.daisy.pipeline.xmlcatalog.XmlCatalogParser;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class MockModule implements ModuleRef {
    
    private Module instance;
    private XmlCatalogParser catalogParser;
    
    public Module get() {
        if (instance == null) {
            URI jarFileURI; {
                try {
                    jarFileURI = MockModule.class.getProtectionDomain().getCodeSource().getLocation().toURI();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
            File jarFile; {
                try {
                    jarFile = new File(jarFileURI);
                } catch (IllegalArgumentException e) {
                    // Could be because we are running in OSGi context
                    instance = OSGiHelper.getOSGiModule(catalogParser);
                    return instance;
                }
            }
            XmlCatalog catalog = catalogParser.parse(
                jarFile.isDirectory() ?
                new File(jarFile, "/META-INF/catalog.xml").toURI() :
                URI.create("jar:" + jarFileURI.toASCIIString() + "!/META-INF/catalog.xml")
            );
            instance = new JarModuleBuilder()
                .withName("mock-module")
                .withVersion("1.0.0")
                .withTitle("mock-module")
                .withJarFile(jarFile)
                .withCatalog(catalog)
                .build();
        }
        return instance;
    }
    
    public void setParser(XmlCatalogParser parser) {
        catalogParser = parser;
    }
    
    private static abstract class OSGiHelper {
        static Module getOSGiModule(XmlCatalogParser catalogParser) {
            Bundle bundle = FrameworkUtil.getBundle(MockModule.class);
            URI catalogURI; {
                try {
                    catalogURI = bundle.getResource("META-INF/catalog.xml").toURI();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
            return new OSGIModuleBuilder()
                .withBundle(bundle)
                .withCatalog(catalogParser.parse(catalogURI))
                .build();
        }
    }
}
