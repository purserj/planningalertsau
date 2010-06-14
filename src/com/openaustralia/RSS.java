package com.openaustralia;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.util.Log;

public class RSS extends DefaultHandler {
	 
    public static List parseFeed(String url) {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp;
            RSS handler = new RSS();
            try {
                    sp = spf.newSAXParser();
                    XMLReader xr = sp.getXMLReader();
                    xr.setContentHandler(handler);
                    xr.setErrorHandler(handler);
                   
                    URLConnection c = (new URL(url)).openConnection();
                    //c.setRequestProperty("User-Agent", "Android/m3-rc37a");
                    xr.parse(new InputSource(c.getInputStream()));
                   
            } catch (SAXParseException e) {
                    e.printStackTrace();
            } catch (ParserConfigurationException e) {
                    e.printStackTrace();
            } catch (SAXException e) {
                    e.printStackTrace();
            } catch (MalformedURLException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }
           
            return handler.result;
    }

    private List result;
    private ContentValues postBuffer;

    /* Efficiency is the name of the game here... */
    private int mState;

    private static final int STATE_IN_ITEM = (1 << 2);
    private static final int STATE_IN_ITEM_TITLE = (1 << 3);
    private static final int STATE_IN_ITEM_LINK = (1 << 4);
    private static final int STATE_IN_ITEM_DESC = (1 << 5);
    private static final int STATE_IN_ITEM_DATE = (1 << 6);
    private static final int STATE_IN_ITEM_AUTHOR = (1 << 7);
   // private static final int STATE_IN_TITLE = (1 << <img src="http://www.anddev.org/images/smilies/cool.png" alt="8)" title="Cool" />);
    private static final int STATE_IN_ITEM_POINT = (1 << 9);

    private static HashMap<String, Integer> mStateMap;

    static
    {
            mStateMap = new HashMap<String, Integer>();
            mStateMap.put("item", new Integer(STATE_IN_ITEM));
            mStateMap.put("entry", new Integer(STATE_IN_ITEM));
            mStateMap.put("title", new Integer(STATE_IN_ITEM_TITLE));
            mStateMap.put("link", new Integer(STATE_IN_ITEM_LINK));
            mStateMap.put("description", new Integer(STATE_IN_ITEM_DESC));
            mStateMap.put("content", new Integer(STATE_IN_ITEM_DESC));
            mStateMap.put("content:encoded", new Integer(STATE_IN_ITEM_DESC));
            mStateMap.put("dc:date", new Integer(STATE_IN_ITEM_DATE));
            mStateMap.put("updated", new Integer(STATE_IN_ITEM_DATE));
            mStateMap.put("pubDate", new Integer(STATE_IN_ITEM_DATE));
            mStateMap.put("dc:author", new Integer(STATE_IN_ITEM_AUTHOR));
            mStateMap.put("author", new Integer(STATE_IN_ITEM_AUTHOR));
            mStateMap.put("georss:point", new Integer(STATE_IN_ITEM_POINT));
   
    }

   
    public RSS() {
            result = new ArrayList<Map>();
    }
   
    public void startElement(String uri, String name, String qName,
                    Attributes attrs)
    {

            Integer state = mStateMap.get(qName);

            if (state != null)
            {
                    mState |= state.intValue();

                    if (mState == STATE_IN_ITEM)
                            postBuffer = new ContentValues();
                    else if ((mState & STATE_IN_ITEM) != 0 && state.intValue() == STATE_IN_ITEM_LINK)
                    {
                            String href = attrs.getValue("href");

                            if (href != null)
                                    postBuffer.put("link", href);
                    }
            }
    }

    public void endElement(String uri, String name, String qName)
    {
            Integer state = mStateMap.get(qName);

            if (state != null)
            {
                    mState &= ~(state.intValue());

                    if (state.intValue() == STATE_IN_ITEM)
                    {
                            result.add(postBuffer);
                    }
            }
    }

    public void characters(char ch[], int start, int length)
    {

            if ((mState & STATE_IN_ITEM) == 0)
                    return;

            /*
             * We sort of pretended that mState was inclusive, but really only
             * STATE_IN_ITEM is inclusive here.  This is a goofy design, but it is
             * done to make this code a bit simpler and more efficient.
             */
            switch (mState)
            {
            case STATE_IN_ITEM | STATE_IN_ITEM_TITLE:
                    postBuffer.put("title", new String(ch, start, length));
                    break;
            case STATE_IN_ITEM | STATE_IN_ITEM_DESC:
                    postBuffer.put("description", new String(ch, start, length));
                    break;
            case STATE_IN_ITEM | STATE_IN_ITEM_LINK:
                    postBuffer.put("link", new String(ch, start, length));
                    break;
            case STATE_IN_ITEM | STATE_IN_ITEM_DATE:
                    postBuffer.put("date", new String(ch, start, length));
                    break;
            case STATE_IN_ITEM | STATE_IN_ITEM_AUTHOR:
                    postBuffer.put("author", new String(ch, start, length));
                    break;
            case STATE_IN_ITEM | STATE_IN_ITEM_POINT:
                    String[] latlng = (new String(ch, start, length)).split(" ");
                    postBuffer.put("lat", (int)(Double.valueOf(latlng[0])*1000000));
                    postBuffer.put("lng", (int)(Double.valueOf(latlng[1])*1000000));
                    break;
            default:
                    /* Don't care... */
            }
    }
}
