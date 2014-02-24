package org.daisy.common.priority;

public enum Priority{
                LOW,
                MEDIUM,
                HIGH;
                //for efficiency 
                private static final int size = Priority.values().length;
                public double asDouble(){
                        return this.ordinal()/size;
                }
}
