package org.daisy.pipeline.job.fuzzy;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.daisy.pipeline.job.fuzzy.FuzzyRunnable;
import org.daisy.pipeline.job.fuzzy.InferenceEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Function;

@RunWith(MockitoJUnitRunner.class)
public class FuzzyRunnableTest   {

        @Mock private InferenceEngine engine;
        @Mock Runnable runnable;
        FuzzyRunnable fuzzyRunnable;
        @Before
        public void setUp(){
                fuzzyRunnable = spy(new FuzzyRunnable(runnable,engine) {

                        @Override
                        public double[] getPriorities() {
                                return null;
                        }

                        @Override
                        public double getPriority() {
                                return 0;
                        }

                        @Override
                        public double forcePriority(double priority) {
                                // TODO Auto-generated method stub
                                return 0;
                        }});


        }

        @Test
        public void getScore(){
                double vals[]= new double[]{1.0};
                when(engine.getScore(anyDouble())).thenReturn(1.0);
                when(fuzzyRunnable.getPriorities()).thenReturn(vals);

                fuzzyRunnable.getScore();
                verify(engine,times(1)).getScore(0.0,1.0);
                fuzzyRunnable.getScore();
                verify(engine,times(1)).getScore(0.0,1.0);

        }
        
        @Test
        public void setDirty(){
                double vals[]= new double[]{1.0};
                when(engine.getScore(anyDouble())).thenReturn(1.0);
                when(fuzzyRunnable.getPriorities()).thenReturn(vals);

                fuzzyRunnable.getScore();
                verify(engine,times(1)).getScore(0.0,1.0);
                fuzzyRunnable.markDirty();
                fuzzyRunnable.getScore();
                verify(engine,times(2)).getScore(0.0,1.0);

        }

        @Test
        public void updateRelativeTime(){
                double vals[]= new double[]{1.0};
                when(engine.getScore(anyDouble())).thenReturn(1.0);
                when(fuzzyRunnable.getPriorities()).thenReturn(vals);
                when(fuzzyRunnable.getTimestamp()).thenReturn(0L);

                Function<Long,Double> normaliser= new Function<Long,Double>(){

                                        @Override
                                        public Double apply(Long arg0) {
                                                return 0.5;
                                        }
                };
                fuzzyRunnable.getScore();
                verify(engine,times(1)).getScore(0.0,1.0);
                fuzzyRunnable.setRelativeWaitingTime(normaliser);
                fuzzyRunnable.getScore();
                verify(engine,times(1)).getScore(0.5,1.0);

        }
}
