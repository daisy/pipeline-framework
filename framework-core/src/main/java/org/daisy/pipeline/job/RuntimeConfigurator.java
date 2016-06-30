package org.daisy.pipeline.job;

import org.daisy.pipeline.event.EventBusProvider;

import com.google.common.eventbus.EventBus;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
    name = "job-context-factory",
    immediate = true,
    service = { RuntimeConfigurator.class }
)
public class RuntimeConfigurator {

        public interface Monitorable{
                public void setJobMonitorFactory(JobMonitorFactory factory);
        }

        public interface EventBusable{
                public void setEventBus(EventBus bus);
        }
      
        private EventBus bus;
        private JobMonitorFactory factory;

        /**
         * @return the bus
         */
        public EventBus getEventBus() {
                return bus;
        }


        /**
         * @return the factory
         */
        public JobMonitorFactory getFactory() {
                return factory;
        }

        @Reference(
            name = "event-bus-provider",
            unbind = "-",
            service = EventBusProvider.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC
        )
        public void setEventBus(EventBusProvider provider){
                this.bus=provider.get();
        }

        @Reference(
            name = "monitor",
            unbind = "-",
            service = JobMonitorFactory.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC
        )
        public void setJobMonitorFactory(JobMonitorFactory factory){
                this.factory=factory;
        }

        public void configure(Monitorable m){
                m.setJobMonitorFactory(this.factory);
        }

        public void configure(EventBusable e){
                e.setEventBus(this.bus);
        }
}
