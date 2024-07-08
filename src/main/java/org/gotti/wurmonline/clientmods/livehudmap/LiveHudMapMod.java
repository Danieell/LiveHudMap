package org.gotti.wurmonline.clientmods.livehudmap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wurmonline.client.renderer.gui.*;
import org.gotti.wurmonline.clientmods.livehudmap.renderer.MapRendererCave;
import org.gotti.wurmonline.clientmods.livehudmap.renderer.RenderType;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmClientMod;
import org.gotti.wurmunlimited.modsupport.console.ConsoleListener;
import org.gotti.wurmunlimited.modsupport.console.ModConsole;

import com.wurmonline.client.game.World;
import com.wurmonline.client.settings.SavePosManager;

public class LiveHudMapMod implements WurmClientMod, Initable, PreInitable, Configurable, ConsoleListener {

	private static Logger logger = Logger.getLogger(LiveHudMapMod.class.getName());
	
	private boolean hiResMap = false;
	private boolean showHiddenOre = false;
	private boolean mShowDeeds = true;
	
	public String mMapName = "Lib";
	
	private Object liveMap;
	private LiveMapWindow mLiveMapWindow;

	public static int serverSize = 0;
	
	public static String mJsonLibPath = "";
	public static String mJsonNovPath = "";
	public static String mJsonInfPath = "";
	public static String mCurrentJsonPath = "";

	@Override
	public void configure(Properties properties) {
		hiResMap = Boolean.valueOf(properties.getProperty("hiResMap", String.valueOf(hiResMap)));
		showHiddenOre = Boolean.valueOf(properties.getProperty("showHiddenOre", String.valueOf(showHiddenOre)));
		mShowDeeds = Boolean.valueOf( properties.getProperty( "showDeeds", String.valueOf( mShowDeeds ) ) );
		mMapName = properties.getProperty("defaultServer");
		mJsonLibPath = properties.getProperty("libertyPath");
		mJsonNovPath = properties.getProperty("novusPath");
		mJsonInfPath = properties.getProperty("infinityPath");
		
		logger.log(Level.INFO, "hiResMap: " + hiResMap);
		logger.log(Level.INFO, "showHiddenOre: " + showHiddenOre);
		logger.log( Level.INFO, "Default Server: " + mMapName );
		logger.log( Level.INFO, "Liberty Path: " + mJsonLibPath );
		logger.log( Level.INFO, "Novus Path: " + mJsonNovPath );
		logger.log( Level.INFO, "Infinity Path: " + mJsonInfPath );

		RenderType.highRes = hiResMap;
		MapRendererCave.showHiddenOre = showHiddenOre;
		
		
	}

	@Override
	public void preInit() {
	}

	@Override
	public void init() {
		// com.wurmonline.client.renderer.gui.HeadsUpDisplay.init(int, int)
		HookManager.getInstance().registerHook("com.wurmonline.client.renderer.gui.HeadsUpDisplay", "init", "(II)V",
				new InvocationHandlerFactory() {

					@Override
					public InvocationHandler createInvocationHandler() {
						return new InvocationHandler() {

							@Override
							public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
								method.invoke(proxy, args);

								initLiveMap((HeadsUpDisplay) proxy);

								return null;
							}
						};
					}
				});

		ModConsole.addConsoleListener(this);
	}
	
	private void initLiveMap(HeadsUpDisplay hud) {
		
		new Runnable() {
			
			@Override
			public void run() {
				try {
					World world = ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "world"));
		
					mLiveMapWindow = new LiveMapWindow( world, mMapName );
					liveMap = mLiveMapWindow;
					setDefaultServer();
					mLiveMapWindow.getDeedData().setJsonServer( mCurrentJsonPath );
					mLiveMapWindow.getDeedData().setShowDeeds( mShowDeeds );
					mLiveMapWindow.getDeedData().setLibertyPath( mJsonLibPath );;
					mLiveMapWindow.getDeedData().setNovusPath( mJsonNovPath );
					mLiveMapWindow.getDeedData().setInfinityPath( mJsonInfPath );
					mLiveMapWindow.getDeedData().refreshMap();
		
					MainMenu mainMenu = ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "mainMenu"));
					mainMenu.registerComponent("Live map", mLiveMapWindow);
		
					List<WurmComponent> components = ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "components"));
					components.add(mLiveMapWindow);
					
					SavePosManager savePosManager = ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "savePosManager"));
					savePosManager.registerAndRefresh(mLiveMapWindow, "livemapwindow");
				}
				catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
					throw new RuntimeException(e);
				}
			}
		}.run();
	}
	
	@Override
	public boolean handleInput(String string, Boolean silent) {
		if (string != null && string.startsWith("toggle livemap") && liveMap instanceof LiveMapWindow) {
			((LiveMapWindow)liveMap).toggle();
			return true;
		} else if (string != null && string.startsWith("serversize")) {
			final StringTokenizer tokens = new StringTokenizer(string);
			tokens.nextToken();
			if (tokens.hasMoreTokens()) {
				serverSize = Integer.parseInt(tokens.nextToken(), 10);
				((LiveMapWindow)liveMap).getHud().consoleOutput("Server size set to " + serverSize);
			} else {
				((LiveMapWindow)liveMap).getHud().consoleOutput("Server size required");
			}
			return true;
		} 
		return false;
	}
	
	private void setDefaultServer()
	{
		logger.log(Level.INFO, mMapName);
		if ( mMapName.contains( "Lib" ) )
		{
			mCurrentJsonPath = mJsonLibPath;
			mLiveMapWindow.setServerShortcut(Server.Liberty );
		}
		else if ( mMapName.contains( "Nov" ) )
		{
			mCurrentJsonPath = mJsonNovPath;
			mLiveMapWindow.setServerShortcut( Server.Novus );
		}
		else if ( mMapName.contains( "Inf" ) )
		{
			mCurrentJsonPath = mJsonInfPath;
			mLiveMapWindow.setServerShortcut( Server.Infinity );
		}
	}
}
