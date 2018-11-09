package systemj.common;


public class BaseInterface {
	private InterfaceManager im;
	
	public final void setInterfaceManager(InterfaceManager imm){ im = imm; }
	public final boolean pushToQueue(Object[] o){ return im.pushToQueue(o); }
	public final void runInterfaceManager(){ im.run(); }
	public final void forwardChannelData(Object[] o){ im.forwardChannelData(o); }
        public final InterfaceManager getInterfaceManager(){return im;}
}
