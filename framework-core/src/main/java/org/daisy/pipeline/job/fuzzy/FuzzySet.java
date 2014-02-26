package org.daisy.pipeline.job.fuzzy;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * FuzzySet needs a name, a weight and a function to calculate the membership value 
 * of a given x
 *
 *
*/
public class FuzzySet {
        private String name;
        private double weight;
        private Function<Double, Double> membership;

        /**
         * @param name
         * @param weight
         * @param membership
         */
        public FuzzySet(String name, double weight,
                        Function<Double, Double> membership) {
                this.name = name;
                this.weight = weight;
                this.membership = membership;
        }

        /**
         * @return the name
         */
        public String getName() {
                return name;
        }

        /**
         * @return the weight
         */
        public double getWeight() {
                return weight;
        }

        /**
         * @return the membership
         */
        public Function<Double, Double> getMembership() {
                return membership;
        }

}
