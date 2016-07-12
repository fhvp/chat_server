package com.web.chat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.stack.HttpManager;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController
{
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private static final HttpManager manager = new HttpManager();
	
	@RequestMapping(value = "/single.cht", method=RequestMethod.POST)
	public @ResponseBody String chat_request(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		DataInputStream inStream = new DataInputStream(request.getInputStream());
		
		InputStreamReader reader = new InputStreamReader(inStream);
		BufferedReader breader = new BufferedReader(reader);
		String requestMsg = "";
		String str;
		
		while ((str = breader.readLine()) != null)
			requestMsg += str;

		//Primitive Parser
		String responseData = manager.recvHttpSock(requestMsg);
		
		//Response Parameter Set
		response.setCharacterEncoding(request.getCharacterEncoding());

		return responseData;
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		String formattedDay = "2016-06-10";
		
		model.addAttribute("serverDay", formattedDay);
		
		return "index";
	}
	
	public Map<String, Object> decodeHttp(String httpData, String requestCharSet) throws UnsupportedEncodingException
	{
		Map<String, Object> primitive = new HashMap<String, Object>();
		
		String data = URLDecoder.decode(httpData, requestCharSet);
		String key = "", value = "";
		String remainData = data;
		int index = 0, totalIndex = 0;
		
		while( totalIndex < data.length() )
		{
			key = remainData.substring(index, remainData.indexOf("="));
			index = remainData.indexOf("=") + 1;
			
			if( remainData.indexOf("&") == -1 )
			{
				value = remainData.substring(index); //last argument
				primitive.put(key, value);
				break;
			}
			else
				value = remainData.substring(index, index = remainData.indexOf("&"));
			
			primitive.put(key, value);
			
			remainData = remainData.substring(++index);
			totalIndex += index;
			index = 0;
		}
		
		
		/// 문자열 반환
		return primitive;
	}
	
	public String decodeItem(String startArgument, String endArgument, String data)
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
	
	private String GROUP_ID = "groupId";
	private String HEADER_FIELD1 = "dummy1";
	private String HEADER_FIELD2 = "dummy2";
	private String HEADER_FIELD3 = "dummy3";
	
	private Map<String, Object> decodeHttpHeader( String data )
	{
		Map<String, Object> header = new HashMap<String, Object>();
		String value = "";
		
		value = decodeItem( GROUP_ID, "&", data );
		header.put(GROUP_ID, value);
		
		value = decodeItem( HEADER_FIELD1, "&", data );
		header.put(HEADER_FIELD1, value);

		value = decodeItem( HEADER_FIELD2, "&", data );
		header.put(HEADER_FIELD2, value);

		value = decodeItem( HEADER_FIELD3, "&", data );
		header.put(HEADER_FIELD3, value);
		
		return header;
	}
	
	public Map<String, Object> decodeHttpChatPrimitive(String httpData, String requestCharSet) throws UnsupportedEncodingException
	{
		Map<String, Object> primitive = new HashMap<String, Object>();
		
		String data = URLDecoder.decode(httpData, requestCharSet);
		String value = "";
		String remainData = data;
		
		//decode header
		primitive.put( "header", decodeHttpHeader( remainData ));
		
		//id
		value = decodeItem( "id", "&", remainData );
		primitive.put("id", value);
		
		//type
		value = decodeItem( "type", "&", remainData );
		primitive.put("type", value);
		
		//value
		value = decodeItem( "value", "", remainData );
		primitive.put("value", value);
		
		/// 문자열 반환
		return primitive;
	}
}
