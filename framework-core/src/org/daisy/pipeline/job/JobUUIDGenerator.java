package org.daisy.pipeline.job;

import java.util.UUID;

public class JobUUIDGenerator implements JobIdGenerator {
	@Override
	public JobId generateId() {
		return new JobUUID();
	}

	@Override
	public JobId generateIdFromString(String base) {
		return new JobUUID(base);
	}
	private static class JobUUID implements JobId {

		private UUID mId;

		private JobUUID() {
			mId = UUID.randomUUID();
		}

		private JobUUID(String base) {
			mId = UUID.fromString(base);
		}
		@Override
		public int compareTo(JobId other) {
			if (other instanceof JobUUID) {
				return ((JobUUID) other).mId.compareTo(mId);
			} else {
				return -1;
			}
		}

		@Override
		public int hashCode() {
			return mId.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			JobUUID other = (JobUUID) obj;
			if (mId == null) {
				if (other.mId != null)
					return false;
			} else if (!mId.equals(other.mId))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return mId.toString();
		}

	}



}
