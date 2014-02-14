package org.daisy.pipeline.job.priority;

import org.daisy.pipeline.job.priority.FuzzyVariable.FuzzySet;
import org.junit.Assert;
import org.junit.Test;

public class FuzzyVariableTest {

        @Test 
        public void newFuzzySet(){
                String name="Name";
                double weight=10.0;
                FuzzySet set=FuzzySet.newSet(name,weight,0.0,0.3,0.4,0.7);
                Assert.assertEquals(weight,set.getWeight(),0.0);
                Assert.assertEquals("Check name",name,set.getName());

        }

        @Test(expected=IllegalArgumentException.class)
        public void newBadBoundariesOrder(){
                FuzzySet set=FuzzySet.newSet("",0.0,0.5,0.3,0.4,0.7);
        }

        @Test
        public void inSegmentOut(){
                FuzzySet set=FuzzySet.newSet("",0.0,0.2,0.6,0.7,0.8);
                Assert.assertEquals("Out of lower boundary 0.1",-1,set.getSegment(0.1));
                Assert.assertEquals("Out of upper boundary 0.9",-1,set.getSegment(0.9));
        }
        @Test
        public void applyOutOfSet(){
                FuzzySet set=FuzzySet.newSet("",0.0,0.2,0.6,0.7,0.8);
                Assert.assertEquals("membership 0, out of lower ",0.0,set.apply(0.1),0.0);
                Assert.assertEquals("memebership 0, out of upper0.9",0.0,set.apply(0.9),0.0);
        }


        @Test
        public void inSegment(){
                FuzzySet set=FuzzySet.newSet("",0.0,0.2,0.6,0.7,0.8);
                Assert.assertEquals("seg 0",0,set.getSegment(0.3));
                Assert.assertEquals("seg 1",1,set.getSegment(0.65));
                Assert.assertEquals("seg 2",2,set.getSegment(0.75));
        }

        @Test
        public void applyFirstSeg(){
                FuzzySet set=FuzzySet.newSet("",0.0,0.2,0.6,0.7,0.8);
                //(x-0.2)/(0.6-0.2)=0.25
                Assert.assertEquals("Slope",0.25,set.apply(0.3),0.01);
        }

        @Test
        public void applySecondSeg(){
                FuzzySet set=FuzzySet.newSet("",0.0,0.2,0.6,0.7,0.8);
                //(x-0.2)/(0.6-0.2)=0.25
                Assert.assertEquals("Slope",1.0,set.apply(0.65),0.0);
        }

        @Test
        public void applyThirdSeg(){
                FuzzySet set=FuzzySet.newSet("",0.0,0.2,0.6,0.7,0.8);
                //(0.8-0.72)/(0.8-0.7)=0.25
                Assert.assertEquals("Slope",0.8,set.apply(0.72),0.0);
        }
}
