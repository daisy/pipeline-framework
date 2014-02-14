package org.daisy.pipeline.job.priority;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Function;


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

        static class FuzzySet implements Function<Double,Double>{
                /** A fuzzy set which basically have this shape
                 *    --------     1
                 *   /        \
                 *  /          \
                 * /            \  0
                */
                private double []points;
                private String name;
                private double weight;

                private FuzzySet(String name, double weight, double x1, double x2, double x3,double x4){
                        this.name=name;
                        this.weight=weight;
                        this.points=new double[]{x1,x2,x3,x4};
                }
                /**
                 *  Builds a new trapeziod by defining the start, end of the first slope, start of the last slope and the ending point.
                 *  x1<=x2<=x3<=x4
                 */
                public static FuzzySet newSet(String name, double weight, double x1, double x2, double x3,double x4){
                        if (!(x1<=x2 && x2<=x3 && x3<=x4)){
                                throw new IllegalArgumentException(String.format("Fuzzy set boundaries are not x1<=x2<=x3<=x4 (%s, %s, %s, %s)", 
                                                        x1,x2,x3,x4));
                        }


                        return new FuzzySet(name,weight,x1,x2,x3,x4);

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

                @Override
                /**
                 * Computes the memebership value
                 */
                public Double apply(Double x) {
                        double res=-1;
                        int seg=getSegment(x);
                        switch(seg){
                                case -1: 
                                        res=0;
                                        break;
                                case 0: //(x-x_0)/(x_1-x_0)
                                        res=(x-this.points[seg])/(this.points[seg+1]-this.points[seg]);
                                        break;
                                case 1:
                                        res=1;
                                        break;
                                case 2://(x1-x)/(x_1-x_0)
                                        res=(this.points[seg+1]-x)/(this.points[seg+1]-this.points[seg]);
                                        break;
                        }
                        return new Double(res);
                }
              
                /**
                 * Returns the segment index where this vlaue falls into. -1 is out of the bounds
                 * 
                 */
                int getSegment(double x){
                        if (x<0 || x>1.0){
                                return -1;
                        }
                        for (int  i=0;i<this.points.length-1;i++){
                                if(inSegment(i,x)){
                                        return i;
                                }
                        }
                        return -1;
                                
                                
                }
                /**
                 *
                 * Check the segment where x has to be projected into
                 */
                boolean inSegment(int segment,double x){
                        return (x>=this.points[segment] && x<=this.points[segment+1]);
                }


                
        }
}


