package platform;

public interface PrimitiveFactory
{
	abstract class primStruct{}
	
	//initialize
	public void init();
	
	//@Override
	public Object encode( Object primitive );

	//@Override
	public Object decode(Object data);
}
