package org.daisy.pipeline.job.priority;

import org.daisy.pipeline.job.Job;

public class FuzzyJobFactory {
        
        private InferenceEngine engine;
        /**
         *
         */
        protected FuzzyJobFactory(InferenceEngine engine) {
                this.engine=engine;
        }

        public static FuzzyJobFactory newFuzzyJobFactory(){
                InferenceEngine engine = buildEngine();
                return new FuzzyJobFactory(engine);
        }


        public FuzzyJobRunnable newFuzzyJob(Job job,Runnable runnable){
                return new FuzzyJobRunnable(job,runnable,engine);
        }

        static InferenceEngine buildEngine() {
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
        
                InferenceEngine engine=new InferenceEngine();
                //add the fuzzy variables
                engine.add(time);
                engine.add(clientPriority);
                engine.add(jobPriority);

                return engine;
        }
}
