package com.web.stack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import platform.ClassFactory;

public class HttpGroupEntity extends ClassFactory
{	
	public class HttpGroupInfo
	{
		String				m_userId[];
		
		public HttpGroupInfo( String userInfo[] )
		{
			// TODO Auto-generated constructor stub
			for( int index = 0; index < userInfo.length; index++ )
			{
				this.m_userId[index] = userInfo[index];
			}
		}
	}
	
	private static final Map<String, HttpGroupInfo> groupMap = new HashMap<String, HttpGroupInfo>();
	
	private String makeGroupId()
	{
		long time = System.currentTimeMillis();
		SimpleDateFormat date = new SimpleDateFormat("yyyymmddhhmmss");
		String result = date.format(new Date(time));
		
		return result;
	}
	
	public void insert(String userId[])
	{
		HttpGroupInfo groupInfo = new HttpGroupInfo(userId);
		
		groupMap.put(makeGroupId(), groupInfo);
	}
	
	public void delete(String groupId)
	{
		for( String key : groupMap.keySet() )
		{
			if( key == groupId )
				groupMap.remove(key);
		}
	}
	
	public String[] findGroupInfo(String groupId)
	{
		for( String key : groupMap.keySet() )
		{
			if( key == groupId )
				return groupMap.get(key).m_userId;
		}
		
		return null;
	}
}
