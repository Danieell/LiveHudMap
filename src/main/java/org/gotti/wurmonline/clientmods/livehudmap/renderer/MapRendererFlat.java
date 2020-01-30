package org.gotti.wurmonline.clientmods.livehudmap.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import org.gotti.wurmonline.clientmods.livehudmap.DeedData;

import com.wurmonline.client.game.NearTerrainDataBuffer;
import com.wurmonline.mesh.Tiles.Tile;

public class MapRendererFlat extends AbstractSurfaceRenderer {
	final Logger logger = Logger.getLogger( MapRendererFlat.class.getName() );
	
	public MapRendererFlat(NearTerrainDataBuffer buffer) {
		super(buffer);
	}
	
	@Override
	public BufferedImage createMapDump(int xo, int yo, int lWidth, int lHeight, int px, int py) {
		if (yo < 0)
			yo = 0;
		if (xo < 0)
			xo = 0;

		final BufferedImage bi2 = new BufferedImage(lWidth, lWidth, BufferedImage.TYPE_INT_RGB);
		final float[] data = new float[lWidth * lWidth * 3];

		for (int x = 0; x < lWidth; x++) {
			for (int y = lWidth - 1; y >= 0; y--) {
				final short height = getSurfaceHeight(x + xo, y + yo);
				
				final int tx = x + xo;
				final int ty = y + yo;
				
				final Tile tile = getTileType(tx, ty);

				final Color color;
				if (tile != null) {
					color = tile.getColor();
				}
				else {
					color = Tile.TILE_DIRT.getColor();
				}
				int r = color.getRed();
				int g = color.getGreen();
				int b = color.getBlue();

				if ( DeedData.mShowDeeds )
				{
					if ( DeedData.mMap[tx][ty] == (byte)1 )
					{
						if ( g <= 216 )
						{
							g = g + 40;
						}
						else
						{
							g = 256;
						}
					}
					else if ( DeedData.mMap[tx][ty] == (byte)2 )
					{
						if ( r <= 216 )
						{
							r = r + 40;
						}
						else
						{
							r = 256;
						}
					}
				}
					
				if (height < 0) {
					r = (int) (r * 0.2f + 0.4f * 0.4f * 256f);
					g = (int) (g * 0.2f + 0.5f * 0.4f * 256f);
					b = (int) (b * 0.2f + 1.0f * 0.4f * 256f);
				}
				
				if (px == x + xo && py == y + yo) {
					r = Color.RED.getRed();
					g = 0;
					b = 0;
				}

				data[(x + y * lWidth) * 3 + 0] = r;
				data[(x + y * lWidth) * 3 + 1] = g;
				data[(x + y * lWidth) * 3 + 2] = b;
			}
		}

		bi2.getRaster().setPixels(0, 0, lWidth, lWidth, data);
		return bi2;
	}
}
