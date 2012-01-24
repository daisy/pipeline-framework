package org.daisy.common.messaging;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.daisy.common.base.Filter;
import org.daisy.common.messaging.Message.Level;


/**
 * Gives access to the stored messages by level.
 */
public interface MessageAccessor{

	/**
	 * Gets the errors.
	 *
	 * @return the error messages
	 */
	public List<Message> getErrors();

	/**
	 * Gets the warnings.
	 *
	 * @return the warning messages
	 */
	public List<Message> getWarnings();

	/**
	 * Gets the infos.
	 *
	 * @return the info messages
	 */
	public List<Message> getInfos();

	/**
	 * Gets the debugs.
	 *
	 * @return the debug messages
	 */
	public List<Message> getDebugs();

	/**
	 * Gets the traces.
	 *
	 * @return the trace messages
	 */
	public List<Message> getTraces();

	/**
	 * Gets the messgages from a set of levels
	 *
	 * @param fromLevels levels
	 * @return the messages
	 */
	public List<Message> getMessages(Level... fromLevels);

	public List<Message> getAll();

	public List<Message> filtered(Filter<List<Message>>... filters);
	public static class SequenceFilter implements Filter<List<Message>> {
		int mFrom;

		public SequenceFilter(int from){
			mFrom=from;

		}
		@Override
		public List<Message> filter(List<Message> in) {
			return in.subList(mFrom,in.size());
		}

	}
	public static class LevelFilter implements Filter<List<Message>> {
		Set<Level> mLevels;

		public LevelFilter(Set<Level> levels ){
			mLevels=levels;
		}
		@Override
		public List<Message> filter(List<Message> in) {
			List<Message> filtered= new LinkedList<Message>();
			for(Message msg:in){
				if (mLevels.contains(msg.getLevel())){
					filtered.add(msg);
				}
			}
			return filtered;
		}

	}
}
