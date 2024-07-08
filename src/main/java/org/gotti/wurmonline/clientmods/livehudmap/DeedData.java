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
{	private static String mJsonLibPath = "";
	private static String mJsonNovPath = "";
	private static String mJsonInfPath = "";
	private static String mJsonPath = "";
	public static byte[][] mMap = new byte[4097][4097];
	public static boolean mShowDeeds;
	
	public final static Logger logger = Logger.getLogger(DeedData.class.getName());
	
	private static void parseMapData() 
    {
		JSONParser parser = new JSONParser();
		String lJsonString = ""; 
        try 
        {
        	URL lURL = new URL( mJsonPath );
		    URLConnection lConnection = lURL.openConnection();
		    BufferedReader lBufferedReader = new BufferedReader( new InputStreamReader( lConnection.getInputStream() ) );
	        String lInputString;
	        while ( ( lInputString = lBufferedReader.readLine() ) != null ) 
	        	lJsonString += lInputString;
	        lBufferedReader.close();
        
	        for (Iterable<String> iterable : Iterables.partition(Splitter.on(",").split( lJsonString.substring(19, ( lJsonString.length() - 2 ) ) ), 15)) 
	        { 
	            JSONObject lDeed = (JSONObject) parser.parse( Joiner.on(",").join(iterable) );

	            Long xStart = (Long) lDeed.get( "x" ) - (Long) lDeed.get( "tilesWest" );
	            Long xEnd = (Long) lDeed.get( "x" ) + (Long) lDeed.get( "tilesEast" );
	            Long yStart = (Long) lDeed.get( "y" ) - (Long) lDeed.get( "tilesNorth" );
	            Long yEnd = (Long) lDeed.get( "y" ) + (Long) lDeed.get( "tilesSouth" );
	            
	            Long xPerimeterStart = xStart - (Long) lDeed.get( "tilesPerimeter" );
	            Long xPerimeterEnd = xEnd + (Long) lDeed.get( "tilesPerimeter" );
	            Long yPerimeterStart = yStart - (Long) lDeed.get( "tilesPerimeter" );
	            Long yPerimeterEnd = yEnd + (Long) lDeed.get( "tilesPerimeter" );
	            
	            
	            for ( int i = xPerimeterStart.intValue(); i <= xPerimeterEnd; i ++)
	            {
	            	if ( ( i >= 0 ) && ( i <= 4096 ) )
	            	{
		            	for ( int j = yPerimeterStart.intValue(); j <= yPerimeterEnd; j ++)
			            {
		            		if ( ( j >= 0 ) && ( j <= 4096 ) )
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
	        }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	
	public void refreshMap()
	{
		logger.log( Level.INFO, "Deed data refreshing..." );
		logger.log( Level.INFO, "JsonPath: " + mJsonPath );
		resetMapData();
		if ( mJsonPath.length() > 10 )
		{
			parseMapData();
		}
		logger.log( Level.INFO, "Deed data for Livemap refreshed" );
	}
	
	public byte[][] getMapData()
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
		logger.log( Level.INFO, "Deed data for Livemap reseted" );
	}

	public void setLibertyPath( String pPath )
	{
		mJsonLibPath = pPath;
	}
	
	public String getLibertyPath()
	{
		return mJsonLibPath;
	}
	
	public void setNovusPath( String pPath )
	{
		mJsonNovPath = pPath;
	}
	
	public String getNovusPath()
	{
		return mJsonNovPath;
	}

	public void setInfinityPath( String pPath )
	{
		mJsonInfPath = pPath;
	}

	public String getInfinityPath()
	{
		return mJsonInfPath;
	}
	
	public void setJsonServer( String pServer )
	{
		mJsonPath = pServer;
	}
	
	public String getJsonServer()
	{
		return mJsonPath;
	}
	
	public void setShowDeeds( boolean pBoolean )
	{
		mShowDeeds = pBoolean;
	}
	
	public boolean getShowDeed()
	{
		return mShowDeeds;
	}
}
