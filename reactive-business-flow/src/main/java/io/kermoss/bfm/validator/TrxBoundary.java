package io.kermoss.bfm.validator;

import java.util.HashSet;
import java.util.Set;

public class TrxBoundary {
		
		private Set<TrxBoundary> children = new HashSet<>();
		private String type;
		private boolean validated;
		private String name;
		private String childOf;
		private Class<?> clazz;

		
        
		public void setType(String type) {
			this.type = type;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setChildOf(String childOf) {
			this.childOf = childOf;
		}

		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}

		public TrxBoundary(String type, boolean validated, String name, String childOf, Class<?> clazz) {
			super();
			this.type = type;
			this.validated = validated;
			this.name = name;
			this.childOf = childOf;
			this.clazz = clazz;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TrxBoundary other = (TrxBoundary) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		public Set<TrxBoundary> getChildren() {
			return children;
		}

		public void setChildren(Set<TrxBoundary> children) {
			this.children = children;
		}
        
		public void setValidated(boolean validated) {
			this.validated = validated;
		}
		public String getType() {
			return type;
		}

		public boolean isValidated() {
			return validated;
		}

		public String getName() {
			return name;
		}

		public String getChildOf() {
			return childOf;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		

		@Override
		public String toString() {
			return "TrxBoundary [children=" + children + ", type=" + type + ", validated=" + validated + ", name="
					+ name + ", childOf=" + childOf + "]";
		}
		

	}