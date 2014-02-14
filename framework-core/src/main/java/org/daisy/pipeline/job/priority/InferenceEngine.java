package org.daisy.pipeline.job.priority;

import java.util.Collection;

import org.daisy.pipeline.job.priority.FuzzyVariable.FuzzySet;


public class InferenceEngine {
        
       public InferenceEngine add(FuzzySet set){
               return this;
       }
       public float getScore(Collection<Float> crispValues){
               //size(values) == size(sets)
               
               //build a set of functions to compute the final score
               return 0;
       }

}
