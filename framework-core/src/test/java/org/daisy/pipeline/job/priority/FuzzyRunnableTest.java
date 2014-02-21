package org.daisy.pipeline.job.priority;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Function;

@RunWith(MockitoJUnitRunner.class)
public class FuzzyRunnableTest   {

        @Mock private InferenceEngine engine;
        FuzzyRunnable runnable;
        @Before
        public void setUp(){
                runnable = spy(new FuzzyRunnable(engine) {

                        @Override
                        public void run() {

                        }

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
                when(runnable.getPriorities()).thenReturn(vals);

                runnable.getScore();
                verify(engine,times(1)).getScore(0.0,1.0);
                runnable.getScore();
                verify(engine,times(1)).getScore(0.0,1.0);

        }
        
        @Test
        public void setDirty(){
                double vals[]= new double[]{1.0};
                when(engine.getScore(anyDouble())).thenReturn(1.0);
                when(runnable.getPriorities()).thenReturn(vals);

                runnable.getScore();
                verify(engine,times(1)).getScore(0.0,1.0);
                runnable.markDirty();
                runnable.getScore();
                verify(engine,times(2)).getScore(0.0,1.0);

        }

        @Test
        public void updateRelativeTime(){
                double vals[]= new double[]{1.0};
                when(engine.getScore(anyDouble())).thenReturn(1.0);
                when(runnable.getPriorities()).thenReturn(vals);
                when(runnable.getTimestamp()).thenReturn(0L);

                Function<Long,Double> normaliser= new Function<Long,Double>(){

                                        @Override
                                        public Double apply(Long arg0) {
                                                return 0.5;
                                        }
                };
                runnable.getScore();
                verify(engine,times(1)).getScore(0.0,1.0);
                runnable.setRelativeWaitingTime(normaliser);
                runnable.getScore();
                verify(engine,times(1)).getScore(0.5,1.0);

        }
}
