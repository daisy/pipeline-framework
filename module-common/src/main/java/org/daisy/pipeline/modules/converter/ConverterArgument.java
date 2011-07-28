package org.daisy.pipeline.modules.converter;

import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;



public class ConverterArgument {
	
		public enum OutputType {
			FILE("anyFileURI"),FOLDER("anyFolderURI");
			private final String name;
			OutputType(String name){
				this.name=name;
			}
			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return name.toString();
			}
			
		}

		public enum Direction {
			INPUT, OUTPUT
		}

		/**
		 * The Enum with the different types of converter arguments.
		 */
		public enum BindType {
			
			/** INPUT arg */
			PORT, 
			/** OPTION arg */
			OPTION, 
			/** PARAMETER arg */
			PARAMETER;

		};

		/** The  argument name */
		protected String mName;
		
		/** The argument type. */
		protected BindType mBindType;
		
				
		/** The  string to bind this arg . */
		protected String mBind;
		
		/** argument description */
		protected String mDesc;
		
		/** indicates if this argument is optional */
		protected boolean mOptional;

		protected Direction mDirection;
		//TODO:MIME type?
		protected String mMediaType;
		
		protected OutputType mOutputType;
		
		protected boolean mSequence=false;
		/**
		 * Instantiates a new converter argument.
		 */
		public ConverterArgument(){
			
		}
		
		/**
		 * Instantiates a new converter argument.
		 *
		 * @param name the name
		 * @param type the type
		 * @param bind the bind
		 * @param desc the desc
		 * @param optional the optional
		 */
		public ConverterArgument(String name, BindType type,
				String bind,Direction dir,String mediaType, String desc, boolean optional,OutputType outputType,boolean sequence) {
			super();
			mName = name;
			mBindType = type;
			mBind = bind;
			mDesc = desc;
			mOptional = optional;
			mDirection=dir;
			mMediaType=mediaType;
			mOutputType=outputType;
			mSequence=sequence;
		}

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName() {
			return mName;
		}

		/**
		 * Gets the type.
		 *
		 * @return the type
		 */
		public BindType getBindType() {
			return mBindType;
		}

				
		/**
		 * Gets the bind.
		 *
		 * @return the bind
		 */
		public String getBind() {
			return mBind;
		}

		/**
		 * Gets the desc.
		 *
		 * @return the desc
		 */
		public String getDesc() {
			return mDesc;
		}

		public Direction getDirection(){
			return mDirection;
		}
		/**
		 * Checks if is optional.
		 *
		 * @return true, if is optional
		 */
		public boolean isOptional() {
			return mOptional;
		}

		public String getMediaType(){
			return mMediaType;
		}
		

		
		private ConverterArgument copy(){
			ConverterArgument copy= new ConverterArgument();
			copy.mBind=this.mBind;
			copy.mBindType=this.mBindType;
			copy.mDesc=this.mDesc;
			copy.mDirection=this.mDirection;
			copy.mMediaType=this.mMediaType;
			copy.mName=this.mName;
			copy.mOptional=this.mOptional;
			copy.mOutputType=this.mOutputType;
			copy.mSequence=this.mSequence;
			return copy;
		}
		
		public  ValuedArgumentBuilder getValuedConverterBuilder(){
			return new ValuedArgumentBuilder(this);
		}
		public OutputType getOutputType() {
			return mOutputType;
		}

		
		public boolean isSequence() {
			return mSequence;
		}

		
		
		public class ValuedConverterArgument<K>{
			ConverterArgument mArgument;
			LinkedList<K> mValues= new LinkedList<K>();

			protected ValuedConverterArgument(ConverterArgument arg){
				mArgument=arg;
			}
			public List<K> getValues() {
				return mValues;
				
			}
			public void addValue(K value) {
				mValues.add(value);
			}
			
			public ConverterArgument getConverterArgument(){
				return mArgument;
			}
		}
		
		public class ValuedArgumentBuilder{
			ConverterArgument mConverterArgument;
			public ValuedArgumentBuilder(ConverterArgument arg) {
				mConverterArgument =arg;
			}
			
			public ValuedConverterArgument<Source> withSource(Source... sources){
				
				//check
				if(mConverterArgument.getDirection()!=Direction.INPUT){
					throw new IllegalArgumentException("sources are only for input ports");
				}
				
				ValuedConverterArgument<Source> valued=new ValuedConverterArgument<Source>(mConverterArgument);
				for(Source src:sources){
					valued.addValue(src);
				}
				return valued;
			}
			public ValuedConverterArgument<Result> withResult(Result... results){
				if(mConverterArgument.getDirection()!=Direction.OUTPUT){
					throw new IllegalArgumentException("results are only for result ports");
				}
				
				ValuedConverterArgument<Result> valued=new ValuedConverterArgument<Result>(mConverterArgument);
				for(Result res:results){
					valued.addValue(res);
				}
				return valued;
			}
			public ValuedConverterArgument<String> withString(String str){
				if(mConverterArgument.getBindType()!=BindType.OPTION){
					throw new IllegalArgumentException("strings are only for options");
				}
				
				ValuedConverterArgument<String> valued=new ValuedConverterArgument<String>(mConverterArgument);
				valued.addValue(str);
				return valued;
			}
		}
}
