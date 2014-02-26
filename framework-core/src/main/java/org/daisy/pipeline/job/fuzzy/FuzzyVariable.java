package org.daisy.pipeline.job.fuzzy;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Supplier;


/**
 * The x limits are always going to be between 0 and 1
 *
 */
public class FuzzyVariable {

        List<FuzzySet> sets=new LinkedList<FuzzySet>();
      
        public FuzzyVariable add(FuzzySet set){
                this.sets.add(set);
                return this;
        }
        
        public Iterable<FuzzySet> getSets(){
                return this.sets;
        }

 

                
}


