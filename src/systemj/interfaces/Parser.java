package systemj.interfaces;

import java.util.Vector;

public interface Parser
{
	public abstract void setTarget(Object target, Object context);
	public abstract void parse() throws Exception;
	public abstract Vector getData(); // No generics for compatibility
}
