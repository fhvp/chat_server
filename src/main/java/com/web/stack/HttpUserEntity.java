package com.web.stack;

import java.util.HashMap;
import java.util.Map;

import platform.ClassFactory;

public class HttpUserEntity extends ClassFactory
{	
	public class HttpUserInfo
	{
		String				m_url;
		
		public HttpUserInfo( String url )
		{
			// TODO Auto-generated constructor stub
			this.m_url = url;
		}
	}

	private static final Map<String, HttpUserInfo> userMap = new HashMap<String, HttpUserInfo>();
	
	public void insert( String userId, String url )
	{
		HttpUserInfo userInfo = new HttpUserInfo(url);
		
		userMap.put(userId, userInfo);
	}
	
	public String findUserInfo(String userId)
	{
		for( String key : userMap.keySet() )
		{
			if( key == userId )
				return userMap.get(key).m_url;
		}
		
		return null;
	}
}
