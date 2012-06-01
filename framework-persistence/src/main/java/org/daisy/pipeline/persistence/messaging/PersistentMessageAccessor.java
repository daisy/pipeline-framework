package org.daisy.pipeline.persistence.messaging;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.Message.Level;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.pipeline.job.JobId;

public class PersistentMessageAccessor extends MessageAccessor {

	JobId jobId;
	private EntityManagerFactory emf;

	public PersistentMessageAccessor(JobId jobId,EntityManagerFactory emf) {
		super();
		this.jobId = jobId;
		this.emf = emf;
	}

	@Override
	public List<Message> getAll() {
		return getMessages(jobId, 0,
				Arrays.asList(Level.values()));
	}

	@Override
	protected List<Message> getMessagesFrom(Level level) {
		List<Level> levels = new LinkedList<Level>();

		for (Level iter : Level.values()) {
			if (iter.compareTo(level) == 0) {
				levels.add(iter);
			} else {
				break;
			}
		}

		return getMessages(jobId, 0,
				levels);
	}

	@Override
	public MessageFilter createFilter() {
		return new PersistentMessageAccessor.MessageFilter();
	}

	protected class MessageFilter implements MessageAccessor.MessageFilter {
		List<Level> mLevels = Arrays.asList(Level.values());
		int mSeq;

		@Override
		public MessageFilter filterLevels(final Set<Level> levels) {
			mLevels = new LinkedList<Level>(levels);

			return this;
		}

		@Override
		public MessageFilter fromSquence(int from) {
			mSeq = from;
			return this;
		}

		@Override
		public List<Message> getMessages() {

			return PersistentMessageAccessor.this.getMessages(jobId, mSeq,
					mLevels);

		}

	}
	
	private List<Message> getMessages(JobId id,int from,List<Level> levels){
		EntityManager em = emf.createEntityManager();
		/*
		StringBuilder sqlBuilder=new StringBuilder("select m from PersistentMessage m where m.jobId='%s' and  m.sequence > %s and m.level in ( ");
		
		for (int i=0;i<levels.size();i++){
			sqlBuilder.append(" ?"+(i+1) );
			if(i!=levels.size()-1)
				sqlBuilder.append(", ");
		}
		sqlBuilder.append(") order by m.sequence ");
		String sql=String.format(sqlBuilder.toString(), id.toString(),from);
		*/
		StringBuilder sqlBuilder=new StringBuilder("select m from PersistentMessage m where m.jobId='%s' and  m.sequence > %s");
		String sql=String.format(sqlBuilder.toString(), id.toString(),from);
		Query q=em.createQuery(sql);
		/*
		int i=1;
		for (Level l:levels){
			q.setParameter(i++, l);
		}
		*/
		@SuppressWarnings("unchecked") //just how persistence works
		List<Message> result = q.getResultList();
		em.close();
		return result;
	}
}
