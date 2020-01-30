package com.wurmonline.client.renderer.gui;

import java.util.Locale;
import org.gotti.wurmonline.clientmods.livehudmap.DeedData;
import org.gotti.wurmonline.clientmods.livehudmap.LiveMap;
import org.gotti.wurmonline.clientmods.livehudmap.MapLayer;
import org.gotti.wurmonline.clientmods.livehudmap.renderer.RenderType;

import com.wurmonline.client.game.World;
import com.wurmonline.client.options.Options;
import com.wurmonline.client.renderer.PickData;
import com.wurmonline.client.resources.textures.ResourceTexture;
import com.wurmonline.client.resources.textures.ResourceTextureLoader;

public class LiveMapWindow extends WWindow {

	private WurmBorderPanel mainPanel;
	private LiveMap liveMap;
	private LiveMapView liveMapView;
	private static String mServerShortcut = "";
	
	private DeedData mDeedData = new DeedData();

	public LiveMapWindow(World world, String pServer ) {
		super("Live map Sklo", true);
		setTitle("Live map Sklo");
		mServerShortcut = pServer;
		mainPanel = new WurmBorderPanel("Live map Sklo");

		this.liveMap = new LiveMap(world, 256);
		resizable = false;

		WurmArrayPanel<WButton> buttons = new WurmArrayPanel<WButton>("Live map buttons", WurmArrayPanel.DIR_VERTICAL);
		buttons.setInitialSize(32, 256, false);
		buttons.addComponent(createButton("+", "Zoom in" , 0, new ButtonListener() {

			@Override
			public void buttonPressed(WButton p0) {
			}

			@Override
			public void buttonClicked(WButton p0) {
				liveMap.zoomIn();
			}
		}));

		buttons.addComponent(createButton("-", "Zoom out" , 1, new ButtonListener() {

			@Override
			public void buttonPressed(WButton p0) {
			}

			@Override
			public void buttonClicked(WButton p0) {
				liveMap.zoomOut();
			}
		}));

		buttons.addComponent(createButton("Flat", "Flat view" , 2, new ButtonListener() {

			@Override
			public void buttonPressed(WButton p0) {
			}

			@Override
			public void buttonClicked(WButton p0) {
				liveMap.setRenderer(MapLayer.SURFACE, RenderType.FLAT);
			}
		}));

		buttons.addComponent(createButton("3D", "Pseudo 3D view" , 3, new ButtonListener() {

			@Override
			public void buttonPressed(WButton p0) {
			}

			@Override
			public void buttonClicked(WButton p0) {
				liveMap.setRenderer(MapLayer.SURFACE, RenderType.ISOMETRIC);
			}
		}));

		buttons.addComponent(createButton("Topo", "Topographic view" , 4, new ButtonListener() {

			@Override
			public void buttonPressed(WButton p0) {
			}

			@Override
			public void buttonClicked(WButton p0) {
				liveMap.setRenderer(MapLayer.SURFACE, RenderType.TOPOGRAPHIC);
			}
		}));
		
		buttons.addComponent(createButton( "Deed", "Toggle Deeds" , 5, new ButtonListener() 
		{

			@Override
			public void buttonPressed( WButton p0 ) 
			{
			}

			@Override
			public void buttonClicked( WButton p0 ) 
			{
				mDeedData.setShowDeeds( !mDeedData.getShowDeed() );
			}
		} ) );
		
		buttons.addComponent(createButton( "SER", "Change Server (Lib/Nov)" , 6, new ButtonListener() 
		{

			@Override
			public void buttonPressed( WButton p0 ) 
			{
			}

			@Override
			public void buttonClicked( WButton p0 ) 
			{
				changeServer();
			}
		} ) );



		liveMapView = new LiveMapView("Live map", liveMap, 256, 256);

		mainPanel.setComponent(liveMapView, WurmBorderPanel.WEST);
		mainPanel.setComponent(buttons, WurmBorderPanel.EAST);

		setComponent(mainPanel);
		setInitialSize(256 + 6 + 32, 256 + 25, false);
		layout();
		sizeFlags = FlexComponent.FIXED_WIDTH | FlexComponent.FIXED_HEIGHT;
	}

	private WButton createButton(String label, String tooltip, int textureIndex, ButtonListener listener) 
	{	
		final String themeName = Options.guiSkins.options[Options.guiSkins.value()].toLowerCase(Locale.ENGLISH).replace(" ", "");
		final ResourceTexture backgroundTexture = ResourceTextureLoader.getTexture("img.gui.button.mainmenu." + themeName);
		return new WTextureButton(label, tooltip, backgroundTexture, listener);
	}

	public void closePressed()
	{
		hud.toggleComponent(this);
	}

	public void toggle() {
		hud.toggleComponent(this);
	}
	
	public void pick(final PickData pickData, final int xMouse, final int yMouse) {
		if (this.liveMapView.contains(xMouse, yMouse)) {
			this.liveMap.pick(pickData, 1.0f * (xMouse - this.liveMapView.x) / this.liveMapView.width, 1.0f * (yMouse - this.liveMapView.y) / this.liveMapView.width);
		}
	}

	public HeadsUpDisplay getHud() {
		return hud;
	}
	
	public DeedData getDeedData()
	{
		return mDeedData;
	}
	
	public void changeServer()
	{
		if ( mServerShortcut.contains( "Lib" ) )
		{
			mDeedData.setJsonServer( mDeedData.getNovusPath() );
			mDeedData.refreshMap();
			mServerShortcut = "Nov";
		}
		else if ( mServerShortcut.contains( "Nov" ) )
		{
			mDeedData.setJsonServer( mDeedData.getLibertyPath() );
			mDeedData.refreshMap();
			mServerShortcut = "Lib";
		}
	}
	
	public void setServerShortcut( String pServer )
	{
		mServerShortcut = pServer;
	}
}
