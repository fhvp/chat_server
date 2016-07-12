package com.web.primitive;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import platform.PrimitiveFactory;

public class HttpPrimitive implements PrimitiveFactory
{	
	public class HttpHeader
	{
		public Object				m_primType;
		public String				m_groupId;
		public String				m_userId;
		public String				m_type;
		public Object				m_errorType;
	}
	
	public class HttpPrimitiveStruct extends primStruct
	{
		public HttpHeader			m_header;
		public Object				m_object;
	}
	
	public HttpPrimitiveStruct primitive;
	
	//public/////////////////////////////////////////////

	public static String JAVA_TEXT = "jtext";
	public static String JAVA_IMAGE = "jimg";
	public static String JAVA_FILE = "jfile";
	public static String JAVA_URL = "jurl";
	public static String JAVA_ERROR = "jerror";
	
	//private////////////////////////////////////////////
	private String HTTP_HEADER = "header";
	private String HTTP_PRIM_TYPE = "primType";
	private String HTTP_GROUP_ID = "groupId";
	private String HTTP_USER_ID = "userId";
	private String HTTP_TYPE = "type";
	private String HTTP_ERROR_TYPE = "errorType";

	private String HTTP_OBJECT = "object";
	
	private String DEFAULT_ENCODE_TYPE = "";
	/////////////////////////////////////////////////////
	
	public HttpPrimitive()
	{
		init();
	}
	
	@Override
	public void init()
	{
		DEFAULT_ENCODE_TYPE = "UTF-8";
		this.primitive = new HttpPrimitiveStruct();
		this.primitive.m_header = new HttpHeader();
	}
	
	//module/////////////////////////////////////////////
	private String urlDecode(String requestMsg)
	{
		try
		{
			return URLDecoder.decode(requestMsg, DEFAULT_ENCODE_TYPE);
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private String encodeHttp(Map<String, Object> primitive) throws UnsupportedEncodingException
	{		
		/// primitive가 비어있으면 return null
		if( primitive == null )
			return null;
		
		StringBuilder sBuilder = new StringBuilder();
		
		/// 키, 값을 빼서 String 으로 만들어 준다
		for(Iterator<String> i = primitive.keySet().iterator(); i.hasNext();)
		{
			String key = (String)i.next();
			
			sBuilder.append( URLEncoder.encode( key, DEFAULT_ENCODE_TYPE ) );
			sBuilder.append( "=" );
			sBuilder.append( URLEncoder.encode( String.valueOf( primitive.get( key ) ), DEFAULT_ENCODE_TYPE ) );
			
			if( i.hasNext() )
				sBuilder.append( "&" );
		}
		
		/// 문자열 반환
		return sBuilder.toString();
	}
	
	//Decode Item
	private String decodeItem(String startArgument, String endArgument, String data)
	{
		int startIndex = data.indexOf(startArgument+"=") + startArgument.length() + 1;

		if( endArgument != "" )
		{
			String dataBuf = data.substring(startIndex);
			int endIndex = dataBuf.indexOf(endArgument);
			
			return data.substring(startIndex, startIndex + endIndex);
		}
		
		return data.substring(startIndex);
	}

	//Encode Header
	private Map<String, Object>encodeHttpHeader( HttpPrimitiveStruct httpPrim )
	{
		Map<String, Object> header = new HashMap<String, Object>();

		header.put( HTTP_PRIM_TYPE, httpPrim.m_header.m_primType );
		header.put( HTTP_GROUP_ID, httpPrim.m_header.m_groupId );
		header.put( HTTP_USER_ID, httpPrim.m_header.m_userId );
		header.put( HTTP_TYPE, httpPrim.m_header.m_type );
		header.put( HTTP_ERROR_TYPE, httpPrim.m_header.m_errorType );
		
		return header;
	}

	//Encode
	private String encodeHttpPrimitive( HttpPrimitiveStruct httpPrim )
	{
		Map<String, Object> primitive = new HashMap<String, Object>();

		try
		{
			//header
			primitive.put("header", encodeHttp(encodeHttpHeader(httpPrim)));
			
			//data
			primitive.put(HTTP_OBJECT, httpPrim.m_object);
			
			//encode String
			return encodeHttp(primitive);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	
	//Decode Header/////////////////////////////////////
	private Map<String, Object> decodeHttpHeader( String data )
	{
		Map<String, Object> header = new HashMap<String, Object>();
		String value = "";
		
		value = decodeItem( HTTP_PRIM_TYPE, "&", data );
		header.put( HTTP_PRIM_TYPE, value );
		
		value = decodeItem( HTTP_GROUP_ID, "&", data );
		header.put( HTTP_GROUP_ID, value );

		value = decodeItem( HTTP_USER_ID, "&", data );
		header.put( HTTP_USER_ID, value );

		value = decodeItem( HTTP_TYPE, "&", data );
		header.put( HTTP_TYPE, value );

		value = decodeItem( HTTP_ERROR_TYPE, "&", data );
		header.put( HTTP_ERROR_TYPE, value );

		return header;
	}

	//Decode Map
	private Map<String, Object> decodeHttpPrimitive(String requestMsg)
	{
		Map<String, Object> primitive = new HashMap<String, Object>();

		String value = "";
		String remainData = "";
	
		remainData = urlDecode(requestMsg);
		
		//decode header
		primitive.put( HTTP_HEADER, decodeHttpHeader( remainData ));

		//object
		value = decodeItem( HTTP_OBJECT, "", remainData );
		primitive.put(HTTP_OBJECT, value);
		
		/// 문자열 반환
		return primitive;
	}

	private HttpPrimitiveStruct decodeHttp(Map<String, Object> map)
	{		
		/// primitive가 비어있으면 return null
		if( map == null )
			return null;
		
		HttpPrimitiveStruct httpPrimitive = new HttpPrimitiveStruct();

		@SuppressWarnings("unchecked")
		Map<String, Object> header = (Map<String, Object>) map.get(HTTP_HEADER);
		httpPrimitive.m_header = new HttpHeader();
		
		//Header
		httpPrimitive.m_header.m_primType = header.get( HTTP_PRIM_TYPE );
		httpPrimitive.m_header.m_groupId = String.valueOf( header.get( HTTP_GROUP_ID ));
		httpPrimitive.m_header.m_userId = String.valueOf( header.get( HTTP_USER_ID ));
		httpPrimitive.m_header.m_type = String.valueOf( header.get( HTTP_TYPE ));
		httpPrimitive.m_header.m_errorType = header.get( HTTP_ERROR_TYPE );
		
		//Object
		httpPrimitive.m_object = String.valueOf( map.get( HTTP_OBJECT ));

		/// 문자열 반환
		return httpPrimitive;
	}

	
	@Override
	public Object encode(Object primitive) {
		// TODO Auto-generated method stub
		
		if( primitive == null )
			return null;
		
		return this.encodeHttpPrimitive((HttpPrimitiveStruct)primitive);
	}

	@Override
	public HttpPrimitiveStruct decode(Object data) {
		// TODO Auto-generated method stub
		
		if(data == null)
			return null;

		Map<String, Object> map = this.decodeHttpPrimitive(data.toString());
		
		return decodeHttp(map);
	}
}
