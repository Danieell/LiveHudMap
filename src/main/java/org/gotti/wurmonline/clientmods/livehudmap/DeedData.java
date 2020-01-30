package org.gotti.wurmonline.clientmods.livehudmap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class DeedData 
{
	private static String mJsonPath = "";
	private static String mJsonString = "";
	public static int[][] mMap = new int[4097][4097];
	public static boolean mShowDeeds;
	
	final Logger logger = Logger.getLogger(DeedData.class.getName());
	
	private static void parseMapData() 
    {
		JSONParser parser = new JSONParser();
		 
        try 
        {
        	URL lURL = new URL( mJsonPath );
		    URLConnection lConnection = lURL.openConnection();
		    BufferedReader lBufferedReader = new BufferedReader( new InputStreamReader( lConnection.getInputStream() ) );
	        String lInputString;
	        while ( ( lInputString = lBufferedReader.readLine() ) != null ) 
	            mJsonString += lInputString;
	        lBufferedReader.close();
        
	        for (Iterable<String> iterable : Iterables.partition(Splitter.on(",").split( mJsonString.substring(19, ( mJsonString.length() - 2 ) ) ), 15)) 
	        { 
	            JSONObject lDeed = (JSONObject) parser.parse( Joiner.on(",").join(iterable) );

	            Long xStart = (Long) lDeed.get("x") - (Long) lDeed.get("tilesWest");
	            Long xEnd = (Long) lDeed.get("x") + (Long) lDeed.get("tilesEast");
	            Long yStart = (Long) lDeed.get("y") - (Long) lDeed.get("tilesNorth");
	            Long yEnd = (Long) lDeed.get("y") + (Long) lDeed.get("tilesSouth");
	            
	            Long xPerimeterStart = xStart - (Long) lDeed.get("tilesPerimeter");
	            Long xPerimeterEnd = xEnd + (Long) lDeed.get("tilesPerimeter");
	            Long yPerimeterStart = yStart - (Long) lDeed.get("tilesPerimeter");
	            Long yPerimeterEnd = yEnd + (Long) lDeed.get("tilesPerimeter");
	            
	            
	            for ( int i = xPerimeterStart.intValue(); i <= xPerimeterEnd; i ++)
	            {
	            	for ( int j = yPerimeterStart.intValue(); j <= yPerimeterEnd; j ++)
		            {
	            		if ( ( i > xStart ) && ( i <= xEnd ) && ( j > yStart  ) && ( j <= yEnd ) )
	            		{
	            			mMap[i][j] = 1;	            			
	            		}
	            		else
	            		{
	            			mMap[i][j] = 2;	
	            		}
		            }
	            }
	        }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	
	public void refreshMap( String pPath)
	{
		logger.log(Level.INFO, "Deed data refreshing...");
		mJsonPath = pPath;
		resetMapData();
		if ( mJsonPath.length() > 10 )
		{
			parseMapData();
		}
		logger.log(Level.INFO, "Deed data for Livemap refreshed");
	}
		
	public void setDeedPath( String pPath )
	{
		mJsonPath = pPath;
	}
	
	public String getDeedPath()
	{
		return mJsonPath;
	}
	
	public int[][] getMapData()
	{
		return mMap;
	}
	
	private void resetMapData()
	{
		for ( int i = 1; i <= 4096; i ++ )
		{
			for ( int j = 1; j <= 4096; j ++ )
			{
				mMap[i][j] = 0;
			}
		}
		logger.log(Level.INFO, "Deed data for Livemap reseted");
	}
}
