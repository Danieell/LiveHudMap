package com.wurmonline.client.renderer.gui;

import org.gotti.wurmonline.clientmods.livehudmap.LiveMap;
import org.lwjgl.opengl.GL11;

import com.wurmonline.client.game.World;

public class LiveMapWindow extends WWindow {
	
	private WurmBorderPanel mainPanel;
	private final int windowSizeWidth = 256;
	private final int windowSizeHeight = 256;
	private final LiveMap liveMap;

	public LiveMapWindow(World world) {
		super("Live map", true);
		setTitle("Live map");
		resizable = false;
		mainPanel = new WurmBorderPanel("Live map");
		
		setComponent(mainPanel);
		
		setInitialSize(windowSizeWidth + 6, windowSizeHeight + 25, false);
		
		liveMap = new LiveMap(world, windowSizeWidth);
	}
	
	protected void renderComponent(float alpha) {
		super.renderComponent(alpha);
		
		liveMap.update(x, y);
				
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(r, g, b, 1.0F);

		liveMap.render(0.0F, 0.0F, 1.0F);
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	public void closePressed()
	{
		hud.toggleComponent(this);
	}

	public void toggle() {
		hud.toggleComponent(this);
	}
}
