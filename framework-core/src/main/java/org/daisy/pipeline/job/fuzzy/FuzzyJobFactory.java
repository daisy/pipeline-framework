package org.daisy.pipeline.job.fuzzy;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.PrioritizedJob;
import org.daisy.pipeline.job.priority.ForwardingPrioritableRunnable;
import org.daisy.pipeline.job.priority.PrioritizableRunnable;

import com.google.common.base.Supplier;

public class FuzzyJobFactory {
        


        public static PrioritizableRunnable newFuzzyRunnable(final Job job,Runnable runnable){
                return new PrioritizableRunnable(runnable,
                                new FuzzyPriorityCalculator(ENGINE,
                                        new Supplier<double[]>() {
                                                @Override
                                                public double[] get() {
                                                        return new double[]{
                                                                job.getContext().getClient().getPriority().asDouble(),
                                                                job.getPriority().asDouble()};
                                                }

                                        }));


        }





        static final InferenceEngine ENGINE;
        static{
                //The weight values are set so that there will be no starvation
                //a very old task with low prios will exectute a brand new task with the higest prios
                //time is the real fuzzy deal here
                FuzzyVariable time=new FuzzyVariable();
                time.add(new FuzzySet("new task",30,MembershipFunctions.newTrapezoidFunction(0.0,0.0,0.0,0.5)));
                time.add(new FuzzySet("regular task",60,MembershipFunctions.newTrapezoidFunction(0.0,0.5,0.5,1.0)));
                time.add(new FuzzySet("old task",90,MembershipFunctions.newTrapezoidFunction(0.5,1.0,1.0,1.0)));
                //client priority
                FuzzyVariable clientPriority=new FuzzyVariable();
                clientPriority.add(new FuzzySet("low priority",10,MembershipFunctions.newEqualsFunction(0.0)));
                clientPriority.add(new FuzzySet("medium priority",20,MembershipFunctions.newEqualsFunction(0.5)));
                clientPriority.add(new FuzzySet("high priority",30,MembershipFunctions.newEqualsFunction(1.0)));
                //client priority
                FuzzyVariable jobPriority=new FuzzyVariable();
                jobPriority.add(new FuzzySet("low priority",10,MembershipFunctions.newEqualsFunction(0.0)));
                jobPriority.add(new FuzzySet("medium priority",20,MembershipFunctions.newEqualsFunction(0.5)));
                jobPriority.add(new FuzzySet("high priority",30,MembershipFunctions.newEqualsFunction(1.0)));
        
                ENGINE=new InferenceEngine();
                //add the fuzzy variables
                ENGINE.add(time);
                ENGINE.add(clientPriority);
                ENGINE.add(jobPriority);

        }
}
