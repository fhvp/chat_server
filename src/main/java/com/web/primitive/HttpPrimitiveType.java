package com.web.primitive;

public enum HttpPrimitiveType
{
	PRIM_TYPE_SINGLE_ATTACH_REQUEST,
	PRIM_TYPE_SINGLE_ATTACH_RESPONSE,
	PRIM_TYPE_CONNECTION_REQUEST,
	PRIM_TYPE_CONNECTION_RESPONSE,
	PRIM_TYPE_DATA_REQUEST,
	PRIM_TYPE_DATA_RESPONSE,
	PRIM_TYPE_DATA_COMPLETE,
	PRIM_TYPE_MULTI_ATTACH_REQUEST,
	PRIM_TYPE_MULTI_ATTACH_RESPONSE;
	
	public static HttpPrimitiveType get( Object primType )
	{
		String type = primType.toString();
		if( type != null )
			return HttpPrimitiveType.valueOf(type);
			
		return null;
	}
}
