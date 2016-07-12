package com.web.stack;

import platform.ClassFactory;

import javax.lang.model.type.PrimitiveType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.web.chat.HomeController;
import com.web.primitive.HttpErrorType;
import com.web.primitive.HttpPrimitive;
import com.web.primitive.HttpPrimitive.HttpPrimitiveStruct;
import com.web.primitive.HttpPrimitiveType;

public class HttpManager extends ClassFactory
{
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private static final HttpPrimitive httpPrimitive = new HttpPrimitive();
	private static final HttpGroupEntity httpEntity = new HttpGroupEntity();
	
	public String recvHttpSock( String recvData )
	{
		if( recvData == null )
			return makeErrorPrim(ErrorType.NULL_DATA);
		
		//Decode
		HttpPrimitiveStruct primitive = httpPrimitive.decode(recvData);
		
		logger.info("Recv groupId: " + primitive.m_header.m_groupId + ", Id: " + primitive.m_header.m_userId + ", Data:" + primitive.m_object );
		
		HttpPrimitiveType primType = HttpPrimitiveType.get(primitive.m_header.m_primType);
		
		switch( primType )
		{
		case PRIM_TYPE_SINGLE_ATTACH_REQUEST:
			
			makeConnectionRequest();
			break;
		case PRIM_TYPE_MULTI_ATTACH_REQUEST:
			break;
		case PRIM_TYPE_CONNECTION_RESPONSE:
			
			makeSingleChatAttachRespons();
			break;
		case PRIM_TYPE_DATA_REQUEST:
			String[] groupInfo = httpEntity.findGroupInfo(primitive.m_header.m_groupId);
			
			if( groupInfo == null )
				return makeErrorPrim(ErrorType.GROUP_ID_ERROR);
			
			for( int index = 0; index < groupInfo.length; index++ )
			{
				String userId = groupInfo[index];
				
				//보낸 유저한테는 또 보낼 필요 없음
				if(userId == primitive.m_header.m_userId)
					continue;

				String sendData = makeDataRequest(primitive);
				if( sendData == null )
					return makeErrorPrim(ErrorType.RESPONSE_ERROR);
				
				//Send Another
			}
			
			return makeDataComplete(primitive);
		default:
			logger.info("no Primitive Type!! " + primType.toString());
			break;
		}
		
		return null;
	}
	
	private String makeConnectionRequest()
	{
		return null;
	}
	
	private String makeSingleChatAttachRespons()
	{
		return null;
	}
	
	private String makeDataRequest(HttpPrimitiveStruct recvPrimitive)
	{
		HttpPrimitive httpPrim = new HttpPrimitive();
		HttpPrimitiveStruct primitive = httpPrim.primitive;
		
		primitive.m_header.m_errorType = HttpErrorType.SUCCESS;
		primitive.m_header.m_groupId = recvPrimitive.m_header.m_groupId;
		primitive.m_header.m_userId = recvPrimitive.m_header.m_userId;
		primitive.m_header.m_primType = HttpPrimitiveType.PRIM_TYPE_DATA_REQUEST;
		primitive.m_header.m_type = recvPrimitive.m_header.m_type;
		
		primitive.m_object = recvPrimitive.m_object;
		
		return httpPrim.encode(primitive).toString();
	}
	
	private String makeDataComplete(HttpPrimitiveStruct recvPrimitive)
	{
		HttpPrimitive httpPrim = new HttpPrimitive();
		HttpPrimitiveStruct primitive = httpPrim.primitive;
		
		primitive.m_header.m_errorType = HttpErrorType.SUCCESS;
		primitive.m_header.m_groupId = recvPrimitive.m_header.m_groupId;
		primitive.m_header.m_userId = recvPrimitive.m_header.m_userId;
		primitive.m_header.m_primType = HttpPrimitiveType.PRIM_TYPE_DATA_COMPLETE;
		primitive.m_header.m_type = recvPrimitive.m_header.m_type;
		
		primitive.m_object = recvPrimitive.m_object;
		
		return httpPrim.encode(primitive).toString();
	}
	
	public String makeErrorPrim(ErrorType error)
	{
		HttpPrimitive httpPrim = new HttpPrimitive();
		HttpPrimitiveStruct primitive = httpPrim.primitive;
		
		primitive.m_header.m_errorType = error;
		primitive.m_header.m_primType = HttpPrimitiveType.PRIM_TYPE_DATA_REQUEST;
		primitive.m_header.m_type = HttpPrimitive.JAVA_ERROR;
		
		return httpPrim.encode(primitive).toString();
	}
}
